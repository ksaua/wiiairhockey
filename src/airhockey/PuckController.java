package airhockey;

import org.lwjgl.util.vector.Vector3f;

import engine.Entity;

public class PuckController implements Controller {

    private Entity puck;
    private Vector3f velocity;
    
    public PuckController(Entity puck) {
        this.puck = puck;
        this.velocity = new Vector3f(0,0,0);
    }
    
    public void paddleCollision(Paddle paddle) {
        /*
         * If puck crashed into paddle: reverse the puck's velocity and add the paddle's velocity
         * If not (the paddle pushed on the puck): set the puck's velocity to the paddle's
         * In either case, put the puck in front of the paddle by N amount. 
         */

        Vector3f paddle_velocity = paddle.getVelocity();

        Vector3f last_paddle_pos = new Vector3f(
                paddle.getPos().x - paddle_velocity.x,
                paddle.getPos().y - paddle_velocity.y,
                paddle.getPos().z - paddle_velocity.z);

        Vector3f last_puck_pos = new Vector3f(
                puck.getPos().x - velocity.x,
                puck.getPos().y - velocity.y,
                puck.getPos().z - velocity.z);

        boolean paddle_below_puck = last_paddle_pos.x < last_puck_pos.x;
        boolean puck_going_downwards = velocity.x < 0;
        boolean paddle_going_downwards = paddle_velocity.x < 0;

        boolean a = paddle_going_downwards;
        boolean b = puck_going_downwards;
        boolean c = paddle_below_puck;

        if (paddle_velocity.x == 0 || (b && c || !b && !c)) { // Paddle touches the puck
            velocity.x = -velocity.x + paddle_velocity.x;
            puck.setPosition(paddle.getPos().x + 3f * (velocity.x > 0 ? 1 : -1), puck.getPos().y, puck.getPos().z);
        } else if ((!a && !b && c) || (a && !b && !c)) { // Paddle pushes on the puck
            velocity.x = paddle_velocity.x;
            puck.setPosition(paddle.getPos().x + 3f * (velocity.x > 0 ? 1 : -1), puck.getPos().y, puck.getPos().z);

        } else { // Paddle and puck goes different ways, but somehow collides (possibly jumped over paddle).
            System.out.println("This should never happen!" + Math.random());
        }
    }

    @Override
    public void update(float dt) {
        velocity.scale(1 - (0.6f * dt));
        
        puck.move(velocity.x * dt, velocity.y * dt, velocity.z * dt);
    }

}
