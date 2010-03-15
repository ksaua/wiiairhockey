package airhockey;

import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedList;

import motej.CalibrationDataReport;
import motej.IrCameraMode;
import motej.IrCameraSensitivity;
import motej.IrPoint;
import motej.Mote;
import motej.MoteFinder;
import motej.MoteFinderListener;
import motej.event.AccelerometerEvent;
import motej.event.AccelerometerListener;
import motej.event.CoreButtonEvent;
import motej.event.CoreButtonListener;
import motej.event.IrCameraEvent;
import motej.event.IrCameraListener;
import motej.request.ReportModeRequest;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Timer;
import org.lwjgl.util.vector.Vector2f;

import engine.Camera;
import engine.EmptyState;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.Light;
import engine.Renderable;
import engine.TrueTypeFont;
import engine.Wii.ConsistentIrPoint;
import engine.Wii.Kalman;
import engine.collisionsystem2D.BoundingBox;
import engine.collisionsystem2D.BoundingCircle;
import engine.collisionsystem2D.Collisionsystem;
import engine.utils.MouseBuffer;

public class Ingame extends EmptyState implements MoteFinderListener, CoreButtonListener {

    Airhockey game;

    LinkedList<Collisionsystem> collisionsystems = new LinkedList<Collisionsystem>();

    TrueTypeFont ttf;

    LinkedList<Controller> controllers = new LinkedList<Controller>();

    Entity table;
    Paddle[] paddles = new Paddle[2];

    Entity puck;
    PuckController puckController;
    
    Camera cam;
    
    MouseBuffer mouseBuffer;

    int[] scores = new int[2];

//    Vector3f puck_velocity = new Vector3f();

    @Override
    public void init(Engine e, GraphicContext gc) {
        this.game = (Airhockey)e;

        mouseBuffer = new MouseBuffer(10);
        
        cam = new Camera(-40f, 12f, 0);
        cam.lookAt(0, 0, 0);
        //		GL11.glEnable(GL11.GL_LIGHTING);
        //		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        //		GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

        Light light = new Light(GL11.GL_LIGHT0, true, Light.POSITIONAL, 0, 2, 0);
        light.setAmbient(0.5f, 0.5f, 0.5f, 0);
        light.setDiffuse(0.8f, 0.8f, 0.8f, 0);

        table = new Entity(0,0,0);
        paddles[0] = new Paddle(-24, 1.5f, 0);
        paddles[1] = new Paddle( 24, 1.5f, 0);
        puck = new Entity(-10, 2, 0);

        Font font = new Font("Courier New", Font.BOLD, 32);
        ttf = new TrueTypeFont(font, true);

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
        ttf.drawString(gc.getScreenWidth() / 2, gc.getScreenHeight() - 40, String.valueOf(scores[0] + " - " + scores[1]), 1, 1, TrueTypeFont.ALIGN_CENTER);

        //        ttf.drawString(20, gc.getScreenHeight() - 60, String.valueOf(puck_velocity.x), 1, 1, TrueTypeFont.ALIGN_LEFT);
        ttf.drawString(20, gc.getScreenHeight() - 60, String.valueOf(paddles[0].getVelocity().x), 1, 1, TrueTypeFont.ALIGN_LEFT);
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
        if (button == 0 || button == 1)
            scores[button]++;
    }

    @Override
    public void keyReleased(int lwjglId, char keyChar) {
        if (lwjglId == Keyboard.KEY_ESCAPE) {
            game.setState("menu");
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

	@Override
	public void moteFound(Mote mote) {
		new WiiPaddleController(mote, paddles[0]);
		mote.addCoreButtonListener(this);
		finder.stopDiscovery();
	}

	@Override
	public void buttonPressed(CoreButtonEvent evt) {
		if (evt.isButtonPlusPressed()) {
			puck.setPosition(-10, 2, 0);
			puckController.resetVelocity();
		}
	}
	
//	Timer timer = new Timer();
//	float lastTime = timer.getTime();
//	
//	Vector2f velocity = new Vector2f(0,0); 
//	
//	@Override
//	public void accelerometerChanged(AccelerometerEvent<Mote> ae) {
//		float now = timer.getTime();
//		float dt = now - lastTime;
//		
//		System.out.println((ae.getX() - 122) + ", " + (ae.getY() - 122));
//		
//		if (Math.abs(ae.getX() - 122) > 3)
//			velocity.x += (ae.getX() - 122) * 0.5f * dt;
//		if (Math.abs(ae.getY() - 122) > 3)
//			velocity.y += (ae.getY() - 122) * 0.5f * dt;
//		
//		lastTime = now;
//	}
}
