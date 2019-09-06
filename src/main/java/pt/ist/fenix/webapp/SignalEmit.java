package pt.ist.fenix.webapp;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import pt.ist.fenixframework.FenixFramework;

public class SignalEmit extends CustomTask {
    @Override
    public void runTask() throws Exception {
        Signal.emit(Registration.REGISTRATION_PROCESS_COMPLETE, FenixFramework.getDomainObject("846731327572257"));
    }
}
