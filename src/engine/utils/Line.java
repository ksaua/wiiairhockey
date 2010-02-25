package engine.utils;

import org.lwjgl.util.vector.Vector2f;

public class Line {
    public Vector2f start;
    public Vector2f end;
    
    public Line(Vector2f s, Vector2f e) {
        start = s; end = e;
    }
}
