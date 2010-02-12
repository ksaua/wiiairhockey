package engine.collisionsystem2D;

import engine.Entity;

public class BoundingCircle implements Bounds {
    private Entity entity;
    private float radius;

    public BoundingCircle(Entity entity, float radius) {
        this.entity = entity;
        this.radius = radius;
    }    
    
    public Entity getEntity() {
        return entity;
    }
    
    public float getRadius() {
        return radius;
    }

    @Override
    public void updateVertices() { }
}
