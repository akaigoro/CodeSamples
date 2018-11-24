package so.threads;

import java.util.concurrent.Phaser;

public class WaitCond extends Phaser {
    Phaser p = new Phaser(1);

    public void await() {
        p.arriveAndAwaitAdvance();
    }

    public void enable() {
        p.register();
    }

    public void disable() {
        p.arriveAndDeregister();
    }

    @Override
    public String toString() {
        return "arrived:"+p.getArrivedParties()+"; unarrived:"+p.getUnarrivedParties();
    }

    public static void main(String [] args) throws InterruptedException{
        WaitCond cond = new WaitCond();
        for (int k=0; k<5; k++) {
            cond.await();
        }
        System.out.println("hw");
        new Thread(new Runnable(){

            @Override
            public void run(){
                try {
                    Thread.sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("ok, threads");//NO join，main thread still waits.
            }
        }).start();
        System.out.println("main returms");
    }
}
