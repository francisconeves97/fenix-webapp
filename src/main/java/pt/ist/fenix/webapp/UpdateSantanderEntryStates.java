package pt.ist.fenix.webapp;

import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import org.fenixedu.academic.domain.PersonAccount;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.idcards.domain.SantanderEntryState;
import org.fenixedu.idcards.service.SantanderRequestCardService;
import pt.ist.fenixframework.Atomic;

public class UpdateSantanderEntryStates extends CronTask {
    private static final Logger logger = LoggerFactory.getLogger(UpdateSantanderEntryStates.class);

    @Override
    public Atomic.TxMode getTxMode() {
        return Atomic.TxMode.WRITE;
    }

    @Override
    public void runTask() {
        Bennu.getInstance().getPersonAccountsSet().stream()
                .map(PersonAccount::getPerson)
                .filter(p -> p.getCurrentSantanderEntry() == null || p.getCurrentSantanderEntry().getState().equals(SantanderEntryState.IN_PRODUCTION))
                .forEach(p -> {
                    String state = SantanderRequestCardService.getRegister(p);
                    if (state.equals("Expedido")) {
                        // TODO: notificate user?
                        p.getCurrentSantanderEntry().setState(SantanderEntryState.ISSUED);
                    }
                });
    }
}
