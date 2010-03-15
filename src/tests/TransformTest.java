package tests;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;


public class TransformTest {
    public static void main(String[] args) {
        Point2D pointb = new Point2D.Double(1, 1);
        Point2D pointa = new Point2D.Double(-1, -1);
        
        float phi = (float) Math.atan2(pointb.getY() - pointa.getY(), pointb.getX() - pointa.getX());
        System.out.println(Math.toDegrees(phi));   
        
        AffineTransform af = new AffineTransform();
        af.setToIdentity();
        af.rotate(-phi);
        Point2D newPoint = af.transform(pointb, null);
        System.out.println(newPoint);   
        
    }
}
