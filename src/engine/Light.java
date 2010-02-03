package engine;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

public class Light extends Entity {
	
	public static float POSITIONAL = 0;
	public static float DIRECTIONAL = 1;
	
	private float light_type;
	
	private int lightid;
	
	private boolean state;
	
	public Light(int lightid, boolean state, float light_type, float x, float y, float z) {
		super(x, y, z);
		this.lightid = lightid;
		this.light_type = light_type;
		setEnabled(state);
	}
	
	@Override
	public void setPosition(float x, float y, float z) {
		super.setPosition(x, y, z);
		float[] t = {x, y, z, light_type};
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, FloatBuffer.wrap(t));
	}
	
	public void setAmbient(float r, float g, float b, float a) {
		GL11.glLight(lightid, GL11.GL_AMBIENT, floatbuffer(r,g,b,a));
	}
	
	public void setDiffuse(float r, float g, float b, float a) {
		GL11.glLight(lightid, GL11.GL_DIFFUSE, floatbuffer(r,g,b,a));
	}
	
	public void setSpecular(float r, float g, float b, float a) {
		GL11.glLight(lightid, GL11.GL_SPECULAR, floatbuffer(r,g,b,a));
	}
	
	public void setEnabled(boolean state) {
		this.state = state;
		if (state) 
			GL11.glEnable(lightid);
		else
			GL11.glDisable(lightid);
	}
	
	public boolean isEnabled() {
		return state;
	}
	
	private FloatBuffer floatbuffer(float a, float b, float c, float d) {
		float[] t = {a, b, c, d};
		return FloatBuffer.wrap(t);
	}
}
