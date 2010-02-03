package engine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Camera extends Entity {
	public Camera(float posx, float posy, float posz) {
		super(posx, posy, posz);
		recalculateMatrix();
	}

	public void move(float dx, float dy, float dz) {
		super.move(dx, dy, dz);
		recalculateMatrix();
	}
	
	public void increaseRotation(float dx, float dy, float dz) {
		super.increaseRotation(dx, dy, dz);
		recalculateMatrix();
	}
		
	public void recalculateMatrix() {
		matrix.setIdentity();
		matrix.translate(pos);
		matrix.rotate(rot.x, new Vector3f(1,0,0));
		matrix.rotate(rot.y, new Vector3f(0,1,0));
		matrix.rotate(rot.z, new Vector3f(0,0,1));
		matrix.invert();
		
		// Store matrix in floatbuffer
		matrixbuf.position(0);
		matrix.store(matrixbuf);
	}
	
	/**
	 * Multiplies the camera matrix
	 * with the current OpenGL matrix
	 */
	
	public void transform() {
		matrixbuf.position(0);
		GL11.glMultMatrix(matrixbuf);
	}
}
