import java.lang.reflect.Field;

public class BeanDispatcherImpl implements BeanDispatcher {
    private Bean1 bean1;
    private Bean2 bean2;
    private Bean3 bean3;
    // ...

    public BeanDispatcherImpl() throws IllegalAccessException, NoSuchFieldException {
        bean1 = new Bean1();
        bean2 = new Bean2();
        bean3 = new Bean3();
        // ...

        for (Field bean : this.getClass().getDeclaredFields()) {
            for (Field field : bean.getType().getDeclaredFields()) {
                field.set(bean.get(this), this.getClass().getDeclaredField(field.getName()).get(this));
            }
        }
    }

    public Bean1 getBean1() { return bean1; }
    public Bean2 getBean2() { return bean2; }
    public Bean3 getBean3() { return bean3; }
    // ...
}
