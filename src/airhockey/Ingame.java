package airhockey;

import java.awt.Font;
import java.util.LinkedList;

import motej.Mote;
import motej.MoteFinder;
import motej.MoteFinderListener;
import motej.event.CoreButtonEvent;
import motej.event.CoreButtonListener;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.Light;
import engine.Renderable;
import engine.TrueTypeFont;
import engine.View;
import engine.ViewState;
import engine.collisionsystem2D.BoundingRectangle;
import engine.collisionsystem2D.BoundingCircle;
import engine.collisionsystem2D.CollisionHandler;
import engine.collisionsystem2D.CollisionResponse;
import engine.collisionsystem2D.Collisionsystem;
import engine.gui.GuiContext;
import engine.gui.GuiText;
import engine.utils.MouseBuffer;

public class Ingame extends ViewState implements MoteFinderListener, CoreButtonListener {
    
    Airhockey game;

    LinkedList<Collisionsystem> collisionsystems = new LinkedList<Collisionsystem>();

    // Things which needs to be updated each frame
    LinkedList<Updateable> updateables = new LinkedList<Updateable>();
    
    // Gameobjects
    Entity table;
    Entity puck;
    Paddle[] paddles = new Paddle[2];
    
    // Controllers
    String[] controllers = new String[] {"Mus", "AI"};
    String[] enabledControllers;
    
    // AI controller
    AIPaddleController aipaddle;
    
    // Handles the input from the Wii
    PaddlePositionBuffer ppb;
    
    // Handles input from mouse
    MouseBuffer mouseBuffer;
    
    
    // Puck
    PuckController puckController;
    
    // Is WiiButton A held in?
    boolean calibrating;

    int[] scores = new int[2];
    
    GuiContext guicontext = new GuiContext();
    GuiText textHoldA, textPointController, textPaddleMiddle, textScores;
    
    View viewSingle, viewSplit0, viewSplit1;
    Camera camP1, camP2;

    // Is splitscreen activated?
    boolean splitscreened = false;

    MoteFinder finder;

