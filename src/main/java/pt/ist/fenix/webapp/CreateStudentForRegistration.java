package pt.ist.fenix.webapp;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

public class CreateStudentForRegistration extends CustomTask {
    @Override
    public void runTask() throws Exception {
        Bennu.getInstance().getRegistrationsSet().stream()
                .filter(r -> r.getBennuCompletedRegistration() == null)
                .findAny()
                .ifPresent(r -> {
                    taskLog(r.getStudent().getPerson().getUsername());
                });
    }
}
