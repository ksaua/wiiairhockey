package engine;

import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Timer;

import engine.events.Event;
import engine.events.KeyboardEventCreator;
import engine.events.MouseEventCreator;
import engine.utils.SmoothTimer;

public class Engine {
    private HashMap<String, State> states;
    private State currentState;
    private boolean running;
    private LinkedList<Event> events;
    private String title;

    // Theese will poll the input and create events based on the polling
    private KeyboardEventCreator kec = new KeyboardEventCreator();
    private MouseEventCreator mec = new MouseEventCreator();

    public Engine(String title) {
        this.title = title;
        states = new HashMap<String, State>();
        events = new LinkedList<Event>();
        running = true;


        kec = new KeyboardEventCreator();
        mec = new MouseEventCreator();
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

        GL11.glClearColor(0,0.5f,0,0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glClearDepth(1.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

        // Initialize all states
        for (State state: states.values()) {
            state.init(this, gc);
        }
    }

    GraphicContext gc = new GraphicContext();

    public void loop() {
        Timer timer = new Timer();
        float starttime = timer.getTime();
        float lastdrawtime = timer.getTime();
        float lastupdatetime = timer.getTime();

        int frames = 0;
        int updates = 0;
        
        SmoothTimer.amount = 100;

        while (running && !Display.isCloseRequested()) {
            float currenttime = timer.getTime();
            while ((currenttime - lastdrawtime) < (1 / 80f)) {
                                
                
                // Calculate time
                Timer.tick();
                currenttime = timer.getTime();
                
                SmoothTimer.tick();
                float dt = SmoothTimer.getDelta();
                                
                if (dt != 0) {
                    lastupdatetime = currenttime;
    
                    // Poll for events
                    kec.poll();
                    mec.poll();
    
                    currentState.update(this, gc, dt);
                    
                    updates++;
                }
                
            }
            
            lastdrawtime = timer.getTime();

            // Reset color
            GL11.glColor3f(1, 1, 1);

            // Start drawing 
            gc.start3dDrawing();
            GL11.glLoadIdentity();
            currentState.render(this, gc);


            Display.update();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            
            frames++;
        }

        System.out.println("Average fps: " + frames / (timer.getTime() - starttime) + ", average updates/s: " + updates / (timer.getTime() - starttime));
        Display.destroy();
        System.exit(0);		
    }
    
    public void addState(String id, State state) {
        if (states.isEmpty()) {
            state.onEnter(this, gc);
            setUpListeners(null, state);
            currentState = state;
        }
        states.put(id, state);
    }

    public void setState(String id) {
        State newState = states.get(id); 

        currentState.onExit(this, gc);
        newState.onEnter(this, gc);

        setUpListeners(currentState, newState);
        
        currentState = newState;
    }

    private void setUpListeners(State oldstate, State newstate) {
        kec.removeKeyboardListener(oldstate);
        mec.removeMouseListener(oldstate);
        kec.addKeyboardListener(newstate);
        mec.addMouseListener(newstate);
    }
}