    @Override
    public void init(Engine e, GraphicContext gc) {
        this.game = (Airhockey)e;
        
        camP1 = new Camera(-40f, 12f, 0);
        camP1.lookAt(0, 0, 0);
        
        camP2 = new Camera(40f, 12f, 0);
        camP2.lookAt(0, 0, 0);

        Light light = new Light(GL11.GL_LIGHT0, true, Light.POSITIONAL, 0, 2, 0);
        light.setAmbient(0.5f, 0.5f, 0.5f, 0);
        light.setDiffuse(0.8f, 0.8f, 0.8f, 0);

        table = new Entity(0,0,0);
        paddles[0] = new Paddle(-24, 1.5f, 0);
        paddles[1] = new Paddle( 24, 1.5f, 0);
        puck = new Entity(-10, 2, 0);
        
        mouseBuffer = new MouseBuffer(paddles[0], 10);
        puckController = new PuckController(puck);
        aipaddle = new AIPaddleController(paddles[1], puck, -6, 6, -12, 0, 2);
        
        updateables.add(mouseBuffer);
        updateables.add(puckController);
        updateables.add(aipaddle);

        Renderable paddle = MediaLoader.loadObj("paddle.obj");
        paddles[0].setRenderComponent(paddle);
        paddles[1].setRenderComponent(paddle);
        table.setRenderComponent(MediaLoader.loadObj("table2.obj"));
        puck.setRenderComponent(MediaLoader.loadObj("puck.obj"));
        
        BoundingRectangle bpaddle0 = new BoundingRectangle(paddles[0], 2, 10);
        BoundingRectangle bpaddle1 = new BoundingRectangle(paddles[1], 2, 10);
        BoundingCircle bpuck = new BoundingCircle(puck, 1);
        
        Collisionsystem cs = new Collisionsystem();
        cs.addCollisionHandler(puckController);
        cs.addEntity(bpaddle0);
        cs.addEntity(bpuck);
        collisionsystems.add(cs);
        
        cs = new Collisionsystem();
        cs.addCollisionHandler(puckController);
        cs.addEntity(bpaddle1);
        cs.addEntity(bpuck);
        collisionsystems.add(cs);
        
        // Score changing
        final BoundingRectangle bscore0 = new BoundingRectangle(new Entity(-28.5f, 1.5f, 0), 2, 10);
        final BoundingRectangle bscore1 = new BoundingRectangle(new Entity(28.5f, 1.5f, 0), 2, 10);
        CollisionHandler scoreChanger = new CollisionHandler() {
            @Override
            public void collisionOccured(CollisionResponse cr) {
                if (cr.getEntity1() == bscore0.getEntity() || cr.getEntity2() == bscore0.getEntity()) {
                    scores[0]++;
                } else {
                    scores[1]++;
                }
                textScores.setText(scores[1] + " - " + scores[0]);
                resetPuck();
            }
        };
        cs = new Collisionsystem();
        cs.addEntity(bpuck);
        cs.addEntity(bscore0);
        cs.addCollisionHandler(scoreChanger);
        collisionsystems.add(cs);
        
        cs = new Collisionsystem();
        cs.addEntity(bpuck);
        cs.addEntity(bscore1);
        cs.addCollisionHandler(scoreChanger);
        collisionsystems.add(cs);
        
        
        // Add the GUI text
        
        textScores = new GuiText(
                "0 - 0", 
                new TrueTypeFont(new Font("Courier New", Font.BOLD, 32), true),
                gc.getScreenWidth() / 2, gc.getScreenHeight() - 40,
                TrueTypeFont.ALIGN_CENTER);
        
        textHoldA = new GuiText(
                "Hold A for å kalibrere",
                5, gc.getScreenHeight() - 42);
        textPointController = new GuiText(
                "Pek kontrollen direkte mot lysene",
                5, gc.getScreenHeight() - 42);
        textPaddleMiddle = new GuiText(
                "Få klossen til å stå i midten og slipp A",
                5, gc.getScreenHeight() - 62);
        
        guicontext.addGuiElement(textHoldA);
        guicontext.addGuiElement(textScores);
        
        
        // Set up views for singlescreen and splitscreen 
        viewSingle = new View(gc.getScreenWidth(), gc.getScreenHeight(), 0, 0);
        viewSplit0 = new View(gc.getScreenWidth() / 2, gc.getScreenHeight(), 0, 0);
        viewSplit1 = new View(gc.getScreenWidth() / 2, gc.getScreenHeight(), gc.getScreenWidth() / 2, 0);
        viewSingle.setCamera(camP1);
        viewSplit0.setCamera(camP1);
        viewSplit1.setCamera(camP2);
        
        addView(viewSingle);
        setGuiContext(guicontext);

        try {
            finder = MoteFinder.getMoteFinder();
            finder.addMoteFinderListener(this);
            finder.startDiscovery();
            ((Airhockey)e).setWiiEnabled(true);
            enabledControllers = new String[]{"Wii", "Mus", "AI"};
        } catch (RuntimeException ex){
            ((Airhockey)e).setWiiEnabled(false);
            enabledControllers = new String[]{"Mus", "AI"};
            System.err.println("Fant ikke blåtanmottaker");
        }
    }
    
    @Override
    public void renderView(View view, Engine e, GraphicContext gc) {
        table.render();
        paddles[0].render();
        paddles[1].render();
        puck.render();        
    }

    @Override
    public synchronized void update(Engine e, GraphicContext gc, float dt) {
        
        for (Collisionsystem cs: collisionsystems)
            cs.check();

        for (Updateable u: updateables) u.update(dt);
    }

    @Override
    public void onEnter(Engine e, GraphicContext gc) {
        scores = new int[2];
        Mouse.setGrabbed(true);
    }
    
    @Override
    public void onExit(Engine e, GraphicContext gc) {
        Mouse.setGrabbed(false);
    }

    @Override
    public void mouseMoved(int dx, int dy) {
        mouseBuffer.pushMove(dx, dy);
    }

    @Override
    public void mouseButtonPressed(int x, int y, int button) {
        toggleSplitscreen();
        calibrating = !calibrating;
        
        if (!calibrating) {
            guicontext.addGuiElement(textHoldA);
            guicontext.removeGuiElement(textPointController);
            guicontext.removeGuiElement(textPaddleMiddle);
        } else {
            guicontext.addGuiElement(textPointController);
            guicontext.addGuiElement(textPaddleMiddle);
            guicontext.removeGuiElement(textHoldA);
        }
    }

