package tests;

import java.io.File;
import java.io.FileNotFoundException;

import org.lwjgl.opengl.GL11;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.Event;
import engine.GraphicContext;
import engine.Model;
import engine.State;
import engine.modelloader.ObjLoader;

public class TexturedModelTest implements State {

	Model testmodel;
	Entity test;
	Camera cam;
	
	@Override
	public void event(Engine e, GraphicContext gc, Event ev) {

	}

	@Override
	public void init(Engine e, GraphicContext gc) {
		GL11.glEnable( GL11.GL_TEXTURE_2D );
		
		try {
			testmodel = ObjLoader.load(new File("data/models/hockeybord.obj"));
			cam = new Camera(0f, 55f, 0f);
			cam.increaseRotation(-1.6f, 0, 0);
			test = new Entity();
			test.setRenderComponent(testmodel);

			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void render(Engine e, GraphicContext gc) {
		cam.transform();
		test.render();
	}

	@Override
	public void update(Engine e, GraphicContext gc, float dt) {
		cam.increaseRotation(0, 0.0f, 0.01f);
	}


	public static void main(String[] args) {
		Engine e = new Engine("Test spill");
		e.addState("Meh", new TexturedModelTest() );
		e.init();
		e.loop();
	}
}
