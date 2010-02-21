package engine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import engine.utils.SubscribeableChanges;

public class Entity extends SubscribeableChanges implements Renderable {
	
	public static final int POSITION_CHANGED = 0;
	public static final int ROTATION_CHANGED = 1;
	
	protected Vector3f pos;
	protected Vector3f rot;
	
	protected Renderable rendercomponent;
	
	public Entity() {
		this(0, 0, 0, 0, 0, 0);
	}
	
	public Entity(float posx, float posy, float posz) {
		this(posx, posy, posz, 0, 0, 0);
	}
	
	public Entity(float posx, float posy, float posz, float rotx, float roty, float rotz) {
		setPosition(posx, posy, posz);
		rot = new Vector3f(rotx, roty, rotz);
	}
	
	public Vector3f getPos() {
		return pos;
	}
	public Vector3f getRot() {
		return rot;
	}
	
	public void setPosition(float x, float y, float z) {
		pos = new Vector3f(x, y, z);
		pushChange(POSITION_CHANGED);
	}
	
	/**
	 * Move the object
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void move(float dx, float dy, float dz) {
	    pos = new Vector3f(pos.x + dx, pos.y + dy, pos.z + dz);
//		pos.x += dx;
//		pos.y += dy;
//		pos.z += dz;
		pushChange(POSITION_CHANGED);
	}
	
	/**
	 * Increase rotation
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void increaseRotation(float dx, float dy, float dz) {
		rot.x += dx;
		rot.y += dy;
		rot.z += dz;
		pushChange(ROTATION_CHANGED);
	}
		
	public void setRenderComponent(Renderable rc) {
		rendercomponent = rc;
	}
	
	public Renderable getRenderComponent(Renderable rc) {
		return rc;
	}
	
	/**
	 * Render the entity
	 */
	@Override
	public void render() {
		if (rendercomponent != null) {
			GL11.glPushMatrix();

			GL11.glTranslatef(pos.x, pos.y, pos.z);
			GL11.glRotatef(rot.x * 180, 1, 0, 0);
			GL11.glRotatef(rot.y * 180, 0, 1, 0);
			GL11.glRotatef(rot.z * 180, 0, 0, 1);
			rendercomponent.render();
			GL11.glPopMatrix();

		}
	}

}
