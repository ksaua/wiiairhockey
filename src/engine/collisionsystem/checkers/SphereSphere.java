package engine.collisionsystem.checkers;

import engine.collisionsystem.BoundingSphere;
import engine.collisionsystem.CollisionBounds;
import engine.collisionsystem.CollideableEntity;

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
	public boolean collides(CollideableEntity a, CollideableEntity b) {
		return false; 
	}



}
