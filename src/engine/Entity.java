package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.collisionsystem.Collideable;
import engine.collisionsystem.CollisionBounds;

public class Entity implements Renderable, Collideable {
	
	protected Vector3f pos;
	protected Vector3f rot;
	protected Matrix4f matrix;
	protected FloatBuffer matrixbuf = BufferUtils.createFloatBuffer(16);

	
	protected CollisionBounds collisionbounds;
	
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
		matrix = new Matrix4f();
//		recalculateMatrix();
	}
	
	public Vector3f getPos() {
		return pos;
	}
	public Vector3f getRot() {
		return rot;
	}
	
	public void setPosition(float x, float y, float z) {
		pos = new Vector3f(x, y, z);
	}
	
	/**
	 * Move the object
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void move(float dx, float dy, float dz) {
		pos.x += dx;
		pos.y += dy;
		pos.z += dz;
//		recalculateMatrix();
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
		recalculateMatrix();
	}
	
	
	public void recalculateMatrix() {
//		matrix.setIdentity();
//		matrix.translate(pos);
//		matrix.rotate(rot.x, new Vector3f(1,0,0));
//		matrix.rotate(rot.y, new Vector3f(0,1,0));
//		matrix.rotate(rot.z, new Vector3f(0,0,1));
//		matrixbuf.position(0);
//		matrix.store(matrixbuf);
	}
	
	public Matrix4f getTransformMatrix() {
		return matrix;
	}
	
	
	public void setCollisionBounds(CollisionBounds cb) {
		collisionbounds = cb;
	}
	
	public CollisionBounds getCollisionBounds() {
		return collisionbounds;
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
//			matrixbuf.position(0);
//			GL11.glMultMatrix(matrixbuf);
			GL11.glTranslatef(pos.x, pos.y, pos.z);
			GL11.glRotatef(rot.x * 180, 1, 0, 0);
			GL11.glRotatef(rot.y, 0, 1, 0);
			GL11.glRotatef(rot.z, 0, 0, 1);
			rendercomponent.render();
			GL11.glPopMatrix();

		}
	}

}
