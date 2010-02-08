package tests;

import engine.Entity;
import engine.collisionsystem.BoundingBox;
import engine.collisionsystem.CollideableEntity;
import engine.collisionsystem.CollisionChecker;
import engine.collisionsystem.CollisionHandler;

public class CollideableTest {
	public static void main(String[] args) {
		Entity e = new Entity(12, 0, 0);		
		Entity e2 = new Entity(-12, 0, 0);
		
		CollisionChecker cc = new CollisionChecker();
		cc.addCollideableEntity(new CollideableEntity(e, new BoundingBox(20, 20)));
		cc.addCollideableEntity(new CollideableEntity(e2, new BoundingBox(20, 20)));
		
		cc.addCollisionHandler(new CollisionHandler() {
			
			@Override
			public void collisionOccured(CollideableEntity obj1, CollideableEntity obj2) {
				System.out.println("Collided");				
			}
		});
		
		cc.checkForCollision();
		e.move(-6, 0, 0);
		cc.checkForCollision();

		
		
	}
}
