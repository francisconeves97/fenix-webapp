package pt.ist.fenix.webapp;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
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
        Bennu.getInstance().getUserSet().forEach(
                u -> {
                    Person person = u.getPerson();

                    if (person != null) {
                        Site homepageSite = person.getHomepage();

                        if (homepageSite != null) {
                            homepageSite.setPublished(HomepageSiteBuilder.getInstance().getPublished());
                            homepageSite.setCanViewGroup(HomepageSiteBuilder.getInstance().getCanViewGroup());
                            homepageSite.setDefaultRoleTemplate(HomepageSiteBuilder.getInstance().getDefaultRoleTemplate());
                            taskLog("Updating %s website%n", person.getUsername());
                        }
                    }
                }
        );
    }
}
