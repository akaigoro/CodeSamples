package javatalks;

public class ClassWithMethods {
    public void A1() { System.out.println("A1") ; }
    public void A2() { System.out.println("A2") ;}
    public void A3() {System.out.println("A3") ;}
    public void A4() {System.out.println("A4") ;}

    public interface MoveAction {
        void move();
    }

    private MoveAction[] moveActions = new MoveAction[] {
            new MoveAction() { public void move() {A1(); } },
            () -> A2(),
            () -> { System.out.println("A3") ;},
            this::A4,
    };

    public void move(int index) {
        moveActions[index].move();
    }

    public static void main(String[] a) {
        ClassWithMethods inst = new ClassWithMethods();
        for (int k=0; k<4; k++) {
            inst.move(k);
        }
    }
}
