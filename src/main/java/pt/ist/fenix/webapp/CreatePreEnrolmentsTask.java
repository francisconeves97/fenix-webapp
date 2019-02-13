package pt.ist.fenix.webapp;

import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import pt.ist.fenixedu.integration.service.services.externalServices.CreatePreEnrolment;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class CreatePreEnrolmentsTask extends CustomTask {

    private static final Logger logger = LoggerFactory.getLogger(CreatePreEnrolmentsTask.class);

    @Override
    public Atomic.TxMode getTxMode() {
        return Atomic.TxMode.WRITE;
    }

    @Override
    public void runTask() {
        Degree degree = FenixFramework.getDomainObject("2761663971475"); // MEIC-A
        ExecutionSemester executionSemester = ExecutionSemester.readActualExecutionSemester();
        User user = User.findByUsername("ist424770");
        CurricularCourse course = FenixFramework.getDomainObject("1127428915200126"); // Aprendizagem
        CourseGroup group = FenixFramework.getDomainObject("281882998604010"); // Processamento e análise de dados

        CreatePreEnrolment.create(executionSemester, degree, user, course, group);

        taskLog("Created preEnrolment for user %s.%n%s > %s",
                user.getUsername(), group.getName(), course.getName());
    }
}