package pt.ist.fenix.webapp;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.bennu.spring.BennuSpringContextHelper;
import org.fenixedu.idcards.domain.SantanderUser;
import org.fenixedu.idcards.service.IUserInfoService;
import org.fenixedu.santandersdk.dto.CardPreviewBean;
import org.fenixedu.santandersdk.dto.CreateRegisterRequest;
import org.fenixedu.santandersdk.dto.CreateRegisterResponse;
import org.fenixedu.santandersdk.dto.RegisterAction;
import org.fenixedu.santandersdk.service.SantanderSdkService;

public class AtuaForUserTask extends CustomTask {
    @Override
    public void runTask() throws Exception {
        User user = User.findByUsername("ist424809");
        SantanderSdkService sdkService = BennuSpringContextHelper.getBean(SantanderSdkService.class);
        IUserInfoService userInfoService = BennuSpringContextHelper.getBean(IUserInfoService.class);

        SantanderUser santanderUser = new SantanderUser(user, userInfoService);
        CreateRegisterRequest createRegisterRequest = santanderUser.toCreateRegisterRequest(RegisterAction.ATUA);
        CardPreviewBean cardPreviewBean = sdkService.generateCardRequest(createRegisterRequest);
        CreateRegisterResponse response = sdkService.createRegister(cardPreviewBean);

        taskLog("Sending line %s%n", cardPreviewBean.getRequestLine().trim());

        if (response.wasRegisterSuccessful()) {
            taskLog("Register was created successfully");
        } else {
            taskLog("Register failed! %s%n", response.getErrorDescription());
        }
    }
}
