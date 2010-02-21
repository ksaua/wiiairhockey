package engine.utils;

import java.util.LinkedList;

import org.lwjgl.util.Timer;
import org.lwjgl.util.vector.Vector3f;

/**
 * Allows one to add vectors.
 * The class figures out the time between the this vector
 * and last object added and adds this to the calculation.
 * Then get the average speed
 * 
 * @author canute
 *
 */

public class VelocityGenerator {

	private static class TimestampedPosition {
		Vector3f pos;
		float time;

		public TimestampedPosition(Vector3f p, float t) {
			pos = p; time = t;
		}
	}

	LinkedList<TimestampedPosition> timepositions;

	private float savefor;

	private Timer timer;

	/**
	 * @param savefor Average out the last n seconds of velocity.  
	 */
	public VelocityGenerator(float savefor) {
		this.savefor = savefor;
		this.timer = new Timer();
		this.timepositions = new LinkedList<TimestampedPosition>();
	}

	public void push_move(Vector3f obj) {
		timepositions.addFirst(
				new TimestampedPosition(obj, timer.getTime())
		);
	}

	public Vector3f getVelocity() {
		float current_time = timer.getTime();
		cleanup_list(current_time);

		Vector3f vel = new Vector3f(0,0,0);

		if (timepositions.size() > 0) {
			TimestampedPosition last = new TimestampedPosition(timepositions.get(0).pos, current_time);
			for (TimestampedPosition current: timepositions) {
				float dt = last.time - current.time;
				if (dt != 0) {
					vel.x += (last.pos.x - current.pos.x) / dt;
					vel.y += (last.pos.y - current.pos.y) / dt;
					vel.z += (last.pos.z - current.pos.z) / dt;
					last = current;
				}
			}
		}
		return vel;
		//		boolean savedtt = (timer.getTime() - lastTime) == 0; // Saved this tick?
		//		float dt = lastTime - secondLastTime;
		//		if (deltaPos != null && savedtt && dt != 0) {
		//
		//			Vector3f velocity = new Vector3f();
		//			velocity.x = deltaPos.x / dt;
		//			velocity.y = deltaPos.y / dt;
		//			velocity.z = deltaPos.z / dt;
		//			return velocity;			
		//		} else {
		//			return new Vector3f(0,0,0);
		//		}
	}

	/**
	 * Remove entries older than set time limit
	 * @param current_time
	 */
	private void cleanup_list(float current_time) {
		int end_index = -1;

		for (TimestampedPosition tp: timepositions) {
			if ((current_time - tp.time) > savefor) break;
			end_index++;
		}

		if (timepositions.size() != 0 && end_index != timepositions.size()) 
			timepositions.subList(end_index + 1, timepositions.size()).clear();
	}
}
