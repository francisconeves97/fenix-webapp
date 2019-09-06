package pt.ist.fenix.webapp;

import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.FenixFramework;

import java.util.Locale;

public class CreateTecnicoCardMenuTask extends CustomTask {

    @Override
    public void runTask() {
        MenuContainer personalArea = FenixFramework.getDomainObject("7391638718512");
        LocalizedString title = new LocalizedString.Builder()
                .with(Locale.forLanguageTag("pt-PT"), "Cartão do Técnico")
                .with(Locale.forLanguageTag("en-GB"), "Técnico Card")
                .build();
        new MenuFunctionality(personalArea, true,
                "https://fenix.tecnico.ulisboa.pt/tecnico-card/", "redirect", "logged",
                title, title, "tecnico-card", null, "_blank");
    }
}
