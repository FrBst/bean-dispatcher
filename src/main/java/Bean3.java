public class Bean3 implements IBean3 {
    @Qualifier("q1")
    IBean1 bean1;
    IBean2 bean2;
}
