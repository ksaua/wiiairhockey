package engine.collisionsystem;

import java.util.ArrayList;
import java.util.LinkedList;

import engine.Entity;
import engine.collisionsystem.checkers.Checker;
import engine.collisionsystem.checkers.SphereSphere;

public class CollisionChecker {
	public ArrayList<Entity> collideableEntities;
	public LinkedList<CollisionHandler> collisionHandler;
	
	private ArrayList<Checker> checkers = new ArrayList<Checker>() {{
//		add(new SphereSphere());
	}};


	
	public CollisionChecker() {
		collideableEntities = new ArrayList<Entity>();
		collisionHandler = new LinkedList<CollisionHandler>();

	}
	
	public void addCollideableEntity(Entity e) {
		collideableEntities.add(e);
	}
	
	public void addCollisionHandler(CollisionHandler ch) {
		collisionHandler.add(ch);
	}
	
	/**
	 * Goes through every entity and checks if they collide.
	 * Invokes a collisionOccured on the handlers if it does.
	 */
	public void checkForCollision() {
		for (int i = 0; i < collideableEntities.size(); i++) {
			for (int j = i + 1; j < collideableEntities.size(); j++) {
				
				if (collides(collideableEntities.get(i), collideableEntities.get(j))) {
					for (CollisionHandler ch: collisionHandler) 
						ch.collisionOccured(collideableEntities.get(i), collideableEntities.get(j));
				}
				
			}
		}
	}
	


	private boolean collides(Entity a, Entity b) {
		CollisionBounds cb1 = a.getCollisionBounds();
		CollisionBounds cb2 = b.getCollisionBounds();
		
		for (Checker checker: checkers) {
			if (checker.getAcceptedClass1() == cb1.getClass() && 
				checker.getAcceptedClass2() == cb2.getClass()) 
				return checker.collides(a, b);
			else if (checker.getAcceptedClass1() == cb1.getClass() && 
					 checker.getAcceptedClass2() == cb2.getClass())
				return checker.collides(b, a);

				
		}
		

		return false;
	}
}
