package engine.collisionsystem;

import org.lwjgl.util.vector.Vector3f;

public interface Collideable {
	public CollisionBounds getCollisionBounds();
	public Vector3f getPos();
	public Vector3f getRot();
}
