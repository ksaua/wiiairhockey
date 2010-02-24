package engine.collisionsystem2D;

import org.lwjgl.util.vector.Vector2f;

import engine.Entity;

public class CollisionResponse {

    private Entity entity1;
    private Vector2f normal1;
    
    private Entity entity2;
    private Vector2f normal2;
    
    public CollisionResponse(Entity a, Vector2f normal12, Entity b, Vector2f normal22) {
        this.entity1 = a;
        this.normal1 = normal12;
        this.entity2 = b;
        this.normal2 = normal22;
    }
    
    public Entity getEntity1() {
        return entity1;
    }

    public Vector2f getNormal1() {
        return normal1;
    }

    public Entity getEntity2() {
        return entity2;
    }

    public Vector2f getNormal2() {
        return normal2;
    }
    
    @Override
    public String toString() {
        return "CollisionResponse [entity1=" + entity1 + ", entity2=" + entity2
                + ", normal1=" + normal1 + ", normal2=" + normal2 + "]";
    }
}
