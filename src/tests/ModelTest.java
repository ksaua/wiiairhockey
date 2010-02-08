package tests;

import java.io.File;
import java.io.FileNotFoundException;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.Model;
import engine.State;
import engine.events.Event;
import engine.modelloader.ObjLoader;

public class ModelTest implements State {

	Model testmodel;
	Entity test;
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
		test.increaseRotation(0.02f, 0, 0);
	}

	
	public static void main(String[] args) {
		Engine e = new Engine("Test spill");
		e.addState("Meh", new ModelTest() );
		e.init();
		e.loop();
	}
}
