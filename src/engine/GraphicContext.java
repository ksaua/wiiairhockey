package engine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class GraphicContext {
	public void start2dDrawing() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0, Display.getDisplayMode().getWidth(), 0.0, Display.getDisplayMode().getHeight(), -1.0, 1.0);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glViewport(0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());

		GL11.glDisable(GL11.GL_LIGHTING);
	}
	public void start3dDrawing() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(60f, Display.getDisplayMode().getWidth() / (float)Display.getDisplayMode().getHeight(), 0.1f, 100f);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		GL11.glEnable(GL11.GL_LIGHTING);
	}
		
	public int getScreenWidth() {
		return Display.getDisplayMode().getWidth();
	}
	public int getScreenHeight() {
		return Display.getDisplayMode().getHeight();
	}
}
