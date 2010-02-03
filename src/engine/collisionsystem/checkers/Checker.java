package engine.collisionsystem.checkers;

import engine.Entity;
import engine.collisionsystem.CollisionBounds;

public interface Checker {
	public Class<? extends CollisionBounds> getAcceptedClass1();
	public Class<? extends CollisionBounds> getAcceptedClass2();
	public boolean collides(Entity a, Entity b);
}
