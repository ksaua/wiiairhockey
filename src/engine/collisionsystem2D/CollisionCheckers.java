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
                    if (lineIntersection(line, line2) != null) {
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

    private static Vector2f lineIntersection(Line a, Line b) {
        // http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/
        float x1 = a.start.x;
        float x2 = a.end.x;
        float x3 = b.start.x;
        float x4 = b.end.x;

        float y1 = a.start.y;
        float y2 = a.end.y;
        float y3 = b.start.y;
        float y4 = b.end.y;

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

    public static void main(String[] args) {
        System.out.println(lineIntersection(new Line(new Vector2f( 2, -2), new Vector2f(-2, 2)), new Line(new Vector2f( 3, -3), new Vector2f(-3, 3))));
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
