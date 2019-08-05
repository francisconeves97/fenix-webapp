package pt.ist.fenix.webapp;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.bennu.spring.BennuSpringContextHelper;
import org.fenixedu.idcards.domain.PickupLocation;
import org.fenixedu.idcards.domain.SantanderEntry;
import org.fenixedu.idcards.domain.SantanderUser;
import org.fenixedu.idcards.service.IUserInfoService;
import org.fenixedu.santandersdk.dto.CardPreviewBean;
import org.fenixedu.santandersdk.dto.CreateRegisterRequest;
import org.fenixedu.santandersdk.dto.CreateRegisterResponse;
import org.fenixedu.santandersdk.dto.RegisterAction;
import org.fenixedu.santandersdk.exception.SantanderMissingInformationException;
import org.fenixedu.santandersdk.exception.SantanderValidationException;
import org.fenixedu.santandersdk.service.SantanderSdkService;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;

public class CancelEntriesWithNoCardsTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        SantanderSdkService sdkService = BennuSpringContextHelper.getBean(SantanderSdkService.class);
        IUserInfoService userInfoService = BennuSpringContextHelper.getBean(IUserInfoService.class);

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
                if (entry == null) {
                    taskLog("User %s has no entry and should be deleted%n", istId);
                    try {
                        SantanderUser santanderUser = new SantanderUser(user, userInfoService);
                        CreateRegisterRequest createRegisterRequest = new CreateRegisterRequest();

                        createRegisterRequest.setRole(santanderUser.getRole());
                        createRegisterRequest.setPhoto(new BufferedImage(1, 1, 1));
                        createRegisterRequest.setName(user.getDisplayName());
                        createRegisterRequest.setDepartmentAcronym(santanderUser.getDepartmentAcronym());
                        createRegisterRequest.setCampus("Alameda");
                        createRegisterRequest.setUsername(user.getUsername());
                        createRegisterRequest.setAction(RegisterAction.CANC);
                        createRegisterRequest.setPickupAddress(PickupLocation.ALAMEDA_SANTANDER.toPickupAddress());
                        CardPreviewBean cardPreviewBean = sdkService.generateCardRequest(createRegisterRequest);
                        CreateRegisterResponse response = sdkService.createRegister(cardPreviewBean);

                        if (response.wasRegisterSuccessful()) {
                            taskLog("Request for user %s was successful! Response: %s%n", istId, response.getResponseLine());
                        } else {
                            taskLog("Request for user %s failed! Error: %s%n", istId, response.getErrorDescription());
                        }
                    } catch (SantanderMissingInformationException missingInfoException) {
                        taskLog("Missing info for user %s: %s%n", istId, missingInfoException.getMessage());
                    } catch (SantanderValidationException validationException) {
                        taskLog("Validation exception for user %s: %s%n", istId, validationException.getMessage());
                    }
                    taskLog("-----------");
                    sleep();
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
