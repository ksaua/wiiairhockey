package engine.collisionsystem2D;

import org.lwjgl.util.vector.Vector2f;

import engine.utils.NormalCreator;

public class CollisionCheckers {

    /**
     * This is returned from the collision checkers
     */
    public static class CollisionCheckerResponse {
        public boolean collided;
        public Vector2f normal1;
        public Vector2f normal2;

        public CollisionCheckerResponse(boolean c, Vector2f n1, Vector2f n2) {
            collided = c; normal1 = n1; normal2 = n2;
        }
    }

    public static CollisionCheckerResponse collides(BoundingBox b1, BoundingBox b2) {
        for (Vector2f v: b1.getVertices()) {
            Vector2f normal = polygonContainsVertex(b2.getVertices(), v);
            if (normal != null)
                return new CollisionCheckerResponse(true, null, normal);
        }
        for (Vector2f v: b2.getVertices()) {
            Vector2f normal = polygonContainsVertex(b1.getVertices(), v);
            if (normal != null)
                return new CollisionCheckerResponse(true, normal, null);
        }
        return new CollisionCheckerResponse(false, null, null);
    }

    public static CollisionCheckerResponse collides(BoundingCircle bounds, BoundingCircle bounds2) {
        float dx = bounds.getEntity().getPos().x - bounds2.getEntity().getPos().x;
        float dy = bounds.getEntity().getPos().y - bounds2.getEntity().getPos().y;

        // To avoid using square root, just square the radiuses instead and check that
        float distance = dx * dx + dy * dy;
        float distance2 = (float) Math.pow(bounds.getRadius() + bounds2.getRadius(), 2);
        return new CollisionCheckerResponse(distance < distance2, null, null);
    }


    private static Vector2f polygonContainsVertex(Vector2f[] vertices, Vector2f pos) {
        boolean inside = false;
        Vector2f previous = vertices[vertices.length - 1];
        Vector2f normal = null;
        for (Vector2f current: vertices) {
            if (current.getY() > pos.getY() != previous.getY() > pos.getY()) {
                if (current.getX() + (pos.getY() - current.getY()) / (previous.getY() - current.getY()) * (previous.getX() - current.getX()) < pos.getX()) {
                    normal = NormalCreator.findNormal(current, previous);
                    inside = !inside;
                }
            }
            previous = current;
        }
        if (inside)
            return normal; 
        else
            return null;
    }

    /**
     * 
     * @param box
     * @param vertex
     * @return The normal (if found)
     */
    private static Vector2f boxContainsVertex(BoundingBox box, Vector2f vertex) {

        Vector2f[] vertices = box.getVertices();

        Vector2f last = box.getVertices()[vertices.length - 1];

        // (last.x <= vertex.x <= current.x || last.x >= verte.x >= current.x) &&
        // (last.y <= vertex.y <= current.y || last.y >= verte.y >= current.y)

        for (Vector2f current: vertices) {
            if (((last.x <= vertex.x && vertex.x <= current.x) || (last.x >= vertex.x && vertex.x >= current.x)) &&
                    ((last.y <= vertex.y && vertex.y <= current.y) || (last.y >= vertex.y && vertex.y >= current.y))) {
                return NormalCreator.findNormal(current, last);
            }
            last = current;
        }

        return null;        
    }
}
