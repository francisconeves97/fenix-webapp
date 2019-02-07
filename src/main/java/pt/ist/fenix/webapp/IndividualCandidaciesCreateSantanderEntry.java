package pt.ist.fenix.webapp;

import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.CronTask;
import pt.ist.fenixedu.integration.ui.spring.santander.service.IdentificationCardService;

public class IndividualCandidaciesCreateSantanderEntry extends CronTask {

    private static final Logger logger = LoggerFactory.getLogger(IndividualCandidaciesCreateSantanderEntry.class);

    private static final String ACTION_NEW = "NOVO";

    @Override
    public void runTask() {
        Bennu.getInstance().getIndividualCandidaciesSet()
                .forEach(c -> {
                    Person person = c.getRegistration().getPerson();
                    IdentificationCardService.createRegister(person, ExecutionYear.readCurrentExecutionYear(), ACTION_NEW);

                    taskLog("Creating santander card entry for %s", person.getUsername());
                });
    }
}
