package engine.utils;

import java.util.LinkedList;

import org.lwjgl.util.Timer;

public class SmoothTimer extends Timer {

    private int smoothamount;
    private LinkedList<Float> times;
    
    public SmoothTimer(int smoothamount) {
        this.times = new LinkedList<Float>();
        this.smoothamount = smoothamount;
    }
    
    public void pushTime(float time) {
        times.addFirst(time);
        while (times.size() > smoothamount + 1) {
            times.removeLast();
        }
    }
    
    public float getDelta() {
        return (times.getFirst() - times.getLast()) / smoothamount;
    }
    
    public static void main(String[] args) {
        SmoothTimer st = new SmoothTimer(5);
        
        for (float i = 0; i < 50; i+=0.1f) {
            st.pushTime(i); System.out.println(st.getDelta());
        }
    }
}
