package pt.ist.fenix.webapp;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import pt.ist.fenixframework.FenixFramework;

public class UpdateTeacherAuthorization extends CustomTask {
    @Override
    public void runTask() throws Exception {
        User user = User.findByUsername("ist12048");
        user.getPerson().getTeacher().getTeacherAuthorization().get().setCampus(FenixFramework.getDomainObject("2448131360898"));
    }
}
