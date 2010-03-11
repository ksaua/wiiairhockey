package engine.Wii;

import org.lwjgl.util.Timer;

public class Kalman {
	float velocity;
	float pos;
	float lasttime;
	float acceleration;
	Timer timer = new Timer();
	public Kalman() {
		
	}
	
	public float pushAccel(float a) {
		float time = timer.getTime();
		if (lasttime != 0) {
			
			float dt = time - lasttime;
			if (dt > 0.1) dt = 0.1f;
			System.out.println(dt);
			velocity += a * dt;
			pos += velocity * dt;
		}
		acceleration = a;
		lasttime = time;
		return pos;
	}
	
	public float getPos()  {
		float dt = timer.getTime() - lasttime; 
		return pos + velocity * dt + 0.5f * acceleration * dt * dt;
	}

	public void reset() {
		pos = 0;
		velocity = 0;
		lasttime = timer.getTime();
	}

	public void zeroVelocity() {
		velocity = 0;
	}
}
