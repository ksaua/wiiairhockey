package airhockey;

import org.lwjgl.util.vector.Vector3f;

import engine.Entity;
import engine.utils.VelocityGenerator;

public class Paddle extends Entity {
	
	private VelocityGenerator velocity_generator;

	public Paddle(float posx, float posy, float posz) {
		super(posx, posy, posz, 0, 0, 0);
		velocity_generator = new VelocityGenerator(0.1f );
	}
	
	@Override
	public void move(float dx, float dy, float dz) {
		super.move(dx, dy, dz);
		velocity_generator.push_move(new Vector3f(dx, dy, dz));
	}
	
	public Vector3f getVelocity() {
		return velocity_generator.getVelocity();
	}
}
