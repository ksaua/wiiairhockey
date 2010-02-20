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

public class VelocityAverager {
	
	private static class TimestampedVector {
		Vector3f vector;
		float time_to_next;
		
		public TimestampedVector(Vector3f v, float dt) {
			vector = v; time_to_next = dt;
		}
	}
	
	private LinkedList<TimestampedVector> list;
	
	// Has the average changed since last calculation?
	private boolean dirty;
	
	// Use this if the average hasn't changed
	private Vector3f average;

	private Timer timer;
	
	private float maxAverageTime;
	
	public VelocityAverager(float maxAverageTime) {
		this.list = new LinkedList<TimestampedVector>();
		this.timer = new Timer();
		this.maxAverageTime = maxAverageTime;
	}
	
	public void add(Vector3f obj) {
		list.addFirst(new TimestampedVector(obj, timer.getTime()));
		dirty = true;
	}
	
	public Vector3f getAverage() {
		if (dirty || average == null) {
			
			average = new Vector3f(0,0,0);
			
			float current = 0;
			for (TimestampedVector tv: list) {
				if (current > maxAverageTime) break;
				
				current += tv.time_to_next;
				average.x += tv.vector.x * tv.time_to_next;
				average.y += tv.vector.y * tv.time_to_next;
				average.z += tv.vector.z * tv.time_to_next;
			}
			
		}
		
		return average;
	}
	
}
