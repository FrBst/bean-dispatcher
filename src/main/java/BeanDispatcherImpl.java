import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanDispatcherImpl implements BeanDispatcher {
    @Qualifier("q1")
    private Bean1 bean1;
    private Bean2 bean2;
    private Bean3 bean3;

    @Qualifier("q4")
    private Bean1 bean4;
    // ...

    public BeanDispatcherImpl() throws IllegalAccessException, NoSuchFieldException {
        bean1 = new Bean1();
        bean2 = new Bean2();
        bean3 = new Bean3();
        // ...

        bean4 = new Bean1();

        injectDependencies();
    }

    public Bean1 getBean1() { return bean1; }
    public Bean2 getBean2() { return bean2; }
    public Bean3 getBean3() { return bean3; }
    // ...

    private void injectDependencies() throws IllegalAccessException, NoSuchFieldException {
        for (Field bean : this.getClass().getDeclaredFields()) {
            for (Field dep : bean.getType().getDeclaredFields()) {

                // Get all beans that can be assigned to the field.
                Object[] matchingType = Arrays.stream(this.getClass().getDeclaredFields())
                        .filter(b -> dep.getType().isAssignableFrom(b.getType())).toArray();
                // If there is only one bean of matching type, just inject it.
                if (matchingType.length == 1) {
                    dep.set(bean.get(this), ((Field) matchingType[0]).get(this));
                    continue;
                } else if (matchingType.length == 0) {
                    throw new NoSuchFieldException("Could not find a bean to inject into field '" + dep.getName() +
                            "' in class '" + bean.get(this).getClass().getName() + "'.");
                } else if (!dep.isAnnotationPresent(Qualifier.class)) {
                    throw new NoSuchFieldException("More than one bean in Dispatcher matching field '" + dep.getName() +
                            "' in class '" + bean.get(this).getClass().getName() + "'. Use @Qualifier annotation.");
                }

                // Otherwise, try to resolve ambiguity using qualifier.
                String qualifier = dep.getAnnotation(Qualifier.class).value();
                Object[] matchingBeans = Arrays.stream(this.getClass().getDeclaredFields())
                        .filter(b -> b.isAnnotationPresent(Qualifier.class) && b.getAnnotation(Qualifier.class).value().equals(qualifier))
                        .toArray();

                if (matchingBeans.length > 1) {
                    throw new NoSuchFieldException("Dispatcher has beans with duplicate qualifier '" + qualifier + "'.");
                } else if (matchingBeans.length == 0) {
                    throw new NoSuchFieldException("Ambiguous dependency for field '" + dep.getName() + "' in class '"
                            + bean.get(this).getClass().getName() + "'. Add @Qualifier annotation in Dispatcher.");
                }
                dep.set(bean.get(this), ((Field) matchingBeans[0]).get(this));
            }
        }
    }
}
