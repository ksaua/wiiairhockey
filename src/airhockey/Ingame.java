package airhockey;

import java.io.File;
import java.io.FileNotFoundException;

import org.lwjgl.util.glu.GLU;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.State;
import engine.events.Event;
import engine.modelloader.ObjLoader;

public class Ingame implements State {
	Entity table;
	Camera cam;
	
	@Override
	public void init(Engine e, GraphicContext gc) {
		table = new Entity(0,0,0);
		cam = new Camera(-55f, 20f, 0);
		cam.lookAt(30, 0, 0);

		
		try {
			table.setRenderComponent(ObjLoader.load(new File("data/models/table2.obj")));

			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void event(Engine e, GraphicContext gc, Event ev) {
	}

	@Override
	public void render(Engine e, GraphicContext gc) {
		GLU.gluLookAt(-40, 12, 0, 0, 0, 0, 0, 1f, 0);
		table.render();
	}

	@Override
	public void update(Engine e, GraphicContext gc, float dt) {

	}

}
