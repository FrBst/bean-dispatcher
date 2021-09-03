public class Bean2 implements IBean2 {
    @Qualifier("q4")
    IBean1 bean1;
    @Qualifier("q4")
    IBean1 beanDupl;
    IBean3 bean3;
}
