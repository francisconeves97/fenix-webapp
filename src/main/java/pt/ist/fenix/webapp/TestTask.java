package pt.ist.fenix.webapp;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.bennu.spring.BennuSpringContextHelper;
import org.fenixedu.idcards.domain.SantanderUser;
import org.fenixedu.idcards.service.IUserInfoService;
import org.fenixedu.idcards.service.SantanderIdCardsService;
import org.fenixedu.santandersdk.dto.RegisterAction;

public class TestTask extends CustomTask {

    @Override
    public void runTask() {
        SantanderIdCardsService service = BennuSpringContextHelper.getBean(SantanderIdCardsService.class);
        IUserInfoService userInfoService = BennuSpringContextHelper.getBean(IUserInfoService.class);

        Bennu.getInstance().getUserSet().stream()
                .filter(u -> {
                    SantanderUser santanderUser;
                    try {
                        santanderUser = new SantanderUser(u, userInfoService);
                        santanderUser.toCreateRegisterRequest(RegisterAction.RENU);
                        return u.getCurrentSantanderEntry() == null && service.canRequestCard(u) && santanderUser.getPhoto() != null;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .forEach(u -> taskLog("%s%n", u.getUsername()));
    }
}
