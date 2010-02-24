package engine.utils;

import org.lwjgl.util.vector.Vector2f;

public class NormalCreator {
    public static Vector2f findNormal(Vector2f a, Vector2f b) {
        Vector2f norm = new Vector2f(b.y - a.y, b.x - a.x);
        norm.normalise();
        return norm;
    }
}
