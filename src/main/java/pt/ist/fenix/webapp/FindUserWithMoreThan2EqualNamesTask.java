package pt.ist.fenix.webapp;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.StringNormalizer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class FindUserWithMoreThan2EqualNamesTask extends CustomTask {

    @Override
    public void runTask() {
        Bennu.getInstance().getUserSet().stream()
                .forEach(u -> {
                    List<String> names = java.util.Arrays
                            .asList(StringNormalizer.normalizeAndRemoveAccents(u.getProfile().getFullName()).toLowerCase().trim().split("\\s+|-"));

                    for(String name: new HashSet<>(names)) {
                        if (Collections.frequency(names, name) > 2) {
                            taskLog("Found user %s with name %s%n", u.getUsername(), u.getProfile().getFullName());
                        }
                    }
                });
    }

    private void changeUserName() {
        User user = User.findByUsername("ist424770");
        try {
            Class<?> clazz = user.getProfile().getClass();
            while (clazz != null) {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().equals("setGivenNames")) {
                        method.setAccessible(true);
                        method.invoke(user.getProfile(), "Francisco Francisco Miguel");
                        taskLog("Username %s", user.getProfile().getGivenNames());
                    }
                }
                clazz = clazz.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            taskLog("IllegalAccessException");
        } catch (InvocationTargetException e) {
            taskLog("InvocationTargetException");
        }
    }
}
