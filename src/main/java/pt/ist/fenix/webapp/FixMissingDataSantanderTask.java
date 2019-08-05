    package pt.ist.fenix.webapp;

    import org.fenixedu.bennu.core.domain.User;
    import org.fenixedu.bennu.scheduler.custom.CustomTask;
    import org.fenixedu.bennu.spring.BennuSpringContextHelper;
    import org.fenixedu.idcards.domain.SantanderEntry;
    import org.fenixedu.idcards.domain.SantanderUser;
    import org.fenixedu.idcards.service.IUserInfoService;
    import org.fenixedu.santandersdk.dto.CardPreviewBean;
    import org.fenixedu.santandersdk.dto.CreateRegisterRequest;
    import org.fenixedu.santandersdk.dto.CreateRegisterResponse;
    import org.fenixedu.santandersdk.dto.RegisterAction;
    import org.fenixedu.santandersdk.exception.SantanderMissingInformationException;
    import org.fenixedu.santandersdk.exception.SantanderValidationException;
    import org.fenixedu.santandersdk.service.SantanderEntryValidator;
    import org.fenixedu.santandersdk.service.SantanderSdkService;

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
                        taskLog("Request %s%n", entry.getRequestLine());
                        taskLog("User %s has entry with <<<<%s>>>> action, created at %s%n", istId, validator.getValue(entry.getRequestLine(), 20), entry.getCreationDate());

                        try {
                            SantanderUser santanderUser = new SantanderUser(user, userInfoService);
                            CreateRegisterRequest createRegisterRequest = santanderUser.toCreateRegisterRequest(RegisterAction.ATUA);
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
