package pt.ist.fenix.webapp;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.cms.domain.RoleTemplate;
import org.fenixedu.cms.domain.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixedu.cmscomponents.domain.homepage.HomepageSiteBuilder;
import pt.ist.fenixframework.Atomic;

public class FixHomepagesTask extends CustomTask {

    private static final Logger logger = LoggerFactory.getLogger(FixHomepagesTask.class);

    @Override
    public Atomic.TxMode getTxMode() {
        return Atomic.TxMode.WRITE;
    }

    @Override
    public void runTask() {
        boolean published = HomepageSiteBuilder.getInstance().getPublished();
        Group canViewGroup = HomepageSiteBuilder.getInstance().getCanViewGroup();
        RoleTemplate roleTemplate = HomepageSiteBuilder.getInstance().getDefaultRoleTemplate();

        Bennu.getInstance().getSitesSet().stream()
                .filter(s -> s.getHomepageSite() != null)
                .forEach(s -> {
                    s.setPublished(published);
                    s.setCanViewGroup(canViewGroup);
                    s.setDefaultRoleTemplate(roleTemplate);
                });
    }
}
