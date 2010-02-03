package engine;

import org.lwjgl.opengl.GL11;


public class Model implements Renderable {
	
	/** OpenGL list id */
	private int id;
	
	public Model(int id) {
		this.id = id;
	}

	@Override
	public void render() {
		GL11.glCallList(id);
	}
}
