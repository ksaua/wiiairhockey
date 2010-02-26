package airhockey;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.Entity;
import engine.utils.Line;

public class PuckController implements Controller {

    private Entity puck;
    private Vector3f velocity;

    public PuckController(Entity puck) {
        this.puck = puck;
        this.velocity = new Vector3f(1f,0,0);
    }

    public void paddleCollision(Paddle paddle, Vector2f normal) {

        Vector3f paddle_velocity = paddle.getVelocity();
        Vector2f paddle_velocity2f = new Vector2f(paddle_velocity.z, paddle_velocity.x);
        
        Vector2f paddle_pos = new Vector2f(paddle.getPos().z, paddle.getPos().x);
        Vector2f puck_pos = new Vector2f(puck.getPos().z, puck.getPos().x);
        
        Vector2f velocity2f = new Vector2f(velocity.z, velocity.x); 


        // Calculate reflection
        Vector2f reflectedvector;
        //		Vector2f normal = new Vector2f(
        //				(float)-Math.sin(paddle.getRot().y),
        //				(float)Math.cos(paddle.getRot().y));

//        System.out.print(normal);

        normal.scale(2 * Vector2f.dot(velocity2f, normal) / Vector2f.dot(normal, normal));
        reflectedvector = Vector2f.sub(normal, velocity2f, null);
        reflectedvector.normalise();

//        System.out.println(" .. " + reflectedvector);


        /*
         * If puck crashed into paddle: reverse the puck's velocity and add the paddle's velocity
         * If not (the paddle pushed on the puck): set the puck's velocity to the paddle's
         * In either case, put the puck in front of the paddle by N amount. 
         */

        // *** Calculate speed ***
//
//
//        Vector3f last_paddle_pos = new Vector3f(
//                paddle.getPos().x - paddle_velocity.x,
//                paddle.getPos().y - paddle_velocity.y,
//                paddle.getPos().z - paddle_velocity.z);

        Vector2f next_pos = new Vector2f(puck_pos.x + 1000 * velocity2f.x, puck_pos.y + 1000 * velocity2f.y);
        Vector2f last_pos = new Vector2f(puck_pos.x - velocity2f.x, puck_pos.y - velocity2f.y);
        
        float x = (float) (Math.cos(paddle.getRot().y) * 1000);
        float y = (float) (Math.sin(paddle.getRot().y) * 1000);
        
        Line line = new Line(new Vector2f(paddle_pos.x + x, paddle_pos.y + y), new Vector2f(paddle_pos.x -x, paddle_pos.y -y));
        Line line2 = new Line(next_pos, puck_pos);
        System.out.println(line + ", " + line2);
        boolean puck_heading_towards_paddle = Line.lineIntersection(line, line2) != null;
        
//        System.out.println(next_pos + ", " + ", " + puck_heading_towards_paddle + ", " + Vector2f.sub(next_pos, now_pos, null));
        
//            Vector3f.sub(last_puck_pos2, paddle.getPos(), null).lengthSquared() >
//            Vector3f.sub(last_puck_pos, paddle.getPos(), null).lengthSquared();

//        boolean paddle_below_puck = last_paddle_pos.x < last_puck_pos.x;
//        boolean puck_going_downwards = velocity.x < 0;
//        boolean paddle_going_downwards = paddle_velocity.x < 0;

//        boolean a = paddle_going_downwards;
//        boolean b = puck_going_downwards;
//        boolean c = paddle_below_puck;

        float current_speed;

        if (puck_heading_towards_paddle) {
            System.out.println("Heading");
            current_speed = Vector2f.sub(velocity2f, paddle_velocity2f, null).length();
        } else {
            System.out.println("Not heading");
            current_speed = paddle_velocity2f.length();
            reflectedvector.negate();
        }
//        if (paddle_velocity.x == 0 || (b && c || !b && !c)) { // Paddle touches the puck. Need to reverse the normal
//            current_speed = paddle_velocity2f.length() + velocity2f.length();
//        } else if ((!a && !b && c) || (a && !b && !c)) { // Paddle pushes on the puck!
//            reflectedvector.scale(-1);
//            current_speed = paddle_velocity2f.length();
//        } else { // Paddle and puck goes different ways, but somehow collides (possibly jumped over paddle).
//            System.out.println("This should never happen!" + Math.random());
//            return;
//        }
        
        // Move the puck to be positioned in front of the paddle
        Vector2f move = new Vector2f(reflectedvector.x * 2f, reflectedvector.y * 2f);
                
        puck.setPosition(puck.getPos().x + move.y, puck.getPos().y, puck.getPos().z + move.x);

        // Scale the normal to the speed

        reflectedvector.scale(current_speed);

        velocity.x = reflectedvector.y;
        velocity.z = reflectedvector.x;
    }

    @Override
    public void update(float dt) {
        if (Math.abs(puck.getPos().x) > 30) velocity.x *= -1;
        if (Math.abs(puck.getPos().z) > 15) velocity.z *= -1;

        velocity.scale(1 - (0.6f * dt));

        puck.move(velocity.x * dt, velocity.y * dt, velocity.z * dt);
    }

}
