package tests;

import engine.Entity;
import engine.collisionsystem2D.BoundingBox;
import engine.collisionsystem2D.BoundingCircle;
import engine.collisionsystem2D.CollisionHandler;
import engine.collisionsystem2D.Collisionsystem;

public class CollisionTest {
    public static void main(String[] args) {
        Entity a = new Entity();
        Entity b = new Entity();

        Collisionsystem cs = new Collisionsystem();
        cs.addCollisionHandler(new CollisionHandler() {
            @Override
            public void collisionOccured(Entity a, Entity b) {
                System.out.println("Collision occured");
            }
        });
        cs.addEntity(a, new BoundingBox(a, 10, 5));
        cs.addEntity(b, new BoundingBox(b, 10, 10));

        a.increaseRotation(0, (float) (Math.PI / 2), 0);

        b.move(7, 0, 0);

        cs.check();

        b.move(1, 0, 0);

        cs.check();
        
        
        System.out.println("Checking circles");
        // Circles
        
        a = new Entity();
        b = new Entity();

        cs = new Collisionsystem();
        cs.addCollisionHandler(new CollisionHandler() {
            @Override
            public void collisionOccured(Entity a, Entity b) {
                System.out.println("Collision occured");
            }
        });
        cs.addEntity(a, new BoundingCircle(a, 5));
        cs.addEntity(b, new BoundingCircle(b, 5));

        a.increaseRotation(0, (float) (Math.PI / 2), 0);

        b.move(-10.1f, 0, 0);

        cs.check();

        b.move(0.2f, 0, 0);

        cs.check();

    }
}
