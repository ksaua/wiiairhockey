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
import engine.EmptyState;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.Light;
import engine.Renderable;
import engine.TrueTypeFont;
import engine.collisionsystem2D.BoundingBox;
import engine.collisionsystem2D.BoundingCircle;
import engine.collisionsystem2D.CollisionHandler;
import engine.collisionsystem2D.CollisionResponse;
import engine.collisionsystem2D.Collisionsystem;
import engine.gui.GuiContext;
import engine.gui.GuiText;
import engine.utils.MouseBuffer;

public class Ingame extends EmptyState implements MoteFinderListener, CoreButtonListener {

    Airhockey game;

    LinkedList<Collisionsystem> collisionsystems = new LinkedList<Collisionsystem>();

    LinkedList<Controller> controllers = new LinkedList<Controller>();

    Entity table;
    Paddle[] paddles = new Paddle[2];

    Entity puck;
    PuckController puckController;
    
    Camera cam;
    
    MouseBuffer mouseBuffer;
    
    boolean calibrating;

    int[] scores = new int[2];
    
    GuiContext guicontext = new GuiContext();
    GuiText textHoldA, textPointController, textPaddleMiddle, textScores;

    @Override
    public void init(Engine e, GraphicContext gc) {
        this.game = (Airhockey)e;
        
        GuiText.defaultFont = new TrueTypeFont(new Font("Courier New", Font.BOLD, 18), true);

        mouseBuffer = new MouseBuffer(10);
        
        cam = new Camera(-40f, 12f, 0);
        cam.lookAt(0, 0, 0);

        Light light = new Light(GL11.GL_LIGHT0, true, Light.POSITIONAL, 0, 2, 0);
        light.setAmbient(0.5f, 0.5f, 0.5f, 0);
        light.setDiffuse(0.8f, 0.8f, 0.8f, 0);

        table = new Entity(0,0,0);
        paddles[0] = new Paddle(-24, 1.5f, 0);
        paddles[1] = new Paddle( 24, 1.5f, 0);
        puck = new Entity(-10, 2, 0);

        puckController = new PuckController(puck);
        controllers.add(puckController);
        controllers.add(new AIPaddleController(paddles[1], puck, -6, 6, -12, 0, 2));

        Renderable paddle = MediaLoader.loadObj("paddle.obj");
        paddles[0].setRenderComponent(paddle);
        paddles[1].setRenderComponent(paddle);
        table.setRenderComponent(MediaLoader.loadObj("table2.obj"));
        puck.setRenderComponent(MediaLoader.loadObj("puck.obj"));
        
        BoundingBox bpaddle0 = new BoundingBox(paddles[0], 2, 10);
        BoundingBox bpaddle1 = new BoundingBox(paddles[1], 2, 10);
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
        final BoundingBox bscore0 = new BoundingBox(new Entity(-28.5f, 1.5f, 0), 2, 10);
        final BoundingBox bscore1 = new BoundingBox(new Entity(28.5f, 1.5f, 0), 2, 10);
        CollisionHandler scoreChanger = new CollisionHandler() {
            @Override
            public void collisionOccured(CollisionResponse cr) {
                if (cr.getEntity1() == bscore0.getEntity() || cr.getEntity2() == bscore0.getEntity()) {
                    scores[0]++;
                } else {
                    scores[1]++;
                }
                textScores.setText(scores[0] + " - " + scores[1]);
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

                
        finder = MoteFinder.getMoteFinder();
		finder.addMoteFinderListener(this);
		finder.startDiscovery();
    }
    MoteFinder finder;

    @Override
    public synchronized void render(Engine e, GraphicContext gc) {
        cam.transform();
        table.render();
        paddles[0].render();
        paddles[1].render();
        puck.render();
        
        gc.start2dDrawing();
        guicontext.render(e, gc);
    }

    @Override
    public synchronized void update(Engine e, GraphicContext gc, float dt) {
        paddles[0].move(mouseBuffer.getY() * 0.01f, 0, mouseBuffer.getX() * 0.01f);
        
        for (Collisionsystem cs: collisionsystems)
            cs.check();

        for (Controller c: controllers) c.update(dt);
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
        
        if (lwjglId == Keyboard.KEY_SPACE) {
            System.out.println(paddles[0].getPos());
        }

        if (lwjglId == Keyboard.KEY_A) {
        	paddles[0].increaseRotation(0,-0.1f, 0);
        }
        
        if (lwjglId == Keyboard.KEY_D) {
        	paddles[0].increaseRotation(0, 0.1f, 0);
        }
        
        // Reset mouse
        if (lwjglId == Keyboard.KEY_SPACE) {
            Mouse.setCursorPosition(400, 300);
            paddles[0].setPosition(-15, 1.5f, 0);
        }
    }
    
    public void resetPuck() {
        puck.setPosition(-10, 2, 0);
        puckController.resetVelocity();
    }

	@Override
	public void moteFound(Mote mote) {
	    PaddlePositionBuffer ppb = new PaddlePositionBuffer(paddles[0], 60, 40, 12f);
	    controllers.add(ppb);
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
}
