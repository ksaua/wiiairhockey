package engine.collisionsystem2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import engine.Entity;
import engine.collisionsystem2D.CollisionCheckers.CollisionCheckerResponse;
import engine.utils.ChangeListener;
import engine.utils.SubscribeableChanges;

public class Collisionsystem implements ChangeListener {
    
    /**
     * Holds the entities and bounds connected to this collisionsystem
     */
    public static class CollisionEntity {
        Entity entity;
        Bounds bounds;
        boolean dirty; // Has been changed since last check? 
        
//        LinkedList<CollisionEntity> collidesWith;
        
        public CollisionEntity(Entity e, Bounds b) {
//        	collidesWith = new LinkedList<CollisionEntity>();
        	entity = e; bounds = b;
        }
        
//        public boolean didCollideWith(CollisionEntity ce) {
//        	return collidesWith.contains(ce);
//        }
//        
//        public void setCollision(CollisionEntity ce, boolean b) {
//        	if (b) collidesWith.add(ce);
//        	else collidesWith.remove(ce);
//        }
    }
        
//    private HashMap<Entity, CollisionEntity> entities;
    private ArrayList<CollisionEntity> entities;

    private LinkedList<CollisionHandler> collisionHandlers;

    
    public Collisionsystem() {
        entities = new ArrayList<CollisionEntity>();
        collisionHandlers = new LinkedList<CollisionHandler>();
    }
    
    public void addEntity(Bounds bounds) {
        Entity entity = bounds.getEntity();
        entities.add(new CollisionEntity(entity, bounds));
        entity.addSubcription(Entity.POSITION_CHANGED, this);
        entity.addSubcription(Entity.ROTATION_CHANGED, this);
    }

    // Entity changed some parameter
    @Override
    public void change(SubscribeableChanges source, int id) {
        CollisionEntity ce = findCollisionEntity((Entity) source);
        ce.dirty = true;
    }
    
    private CollisionEntity findCollisionEntity(Entity entity) {
        for (CollisionEntity ce: entities) {
            if (ce.entity == entity) return ce;
        }
        return null;
    }
    

    public void addCollisionHandler(CollisionHandler ch) {
        collisionHandlers.add(ch);
    }
    
    public void check() {

        for (int i = 0; i < entities.size(); i++) {
            for (int j = entities.size() - 1; j > i; j--) {
                
                CollisionEntity e1 = entities.get(i);
                CollisionEntity e2 = entities.get(j);
                
                if (e1.dirty || e2.dirty) {
                	
//                	boolean didCollide = e1.didCollideWith(e2) || e2.didCollideWith(e1);
                	
//                    if (entitiesCollides(e1, e2) != didCollide) {
//                    	e1.setCollision(e2, !didCollide);
//                    	e2.setCollision(e1, !didCollide);
                    CollisionCheckerResponse ccr = entitiesCollides(e1, e2);
                    if (ccr.collided) {
//                        if (!didCollide) {
                            CollisionResponse cr = new CollisionResponse(e1.entity, ccr.normal1, e2.entity, ccr.normal2);
                        	for (CollisionHandler ch: collisionHandlers) {
                        		ch.collisionOccured(cr);
                        	}
//                        }
                    }
                }
                
            }  
        }

        for (CollisionEntity tmp: entities) {
            tmp.dirty = false;
        }
    }
    
    private CollisionCheckerResponse entitiesCollides(CollisionEntity e1, CollisionEntity e2) {

        if (e1.bounds instanceof BoundingRectangle && e2.bounds instanceof BoundingRectangle) {
            return CollisionCheckers.collides((BoundingRectangle)e1.bounds, (BoundingRectangle)e2.bounds);
        }
        
        if (e1.bounds instanceof BoundingCircle && e2.bounds instanceof BoundingCircle) {
            return CollisionCheckers.collides((BoundingCircle)e1.bounds, (BoundingCircle)e2.bounds);
        }
        
        
        
        if (e1.bounds instanceof BoundingRectangle && e2.bounds instanceof BoundingCircle) {
            return CollisionCheckers.collides((BoundingRectangle)e1.bounds, (BoundingCircle)e2.bounds);
        }
        
        if (e1.bounds instanceof BoundingCircle && e2.bounds instanceof BoundingRectangle) {
            return CollisionCheckers.collides((BoundingCircle)e1.bounds, (BoundingRectangle)e2.bounds);
        }
        
        // TODO: Add more
        return null;
        
    }
    
    
}
