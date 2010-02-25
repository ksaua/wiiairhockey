package airhockey;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.Entity;

public class PuckController implements Controller {

	private Entity puck;
	private Vector3f velocity;

	public PuckController(Entity puck) {
		this.puck = puck;
		this.velocity = new Vector3f(0.1f,0,0);
	}

	public void paddleCollision(Paddle paddle) {

		Vector3f paddle_velocity = paddle.getVelocity();

		Vector2f paddle_velocity2f = new Vector2f(paddle_velocity.z, paddle_velocity.x); 
		Vector2f velocity2f = new Vector2f(velocity.z, velocity.x);


		// Calculate reflection
		Vector2f reflectedvector;
		Vector2f normal = new Vector2f(
				(float)-Math.sin(paddle.getRot().y),
				(float)Math.cos(paddle.getRot().y));

		System.out.print(normal);
		
		normal.scale(2 * Vector2f.dot(velocity2f, normal) / Vector2f.dot(normal, normal));
		reflectedvector = Vector2f.sub(normal, velocity2f, null);
		reflectedvector.normalise();

		System.out.println(" .. " + reflectedvector);


		/*
		 * If puck crashed into paddle: reverse the puck's velocity and add the paddle's velocity
		 * If not (the paddle pushed on the puck): set the puck's velocity to the paddle's
		 * In either case, put the puck in front of the paddle by N amount. 
		 */

		// *** Calculate speed ***


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

		float current_speed;

		if (paddle_velocity.x == 0 || (b && c || !b && !c)) { // Paddle touches the puck. Need to reverse the normal
			current_speed = paddle_velocity2f.length() + velocity2f.length();
			reflectedvector.scale(-1);
		} else if ((!a && !b && c) || (a && !b && !c)) { // Paddle pushes on the puck! 
			current_speed = paddle_velocity2f.length();
		} else { // Paddle and puck goes different ways, but somehow collides (possibly jumped over paddle).
			System.out.println("This should never happen!" + Math.random());
			return;
		}
		
		// Move the puck to be positioned in front of the paddle
		puck.setPosition(paddle.getPos().x + 3f * (reflectedvector.y > 0 ? 1 : -1), puck.getPos().y, puck.getPos().z);
		
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
