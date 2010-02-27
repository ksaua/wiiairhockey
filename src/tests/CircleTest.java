package tests;

public class CircleTest {
    public static void main(String[] args) {
        float circx = 0;
        float circy = 0;
        float radius = 4;
        
        float p1x = -5;
        float p1y = 0;
        
        float p2x = 5;
        float p2y = 1;
        
        
        float asquared = (circx - p2x) * (circx - p2x) + (circy - p2y) * (circy - p2y); 
        float bsquared = (circx - p1x) * (circx - p1x) + (circy - p1y) * (circy - p1y);
        float csquared = (  p1x - p2x) * (  p1x - p2x) + (  p1y - p2y) * (  p1y - p2y);
        
        float b = (float) Math.sqrt(bsquared); 
        float c = (float) Math.sqrt(csquared);
        
        float vinkel = (float) Math.acos((bsquared + csquared - asquared) /  (2 * b * c));
        System.out.println(Math.toDegrees(vinkel));
        float d = (float) Math.sin(vinkel) * c;
        
        
        
        System.out.println(d);
    }
}
