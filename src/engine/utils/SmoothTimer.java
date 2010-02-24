package engine.utils;

import java.util.LinkedList;

import org.lwjgl.util.Timer;

public class SmoothTimer {

    public static int amount;
    private static LinkedList<Float> times = new LinkedList<Float>();
    private static Timer timer = new Timer();

    //    public SmoothTimer(int smoothamount) {
    //        this.times = new LinkedList<Float>();
    //        this.smoothamount = smoothamount;
    //    }
    //    
    //    public void pushTime(float time) {
    //        times.addFirst(time);
    //        while (times.size() > smoothamount + 1) {
    //            times.removeLast();
    //        }
    //    }
    //    
    //    public float getDelta() {
    //        return (times.getFirst() - times.getLast()) / smoothamount;
    //    }
    
    public static float getTime(int amount) {
        return times.getFirst();
    }
    
    public static float getDelta() {
        return (times.getFirst() - times.getLast()) / amount;
    }

    public static void tick() {
        Timer.tick();
        times.addFirst(timer.getTime());
        while (times.size() > amount + 1) {
            times.removeLast();
        }
    }
}
