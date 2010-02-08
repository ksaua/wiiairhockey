package engine.collisionsystem;

import engine.Entity;

public interface CollisionHandler {
	public void collisionOccured(CollideableEntity obj1, CollideableEntity obj2);
}
