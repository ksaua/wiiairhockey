package engine.collisionsystem2D;

import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.util.vector.Vector2f;

import engine.utils.Line;
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

    
    public static CollisionCheckerResponse collides(BoundingCircle circle, BoundingBox box) {
        for (Line line: box.getLines()) {
            Vector2f pos = new Vector2f(circle.getEntity().getPos().x, circle.getEntity().getPos().z);
            if (lineIntersectsCircle(line, circle.getRadius(), pos)) {
                return new CollisionCheckerResponse(true, null, NormalCreator.findNormal(line.end, line.start));
            }
        }
        return new CollisionCheckerResponse(false, null, null);
    }
    
    public static CollisionCheckerResponse collides(BoundingBox box, BoundingCircle circle) {
        CollisionCheckerResponse ccr = collides(circle, box);
        Vector2f temp = ccr.normal1;
        ccr.normal1 = ccr.normal2;
        ccr.normal2 = temp;
        return ccr;
    }
    

    public static CollisionCheckerResponse collides(BoundingBox box1, BoundingBox box2) {
        Vector2f n1 = findCollisionNormal(box1, box2);
        
        // No need to check both if one is zero. Though this makes it be false when one object is completely inside another.
        if (n1 == null) return new CollisionCheckerResponse(false, null, null);
        
        Vector2f n2 = findCollisionNormal(box2, box1);
        return new CollisionCheckerResponse(true, n1, n2); 
    }

    private static Vector2f findCollisionNormal(BoundingBox box1, BoundingBox box2) {
        
        // Find out what vertices is inside for faster reference later
        HashMap<Vector2f, Boolean> verticesInside = new HashMap<Vector2f, Boolean>();
        for (Vector2f v: box1.getVertices()) {
            if (polygonContainsVertex(box2.getVertices(), v))
                verticesInside.put(v, true);
            else 
                verticesInside.put(v, false);
        }


        LinkedList<Line> linesInside = new LinkedList<Line>();

        for (Line line: box1.getLines()) {

            // If box2 contains line: Normal is this line's normal
            if (verticesInside.get(line.start) && verticesInside.get(line.end)) {
                return NormalCreator.findNormal(line.start, line.end);
            }

            // If one of the points is inside, add it to a list.
            else if (verticesInside.get(line.start) || verticesInside.get(line.end)) {
                linesInside.add(line);
            }
            
            // The line might still intersect even thought it's not inside.
            else {
                for (Line line2: box2.getLines()) {
                    if (Line.lineIntersection(line, line2) != null) {
                        linesInside.add(line);
                    }
                }
            }

        }
        
        if (linesInside.size() == 0) return null;

        return NormalCreator.findNormal(linesInside);

    }

    public static CollisionCheckerResponse collides(BoundingCircle bounds, BoundingCircle bounds2) {
        float dx = bounds.getEntity().getPos().x - bounds2.getEntity().getPos().x;
        float dy = bounds.getEntity().getPos().y - bounds2.getEntity().getPos().y;

        // To avoid using square root, just square the radiuses instead and check that
        float distance = dx * dx + dy * dy;
        float distance2 = (float) Math.pow(bounds.getRadius() + bounds2.getRadius(), 2);
        return new CollisionCheckerResponse(distance < distance2, null, null);
    }

    private static boolean lineIntersectsCircle(Line line, float radius, Vector2f pos) {
        
        // Check too see if they are even close
        float minx = Math.min(line.start.x, line.end.x) - radius;
        float maxx = Math.max(line.start.x, line.end.x) + radius;
        
        float miny = Math.min(line.start.y, line.end.y) - radius;
        float maxy = Math.max(line.start.y, line.end.y) + radius;
        
        if (!(minx < pos.x && pos.x < maxx && miny < pos.y && pos.y < maxy))
            return false;
        
        // Find the distance and see if it is less than radius
        Vector2f normal = NormalCreator.findNormal(line.start, line.end);
        
        Vector2f intersection =  Line.lineIntersection(
                line.start.x, line.start.y,
                line.end.x, line.end.y,
                pos.x + normal.x * radius, pos.y + normal.y * radius,
                pos.x - normal.x * radius, pos.y - normal.y * radius);
        
        float radiussq = radius * radius;
        
        return intersection != null || distanceSquared(pos, line.start) < radiussq || distanceSquared(pos, line.end) < radiussq;
        
    }
    
    private static float distanceSquared(Vector2f a, Vector2f b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);  
    }

    private static boolean polygonContainsVertex(Vector2f[] vertices, Vector2f pos) {
        boolean inside = false;
        Vector2f previous = vertices[vertices.length - 1];
        for (Vector2f current: vertices) {
            if (current.getY() > pos.getY() != previous.getY() > pos.getY()) {
                if (current.getX() + (pos.getY() - current.getY()) / (previous.getY() - current.getY()) * (previous.getX() - current.getX()) < pos.getX()) {
                    inside = !inside;
                }
            }
            previous = current;
        }
        return inside;
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