    @Override
    public void keyReleased(int lwjglId, char keyChar) {
        if (lwjglId == Keyboard.KEY_ESCAPE) {
            game.setState("menu");
        }

        if (lwjglId == Keyboard.KEY_A) {
            if (getPaddleControlledBy("Mus") != null)
                getPaddleControlledBy("Mus").increaseRotation(0,-0.1f, 0);
        }
        
        if (lwjglId == Keyboard.KEY_D) {
            if (getPaddleControlledBy("Mus") != null)
                getPaddleControlledBy("Mus").increaseRotation(0, 0.1f, 0);
        }
        
        // Reset
        if (lwjglId == Keyboard.KEY_SPACE) {
            Mouse.setCursorPosition(400, 300);
            puck.setPosition(-10, 2, 0);
            paddles[0].setPosition(-15, 1.5f, 0);
        }
    }
    
    public void resetPuck() {
        puck.setPosition(-10, 2, 0);
        puckController.resetVelocity();
    }

	@Override
	public void moteFound(Mote mote) {
	    ppb = new PaddlePositionBuffer(paddles[0], 60, 40, 12f);
	    updateables.add(ppb);
		new WiiPaddleController(mote, ppb);
		mote.addCoreButtonListener(this);
		finder.stopDiscovery();
	}

	@Override
	public void buttonPressed(CoreButtonEvent evt) {
	    if (evt.isButtonAPressed() != calibrating) {
	        calibrating = !calibrating;
	        
	        if (!calibrating) {
	            guicontext.addGuiElement(textHoldA);
	            guicontext.removeGuiElement(textPointController);
	            guicontext.removeGuiElement(textPaddleMiddle);
	        } else {
	            guicontext.addGuiElement(textPointController);
	            guicontext.addGuiElement(textPaddleMiddle);
	            guicontext.removeGuiElement(textHoldA);
	        }
	    }
		if (evt.isButtonHomePressed()) {
		    resetPuck();
		}
	}
	
	public void toggleSplitscreen() {
	    splitscreened = !splitscreened;
	    if (splitscreened) {
	        addView(viewSplit0);
	        addView(viewSplit1);
	        removeView(viewSingle);
	    } else {
	        addView(viewSingle);
	        removeView(viewSplit0);
	        removeView(viewSplit1);
	    }
	}
	
	public Paddle getPaddleControlledBy(String controller) {
        for (int i = 0; i < controllers.length; i++) {
            if (controllers[i].equals(controller)) return paddles[i];
        }
        return null;
    }

    public String getController(int i) {
        return controllers[i];
    }

    public void changeController(int i) {
        if (enabledControllers.length == 2) {
            String old = controllers[0];
            controllers[0] = controllers[1];
            controllers[1] = old;
            
            setPaddleControllerToPaddle(controllers[0], paddles[0]);
            setPaddleControllerToPaddle(controllers[1], paddles[1]);
        } else {
            setPaddleControllerToPaddle(controllers[i], null);
            controllers[i] = getNextControllerType(controllers[i]);
            setPaddleControllerToPaddle(controllers[i], paddles[i]);
            
            int other = i == 1? 0 : 1;
            if (controllers[i].equals(controllers[other])) {
                controllers[other] = getNextControllerType(controllers[other]);
                setPaddleControllerToPaddle(getNextControllerType(controllers[other]), paddles[other]);
            }
            
        }
    }
    private String getNextControllerType(String s) {
        for (int i = 0; i < enabledControllers.length; i++) {
            if (enabledControllers[i].equals(s)) {
                if (i == enabledControllers.length - 1) {
                    return enabledControllers[0];
                } else {
                    return enabledControllers[i + 1];
                }
            }
        }
        System.out.println("Wrong");
        return s;
    }

    private void setPaddleControllerToPaddle(String controller, Paddle paddle) {
        if (controller.equals("AI")) {
            aipaddle.setPaddle(paddle);
        } else if (controller.equals("Wii")) {
            ppb.setPaddle(paddle);
        } else if (controller.equals("Mus")) {
            mouseBuffer.setPaddle(paddle);
        }
    }
}
