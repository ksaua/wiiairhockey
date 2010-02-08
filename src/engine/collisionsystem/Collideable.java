package engine.collisionsystem;

import org.lwjgl.util.vector.Vector2f;

public interface Collideable {
	public CollisionBounds getCollisionBounds();
	public Vector2f getPos();
	public float getRot();
}
