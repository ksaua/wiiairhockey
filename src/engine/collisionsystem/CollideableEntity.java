package engine.collisionsystem;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.Entity;
import engine.utils.ChangeListener;

public class CollideableEntity implements Collideable, ChangeListener {
		
	Entity entity;
	CollisionBounds bounds;
	
	Vector2f position;
	float rotation;
	
	public CollideableEntity(Entity entity, CollisionBounds bounds) {
		this.entity = entity;
		this.bounds = bounds;
		
		entity.addSubcription(Entity.POSITION_CHANGED, this);
		entity.addSubcription(Entity.ROTATION_CHANGED, this);
		
		Vector3f p = entity.getPos();
		position = new Vector2f(p.x, p.z);
		
		rotation = entity.getRot().y;
	}

	@Override
	public CollisionBounds getCollisionBounds() {
		return bounds;
	}

	public Entity getRealEntity() {
		return entity;
	}
	
	@Override
	public Vector2f getPos() {
		return position;
	}

	@Override
	public float getRot() {
		return rotation;
	}

	@Override
	public void change(int id) {
		if (id == Entity.POSITION_CHANGED) {
			Vector3f p = entity.getPos();
			position = new Vector2f(p.x, p.z);
		} else if (id == Entity.ROTATION_CHANGED) {
			rotation = entity.getRot().y;
		}
	}
}
