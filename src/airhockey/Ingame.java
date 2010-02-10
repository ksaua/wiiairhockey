package airhockey;

import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import engine.EmptyState;
import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.Light;
import engine.State;
import engine.TrueTypeFont;
import engine.events.Event;
import engine.modelloader.ObjLoader;

public class Ingame extends EmptyState {
	
	TrueTypeFont ttf;
	
	Entity table;
	Entity paddle;
	Camera cam;
	
	int[] scores = new int[2];
	
	@Override
	public void init(Engine e, GraphicContext gc) {
		table = new Entity(0,0,0);
		cam = new Camera(-40f, 12f, 0);
		cam.lookAt(0, 0, 0);
//		GL11.glEnable(GL11.GL_LIGHTING);
//		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
//		GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

		Light light = new Light(GL11.GL_LIGHT0, true, Light.POSITIONAL, 0, 2, 0);
		light.setAmbient(0.5f, 0.5f, 0.5f, 0);
		light.setDiffuse(0.8f, 0.8f, 0.8f, 0);
		
		paddle = new Entity(-12, 1.5f, 0);
		
		Font font = new Font("Courier New", Font.BOLD, 32);
		ttf = new TrueTypeFont(font, true);
		
		try {
			table.setRenderComponent(ObjLoader.load(new File("data/models/table2.obj")));
			paddle.setRenderComponent(ObjLoader.load(new File("data/models/paddle.obj")));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void event(Engine e, GraphicContext gc, Event ev) {
		if (ev.type == Event.Type.key_released && ev.lwjgl_id == Keyboard.KEY_ESCAPE) {
			e.setState("menu");
		} else if (ev.type == Event.Type.mouse_moved) {
			paddle.move(ev.dy * 0.05f, 0, ev.dx * 0.05f);
		} else if (ev.type == Event.Type.mouse_pressed && (ev.lwjgl_id == 0 || ev.lwjgl_id == 1)) {
			scores[ev.lwjgl_id]++;
		}
	}

	@Override
	public void render(Engine e, GraphicContext gc) {
		cam.transform();
		table.render();
		paddle.render();
		
		gc.start2dDrawing();
		ttf.drawString(gc.getScreenWidth() / 2, gc.getScreenHeight() - 40, String.valueOf(scores[0] + " - " + scores[1]), 1, 1, TrueTypeFont.ALIGN_CENTER);
	}

	@Override
	public void update(Engine e, GraphicContext gc, float dt) {

	}

	@Override
	public void onEnter(Engine e, GraphicContext gc) {
		scores = new int[2];
	}
}
