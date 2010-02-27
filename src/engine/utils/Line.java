package engine.utils;

import org.lwjgl.util.vector.Vector2f;

public class Line {
    public Vector2f start;
    public Vector2f end;
    
    public Line(Vector2f s, Vector2f e) {
        start = s; end = e;
    }
    
    
    @Override
    public String toString() {
        return "Line [start=" + start + ", end=" + end + "]";
    }
    
    public static Vector2f lineIntersection(Line a, Line b) {
        return lineIntersection(
                a.start.x, a.start.y,
                a.end.x, a.end.y,
                b.start.x, b.start.y,
                b.end.x, b.end.y);
    }
    
    public static Vector2f lineIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        // http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/
        float denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

        if (denom == 0) return null;

        float ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom; 

        float ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;

        // Not inside the line segment
        if (0 > ua || ua > 1 || 0 > ub || ub > 1 ) return null;

        float x = x1 + ua * (x2 - x1);
        float y = y1 + ua * (y2 - y1);

        return new Vector2f(x, y);
    }
}
