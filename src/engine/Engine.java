package engine;

import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Timer;

import engine.Wii.WiiEventCreator;

public class Engine implements EventListener {
	private HashMap<String, State> states;
	private String currentState;
	private boolean running;
	private LinkedList<Event> events;
	private String title;

	public Engine(String title) {
		this.title = title;
		states = new HashMap<String, State>();
		events = new LinkedList<Event>();
		running = true;
	}

	public void setUpDisplay(int width, int height) {
		DisplayMode chosenMode = null;
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();

			for (int i=0;i<modes.length;i++) {
				if ((modes[i].getWidth() == width) && (modes[i].getHeight() == height)) {
					chosenMode = modes[i];
					break;
				}
			}
		} catch (LWJGLException e) {     
			Sys.alert("Error", "Unable to determine display modes.");
			System.exit(0);
		}

		// Fant ingen display modus som passet
		if (chosenMode == null) {
			Sys.alert("Error", "Unable to find appropriate display mode.");
			System.exit(0);
		}

		try {
			Display.setVSyncEnabled(true);
			Display.setDisplayMode(chosenMode);
			Display.setTitle(title);
			Display.create();
		} catch (LWJGLException e) {
			Sys.alert("Error", "Unable to create display.");
			System.exit(0);
		}
	}

	public void init() {
		setUpDisplay(800, 600);

		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		GL11.glClearColor(0,0,0,0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
//		GL11.glClearDepth(1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);




		for (State state: states.values()) {
			state.init(this, gc);
		}
	}
	
	GraphicContext gc = new GraphicContext();

	public void loop() {
		Timer timer = new Timer();
		float lasttime = timer.getTime();
		

		while (running && !Display.isCloseRequested()) {
			Timer.tick();
			float dt = timer.getTime() - lasttime;
			lasttime = timer.getTime();

			State current = states.get(currentState);

			synchronized (events) {
				for (Event e: events) {
					current.event(this, gc, e);
				}
				events.clear();	
			}

			current.update(this, gc, dt);
			
			
			gc.start3dDrawing();
		    GL11.glLoadIdentity();
			current.render(this, gc);


			Display.update();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			Display.sync(60);
		}

		Display.destroy();
		System.exit(0);		
	}


	public void addState(String id, State state) {
		if (states.isEmpty()) currentState = id;
		states.put(id, state);
	}

	@Override
	public void event(Event e) {
		synchronized (events) {
			events.add(e);
		}
	}
}
