package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.Light;
import engine.Model;
import engine.State;
import engine.events.Event;
import engine.modelloader.ObjLoader;

public class LightTest implements State {

	Model testmodel;
	Entity test;
	Light light;
	Camera cam;

	@Override
	public void event(Engine e, GraphicContext gc, Event ev) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(Engine e, GraphicContext gc) {
		try {

			testmodel = ObjLoader.load(new File("data/models/test.obj"));
			cam = new Camera(0, 0f, 6f);
			test = new Entity();
			test.setRenderComponent(testmodel);
			
			light = new Light(GL11.GL_LIGHT0, true, Light.POSITIONAL, 0, 4, 0);
			light.setAmbient(0, 0, 0, 0);
			light.setDiffuse(1, 1, 1, 0);
			light.setSpecular(1, 1, 1, 0);

			float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
			float mat_shininess = 50.0f ;

			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(mat_specular));
			GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, mat_shininess);

			GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);
			
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glEnable(GL11.GL_LIGHTING);
//			GL11.glEnable(GL11.GL_LIGHT0);


		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void render(Engine e, GraphicContext gc) {
		cam.transform();
		GL11.glColor4f(1f, 0.3f, 0.8f, 0.6f);
		test.render();
	}

	@Override
	public void update(Engine e, GraphicContext gc, float dt) {
		test.increaseRotation(0.02f, 0, 0);
	}


	public static void main(String[] args) {
		Engine e = new Engine("Test spill");
		e.addState("Meh", new LightTest() );
		e.init();
		e.loop();
	}
}
