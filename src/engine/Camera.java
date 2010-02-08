package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera extends Entity {
	protected Matrix4f matrix;
	protected FloatBuffer matrixbuf = BufferUtils.createFloatBuffer(16);
	
	public Camera(float posx, float posy, float posz) {
		super(posx, posy, posz);
		matrix = new Matrix4f();
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
		
	public void lookAt(float x, float y, float z) {
		GL11.glPushMatrix();
		
		// Perform a look at operation
		GL11.glLoadIdentity();
		GLU.gluLookAt(pos.x, pos.y, pos.z, x, y, z, 0, 1, 0);
		
		// Save matrix to floatbuffer
		matrixbuf.position(0);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrixbuf); 

		// Save to floatbuffer to Java matrix object 
		matrixbuf.position(0);
		matrix.load(matrixbuf);
		
		GL11.glPopMatrix();
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
