package engine.collisionsystem;

import engine.Entity;

public interface CollisionHandler {
	public void collisionOccured(Entity obj1, Entity obj2);
}
