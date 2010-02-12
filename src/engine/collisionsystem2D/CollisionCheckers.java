package engine.collisionsystem2D;

import org.lwjgl.util.vector.Vector2f;

public class CollisionCheckers {
    public static boolean collides(BoundingBox b1, BoundingBox b2) {
        for (Vector2f v: b1.getVertices()) {
            if (boxContainsVertex(b2, v))
                return true;
        }
        for (Vector2f v: b2.getVertices()) {
            if (boxContainsVertex(b1, v))
                return true;
        }
        return false;
    }
    
    public static boolean collides(BoundingCircle bounds, BoundingCircle bounds2) {
        float dx = bounds.getEntity().getPos().x - bounds2.getEntity().getPos().x;
        float dy = bounds.getEntity().getPos().y - bounds2.getEntity().getPos().y;
        
        // To avoid using square root, just square the radiuses instead and check that
        float distance = dx * dx + dy * dy;
        float distance2 = (float) Math.pow(bounds.getRadius() + bounds2.getRadius(), 2);
        return distance < distance2;
    }
    
    private static boolean boxContainsVertex(BoundingBox box, Vector2f vertex) {
        
        Vector2f[] vertices = box.getVertices();
        
        Vector2f last = box.getVertices()[vertices.length - 1];
        
        // (last.x <= vertex.x <= current.x || last.x >= verte.x >= current.x) &&
        // (last.y <= vertex.y <= current.y || last.y >= verte.y >= current.y)
        
        for (Vector2f current: vertices) {
            if (((last.x <= vertex.x && vertex.x <= current.x) || (last.x >= vertex.x && vertex.x >= current.x)) &&
                ((last.y <= vertex.y && vertex.y <= current.y) || (last.y >= vertex.y && vertex.y >= current.y))) {
                return true;
            }
            last = current;
        }
        
        return false;
        
    }
}
