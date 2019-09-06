package pt.ist.fenix.webapp;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.bennu.spring.BennuSpringContextHelper;
import org.fenixedu.idcards.domain.SantanderEntry;
import org.fenixedu.idcards.domain.SantanderUser;
import org.fenixedu.idcards.service.IUserInfoService;
import org.fenixedu.idcards.service.SantanderIdCardsService;
import org.fenixedu.santandersdk.dto.CardPreviewBean;
import org.fenixedu.santandersdk.dto.CreateRegisterRequest;
import org.fenixedu.santandersdk.dto.CreateRegisterResponse;
import org.fenixedu.santandersdk.dto.RegisterAction;
import org.fenixedu.santandersdk.exception.SantanderValidationException;
import org.fenixedu.santandersdk.service.SantanderSdkService;
import pt.ist.fenixframework.FenixFramework;

public class CreateRegisterA extends CustomTask {

    @Override
    public void runTask() {
        User user = User.findByUsername("ist422945");
        SantanderSdkService sdkService = BennuSpringContextHelper.getBean(SantanderSdkService.class);
        IUserInfoService userInfoService = BennuSpringContextHelper.getBean(IUserInfoService.class);

        FenixFramework.atomic(() -> {
            try {
                SantanderUser santanderUser = new SantanderUser(user, userInfoService);
                CreateRegisterRequest createRegisterRequest = santanderUser.toCreateRegisterRequest(RegisterAction.NOVO);
                CardPreviewBean cardPreviewBean = sdkService.generateCardRequest(createRegisterRequest);
                CreateRegisterResponse response = sdkService.createRegister(cardPreviewBean);
                if (response.wasRegisterSuccessful()) {
                    taskLog("Created successfully");
                } else {
                    taskLog("Failed creating %s%n", response.getErrorDescription());
                }
            } catch (SantanderValidationException e) {
                taskLog("Failed sending request. %s", e.getLocalizedMessage());
            }
        });
    }
}
