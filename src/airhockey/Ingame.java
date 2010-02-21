package airhockey;

import java.awt.Font;
import java.util.LinkedList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import engine.Camera;
import engine.EmptyState;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.Light;
import engine.Renderable;
import engine.TrueTypeFont;
import engine.collisionsystem2D.BoundingBox;
import engine.collisionsystem2D.CollisionHandler;
import engine.collisionsystem2D.Collisionsystem;

public class Ingame extends EmptyState implements CollisionHandler {

    Engine engine;

    Collisionsystem cs;

    TrueTypeFont ttf;

    LinkedList<Controller> controllers = new LinkedList<Controller>();
    
    Entity table;
    Paddle[] paddles = new Paddle[2];
    
    Entity puck;
    Camera cam;

    int[] scores = new int[2];

	Vector3f puck_velocity = new Vector3f();

    @Override
    public void init(Engine e, GraphicContext gc) {
        this.engine = e;

        cs = new Collisionsystem();
        cs.addCollisionHandler(this);

        cam = new Camera(-40f, 12f, 0);
        cam.lookAt(0, 0, 0);
        //		GL11.glEnable(GL11.GL_LIGHTING);
        //		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        //		GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

        Light light = new Light(GL11.GL_LIGHT0, true, Light.POSITIONAL, 0, 2, 0);
        light.setAmbient(0.5f, 0.5f, 0.5f, 0);
        light.setDiffuse(0.8f, 0.8f, 0.8f, 0);

        table = new Entity(0,0,0);
        paddles[0] = new Paddle(-20, 1.5f, 0);
        paddles[1] = new Paddle( 20, 1.5f, 0);
        puck = new Entity(-10, 2, 0);
        puck_velocity = new Vector3f((float)(Math.random() - 0.5) * 20, 0f, 0f );

        cs.addEntity(paddles[0], new BoundingBox(paddles[0], 2, 10));
        cs.addEntity(paddles[1], new BoundingBox(paddles[1], 2, 10));
        cs.addEntity(puck, new BoundingBox(puck, 2, 2));

        Font font = new Font("Courier New", Font.BOLD, 32);
        ttf = new TrueTypeFont(font, true);

        Renderable paddle = MediaLoader.loadObj("paddle.obj");
        paddles[0].setRenderComponent(paddle);
        paddles[1].setRenderComponent(paddle);
        
        controllers.add(new AIPaddleController(paddles[1], puck, -6, 6, -12, 0, 2));
        
        
        table.setRenderComponent(MediaLoader.loadObj("table2.obj"));
        puck.setRenderComponent(MediaLoader.loadObj("puck.obj"));
    }

    @Override
    public void render(Engine e, GraphicContext gc) {
        cam.transform();
        table.render();
        paddles[0].render();
        paddles[1].render();
        puck.render();

        gc.start2dDrawing();
        ttf.drawString(gc.getScreenWidth() / 2, gc.getScreenHeight() - 40, String.valueOf(scores[0] + " - " + scores[1]), 1, 1, TrueTypeFont.ALIGN_CENTER);
    }

    @Override
    public void update(Engine e, GraphicContext gc, float dt) {
        cs.check();
        
        puck.move(puck_velocity.x * dt, puck_velocity.y * dt, puck_velocity.z * dt);
//    	System.out.println("Pad: " + paddles[0].getVelocity());

        for (Controller c: controllers) c.update(dt);
        
    }

    @Override
    public void onEnter(Engine e, GraphicContext gc) {
        scores = new int[2];
    }

    @Override
    public void mouseMoved(int dx, int dy) {
        paddles[0].move(dy * 0.05f, 0, dx * 0.05f);
    }

    @Override
    public void mouseButtonPressed(int x, int y, int button) {
        if (button == 0 || button == 1)
            scores[button]++;
    }

    @Override
    public void keyReleased(int lwjglId, char keyChar) {
        if (lwjglId == Keyboard.KEY_ESCAPE) {
            engine.setState("menu");
        }
        
        // Reset mouse
        if (lwjglId == Keyboard.KEY_SPACE) {
            Mouse.setCursorPosition(400, 300);
            paddles[0].setPosition(-12, 1.5f, 0);
        }
    }

    @Override
    public void collisionOccured(Entity a, Entity b) {
    	if (a instanceof Paddle) {
    		paddlePuckCollision((Paddle) a, b);
    	} else if (b instanceof Paddle) {
    		paddlePuckCollision((Paddle) b, a);
    	}
    }
    
    public void paddlePuckCollision(Paddle paddle, Entity puck) {
    	Vector3f paddle_velocity = paddle.getVelocity();
    	Vector3f paddle_pos = paddle.getPos();
    	
    	boolean paddle_below_puck = paddle.getPos().x < puck.getPos().x;
    	boolean puck_going_downwards = puck_velocity.x <= 0;

    	System.out.println("puckcol " + paddle_below_puck + " .. " + puck_going_downwards);

    	
    	if (paddle_below_puck == puck_going_downwards) {
        	Vector3f tmp_pvel = new Vector3f();
        	tmp_pvel.x = -puck_velocity.x + paddle_velocity.x;
//        	tmp_pvel.y = -puck_velocity.y + paddle_velocity.y;
//        	tmp_pvel.z = -puck_velocity.z + paddle_velocity.z;
        	System.out.println("vel: " + tmp_pvel);
        	puck_velocity = tmp_pvel;
        	puck.setPosition(paddle_pos.x + 1.5f * (puck_velocity.x > 0 ? 1 : -1), puck.getPos().y, puck.getPos().z);
    	}

    }
}
