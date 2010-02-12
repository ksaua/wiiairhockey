package engine.collisionsystem2D;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import engine.Entity;
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
        
        public CollisionEntity(Entity e, Bounds b) {
            entity = e; bounds = b;
        }
    }
    
    
    private HashMap<Entity, CollisionEntity> entities;

    private LinkedList<CollisionHandler> collisionHandlers;

    
    public Collisionsystem() {
        entities = new HashMap<Entity, CollisionEntity>();
        collisionHandlers = new LinkedList<CollisionHandler>();
    }
    
    public void addEntity(Entity entity, Bounds bounds) {
        entities.put(entity, new CollisionEntity(entity, bounds));
        entity.addSubcription(Entity.POSITION_CHANGED, this);
        entity.addSubcription(Entity.ROTATION_CHANGED, this);
    }

    // Entity changed some parameter
    @Override
    public void change(SubscribeableChanges source, int id) {
        CollisionEntity src = entities.get((Entity) source);
        src.bounds.updateVertices();
        src.dirty = true;
    }
    

    public void addCollisionHandler(CollisionHandler ch) {
        collisionHandlers.add(ch);
    }
    
    public void check() {
        Collection<CollisionEntity> c = entities.values();
        
        CollisionEntity[] ce = new CollisionEntity[c.size()];
        c.toArray(ce);
        
        for (int i = 0; i < ce.length; i++) {
            for (int j = ce.length - 1; j > i; j--) {
                
                CollisionEntity e1 = ce[i];
                CollisionEntity e2 = ce[j];
                
                if (e1.dirty || e2.dirty) {
                    if (entitiesCollides(e1, e2)) {
                        for (CollisionHandler ch: collisionHandlers) {
                            ch.collisionOccured(e1.entity, e2.entity);
                        }
                    }
                }
                
            }  
        }

        for (CollisionEntity tmp: ce) {
            tmp.dirty = false;
        }
    }
    
    private boolean entitiesCollides(CollisionEntity e1, CollisionEntity e2) {
        
        if (e1.bounds instanceof BoundingBox && e2.bounds instanceof BoundingBox) {
            return CollisionCheckers.collides((BoundingBox)e1.bounds, (BoundingBox)e2.bounds);
        }
        
        if (e1.bounds instanceof BoundingCircle && e2.bounds instanceof BoundingCircle) {
            return CollisionCheckers.collides((BoundingCircle)e1.bounds, (BoundingCircle)e2.bounds);
        }
        // TODO: Add more
        return false;
        
    }
    
    
}
