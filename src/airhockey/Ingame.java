package airhockey;

import java.awt.Font;
import java.util.LinkedList;

import org.lwjgl.input.Keyboard;
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
        paddles[0] = new Paddle(-12, 1.5f, 0);
        paddles[1] = new Paddle( 12, 1.5f, 0);
        puck = new Entity(-10, 2, 0);

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
    }

    @Override
    public void collisionOccured(Entity a, Entity b) {
        System.out.println("COLLIDING");
    }
}
