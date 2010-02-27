package engine.utils;

import java.util.LinkedList;

import org.lwjgl.util.vector.Vector2f;

public class NormalCreator {
    public static Vector2f findNormal(Vector2f a, Vector2f b) {
        Vector2f norm = new Vector2f(-b.y + a.y, b.x - a.x);
        norm.normalise();
        return norm;
    }
    
    public static Vector2f findNormal(LinkedList<Line> lines) {
        Vector2f tmp = new Vector2f();
        
        for (Line l: lines) {
            Vector2f lineNormal = findNormal(l.start, l.end);
            tmp.x += lineNormal.x;
            tmp.y += lineNormal.y;
        }
        
        if (tmp.x == 0 && tmp.y == 0) return tmp;
        else return (Vector2f) tmp.normalise();
    }
}
