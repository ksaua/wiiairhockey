package airhockey;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.Entity;
import engine.collisionsystem2D.CollisionHandler;
import engine.collisionsystem2D.CollisionResponse;
import engine.utils.Line;

public class PuckController implements Updateable, CollisionHandler {

    private Entity puck;
    private Vector3f velocity;

    public PuckController(Entity puck) {
        this.puck = puck;
        this.velocity = new Vector3f(1f,0,0);
    }

    public void paddleCollision(Paddle paddle, Vector2f normal) {
        
        Vector3f paddle_velocity = paddle.getVelocity(); 
        Vector2f paddle_velocity2f = new Vector2f(paddle_velocity.x, paddle_velocity.z);
        
//        Vector2f paddle_pos = new Vector2f(paddle.getPos().z, paddle.getPos().x);
        float paddle_posx = paddle.getPos().x;
        float paddle_posy = paddle.getPos().z;
        
//        Vector2f puck_pos = new Vector2f(puck.getPos().z, puck.getPos().x);
        float puck_posx = puck.getPos().x;
        float puck_posy = puck.getPos().z;
        
        Vector2f velocity2f = new Vector2f(velocity.x, velocity.z); 
        
        float puck_length = 1000 * velocity2f.x;
        float puck_height = 1000 * velocity2f.y;
        
        float paddle_length = (float) (Math.sin(paddle.getRot().y) * 1000);
        float paddle_height = (float) (Math.cos(paddle.getRot().y) * 1000);

        boolean puck_heading_towards_paddle = Line.lineIntersection(
                paddle_posx + paddle_length, paddle_posy + paddle_height,
                paddle_posx - paddle_length, paddle_posy - paddle_height,
                puck_posx + puck_length, puck_posy + puck_height,
                puck_posx, puck_posy) != null;
        
        // Project the paddle's velocity onto the normal so we can find how much the paddle influences the puck
        if (paddle_velocity2f.x != 0 || paddle_velocity2f.y != 0) {
            float angle = Vector2f.angle(paddle_velocity2f, normal);
            
            float newlength = (float) (Math.cos(angle) * paddle_velocity2f.length());
            paddle_velocity2f.x = normal.x * newlength;
            paddle_velocity2f.y = normal.y * newlength;
        }
        
        if (puck_heading_towards_paddle) {
            // Calculate reflection
            Vector2f reflectedvector = getReflectedVector(normal, velocity2f);

            velocity2f.x = paddle_velocity2f.x + reflectedvector.x;
            velocity2f.y = paddle_velocity2f.y + reflectedvector.y;

        } else {
            Vector2f middlevector = Vector2f.add(velocity2f, paddle_velocity2f, null);
            middlevector.normalise();
            
            float newlength1 = 0;
            if (paddle_velocity2f.x != 0 || paddle_velocity2f.y != 0) {
                float angle = Vector2f.angle(paddle_velocity2f, middlevector);
                newlength1 = (float) (Math.cos(angle) * paddle_velocity2f.length());
            }

            
            float angle = Vector2f.angle(velocity2f, middlevector);
            float newlength2 = (float) (Math.cos(angle) * velocity2f.length());

            velocity2f.x = middlevector.x * (newlength1 + newlength2);
            velocity2f.y = middlevector.y * (newlength1 + newlength2);
        }
                
        Vector2f normalizedvelocity = velocity2f.normalise(null);
        velocity.x = velocity2f.x;
        velocity.z = velocity2f.y;
        
        // Move the puck to be positioned in front of the paddle
        puck.move(normalizedvelocity.x * 2f, 0, normalizedvelocity.y * 2f);
//        puck.setPosition(puck.getPos().x + move.x, puck.getPos().y, puck.getPos().z + move.y);

        
        // Scale the normal to the speed

//        reflectedvector.scale(current_speed);
//
//        velocity.x = reflectedvector.y;
//        velocity.z = reflectedvector.x;
    }

    private Vector2f getReflectedVector(Vector2f normal, Vector2f vector) {
        Vector2f n = new Vector2f(normal.x, normal.y);
        n.scale(2 * Vector2f.dot(vector, n) / Vector2f.dot(n, n));
        n.x -= vector.x;
        n.y -= vector.y;
        n.negate();
        return n;
    }
    
    @Override
    public void update(float dt) {
        if (Math.abs(puck.getPos().x) > 30) velocity.x *= -1;
        if (Math.abs(puck.getPos().z) > 15) velocity.z *= -1;

        velocity.scale(1 - (0.6f * dt));

        puck.move(velocity.x * dt, velocity.y * dt, velocity.z * dt);
    }

    @Override
    public void collisionOccured(CollisionResponse cr) {
        if (cr.getEntity1() instanceof Paddle && cr.getEntity2() == puck) {
            paddleCollision((Paddle)cr.getEntity1(), cr.getNormal1());
        } else if (cr.getEntity2() instanceof Paddle && cr.getEntity1() == puck) {
            paddleCollision((Paddle)cr.getEntity2(), cr.getNormal2());
        }        
    }

	public void resetVelocity() {
		velocity.x = 1f;
		velocity.y = 0;
		velocity.z = 0;
	}

}
