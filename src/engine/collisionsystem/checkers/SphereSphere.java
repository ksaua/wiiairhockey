package engine.collisionsystem.checkers;

import engine.Entity;
import engine.collisionsystem.BoundingSphere;
import engine.collisionsystem.CollisionBounds;

public class SphereSphere implements Checker {

	@Override
	public Class<? extends CollisionBounds> getAcceptedClass1() {
		return BoundingSphere.class;
	}

	@Override
	public Class<? extends CollisionBounds> getAcceptedClass2() {
		return BoundingSphere.class;
	}
	
	@Override
	public boolean collides(Entity a, Entity b) {
		return false;
	}



}
