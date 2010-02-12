package engine.collisionsystem2D;

import engine.Entity;

public interface CollisionHandler {
    public void collisionOccured(Entity a, Entity b);
}
