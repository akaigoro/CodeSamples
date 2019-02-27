package so.interfacesuper;

public class InterfaceSuper {
    interface IMy {
        default void f1() {}
        default void f3() {
            System.out.println("f3");
        }
        static void sf() {
            System.out.println("f1, f2");
        }
    }

    static class MyImpl implements IMy {
        @Override
        public void f3() {
            f1();
            IMy.super.f3();//works!
//            super.f3(); // doesn't compile!
            System.out.println("f3");
        }
    }
    public static void main(String[] args) {
        new MyImpl().f3();
    }
}
