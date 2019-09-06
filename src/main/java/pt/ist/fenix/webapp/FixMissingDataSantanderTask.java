package pt.ist.fenix.webapp;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.bennu.spring.BennuSpringContextHelper;
import org.fenixedu.idcards.domain.SantanderCardState;
import org.fenixedu.idcards.domain.SantanderEntry;
import org.fenixedu.idcards.domain.SantanderUser;
import org.fenixedu.idcards.service.IUserInfoService;
import org.fenixedu.idcards.service.SantanderIdCardsService;
import org.fenixedu.santandersdk.dto.CardPreviewBean;
import org.fenixedu.santandersdk.dto.CreateRegisterRequest;
import org.fenixedu.santandersdk.dto.CreateRegisterResponse;
import org.fenixedu.santandersdk.dto.RegisterAction;
import org.fenixedu.santandersdk.exception.SantanderMissingInformationException;
import org.fenixedu.santandersdk.exception.SantanderValidationException;
import org.fenixedu.santandersdk.service.SantanderEntryValidator;
import org.fenixedu.santandersdk.service.SantanderSdkService;
import pt.ist.fenixframework.FenixFramework;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class FixMissingDataSantanderTask extends CustomTask {

    @Override
    public void runTask() throws IOException {
        SantanderEntryValidator validator =  new SantanderEntryValidator();
        SantanderSdkService sdkService = BennuSpringContextHelper.getBean(SantanderSdkService.class);
        IUserInfoService userInfoService = BennuSpringContextHelper.getBean(IUserInfoService.class);
        SantanderIdCardsService santanderIdCardsService = BennuSpringContextHelper.getBean(SantanderIdCardsService.class);

        try (BufferedReader br = new BufferedReader(new FileReader("/home/francisconeves/missingDataCards.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String istId = values[4];
                User user = User.findByUsername(istId);

                if (user == null) {
                    taskLog("Couldn't find user %s%n", istId);
                    continue;
                }

                SantanderEntry entry = user.getCurrentSantanderEntry();
                if (entry != null && Arrays.asList("NOVO", "RENU").contains(validator.getValue(entry.getRequestLine(), 20))) {
                    String action = validator.getValue(entry.getRequestLine(), 20);
                    taskLog("Requesting for user %s%n", istId);
                    taskLog("User %s has entry with <<<<%s>>>> action, created at %s%n", istId, validator.getValue(entry.getRequestLine(), 20), entry.getCreationDate());

                    try {
                        SantanderUser santanderUser = new SantanderUser(user, userInfoService);
                        CreateRegisterRequest createRegisterRequest = santanderUser.toCreateRegisterRequest(RegisterAction.CANC);
                        CardPreviewBean cardPreviewBean = sdkService.generateCardRequest(createRegisterRequest);
                        CreateRegisterResponse response = sdkService.createRegister(cardPreviewBean);

                        if (response.wasRegisterSuccessful()) {
                            taskLog("Cancelled card successfully");
                            FenixFramework.atomic(() -> {
                                SantanderEntry previousEntry = entry.getPrevious();
                                entry.setPrevious(null);
                                entry.setUser(null);
                                user.setCurrentSantanderEntry(previousEntry);

                                try {
                                    SantanderEntry newEntry = santanderIdCardsService.createRegister(user, RegisterAction.valueOf(action), "fix task");
                                    santanderIdCardsService.sendRegister(user, newEntry);
                                    taskLog("Created new register for user: %s", istId);
                                } catch (SantanderValidationException e) {
                                    taskLog("Failed creating register for user %s: %s%n", istId, e.getMessage());
                                }
                            });
                        } else {
                            taskLog("Request cancelling user %s failed! Error: %s%n", istId, response.getErrorDescription());
                        }
                    } catch (SantanderMissingInformationException missingInfoException) {
                        taskLog("Missing info for user %s: %s%n", istId, missingInfoException.getMessage());
                    } catch (SantanderValidationException validationException) {
                        taskLog("Validation exception for user %s: %s%n", istId, validationException.getMessage());
                    }
                    sleep();
                    taskLog("-----------");
                }
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
    }
}
