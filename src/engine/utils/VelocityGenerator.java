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

    public void push_pos(Vector3f obj) {
        timepositions.addFirst(
                new TimestampedPosition(obj, timer.getTime())
        );
    }

    public Vector3f getVelocity() {
        float current_time = timer.getTime();
        cleanup_list(current_time);

        Vector3f vel = new Vector3f(0,0,0);

        if (timepositions.size() > 1) {

            TimestampedPosition last = timepositions.getFirst();
            for (TimestampedPosition current: timepositions) {

                vel.x += (last.pos.x - current.pos.x);
                vel.y += (last.pos.y - current.pos.y);
                vel.z += (last.pos.z - current.pos.z);
                
                last = current;
            } 

            float dt = current_time - timepositions.getLast().time;

            vel.scale(1 / dt);
//            vel.x = totmovement / dt;
//            vel.y = (ps1.pos.y - ps2.pos.y) / dt;
//            vel.z = (ps1.pos.z - ps2.pos.z) / dt;

            //			Vector3f firstPos = timepositions.getFirst().pos;

        }

        return vel;
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
