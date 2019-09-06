package pt.ist.fenix.webapp;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.idcards.domain.SantanderCardState;
import org.fenixedu.idcards.domain.SantanderEntry;
import pt.ist.fenixframework.FenixFramework;

public class FixIgnoredTask extends CustomTask {

    @Override
    public void runTask() {
        User user = User.findByUsername("ist422945");
        FenixFramework.atomic(() -> {
            SantanderEntry entry = user.getCurrentSantanderEntry();
            entry.getSantanderCardInfo().getSantanderCardStateTransitionsSet().stream()
                    .filter(t -> SantanderCardState.IGNORED.equals(t.getState()))
                    .findFirst().ifPresent(t -> t.setSantanderCard(null));

        });
    }
}
