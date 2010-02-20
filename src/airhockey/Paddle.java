package airhockey;

import org.lwjgl.util.vector.Vector3f;

import engine.Entity;
import engine.utils.VelocityAverager;

public class Paddle extends Entity {
	
	VelocityAverager average_velocity;

	public Paddle(float posx, float posy, float posz) {
		super(posx, posy, posz, 0, 0, 0);
		average_velocity = new VelocityAverager(0.5f);
	}
	
	@Override
	public void move(float dx, float dy, float dz) {
		super.move(dx, dy, dz);
		average_velocity.add(new Vector3f(dx, dy, dz));
	}
}
