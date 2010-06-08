package airhockey;

import java.util.LinkedList;

import motej.IrPoint;

public class WeightedPoint {
    private LinkedList<IrPoint> points;
    
    private float x = 1023.0f;
    private float y = 1023.0f;
    
    public WeightedPoint() {
       points = new LinkedList<IrPoint>();
    }
    
    public float getWeightedX() {
        return x;
    }
    
    public float getWeightedY() {
        return y;
    }
    
    public void pushPoint(IrPoint point) {
        if (points.size() > 2) points.removeLast();
        points.addFirst(point);
        
        float denom = 0;
        for (int i = 1; i < points.size() + 1; i++){
            denom += i;
        }

        x = 0;
        y = 0;
        int i = 0;
        for (IrPoint p: points) {
            x += ((points.size() - i) * p.x) / denom;
            y += ((points.size() - i) * p.y) / denom;
            i++;
        }  
    }
}
