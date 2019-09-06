package pt.ist.fenix.webapp;

import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import pt.ist.fenixframework.Atomic;

public class UpdateMenuFunctionalitiesTarget extends CustomTask {

    @Override
    public Atomic.TxMode getTxMode() {
        return Atomic.TxMode.WRITE;
    }

    @Override
    public void runTask() {
        MenuContainer root = PortalConfiguration.getInstance().getMenu();
        updateTargets(root);
    }

    private void updateTargets(MenuContainer container) {
        for (MenuItem child : container.getChildSet()) {
            if (child.isMenuFunctionality()) {
                child.getAsMenuFunctionality().setTarget("_self");
            } else {
                updateTargets(child.getAsMenuContainer());
            }
        }
    }
}
