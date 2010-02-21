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
        //        puck_velocity = new Vector3f((float)(Math.random() - 0.5) * 20, 0f, 0f );

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

        //        ttf.drawString(20, gc.getScreenHeight() - 60, String.valueOf(puck_velocity.x), 1, 1, TrueTypeFont.ALIGN_LEFT);
        ttf.drawString(20, gc.getScreenHeight() - 60, String.valueOf(paddles[0].getVelocity().x), 1, 1, TrueTypeFont.ALIGN_LEFT);
    }

    @Override
    public void update(Engine e, GraphicContext gc, float dt) {
        cs.check();

        // Friction
        puck_velocity.scale(1 - (0.4f * dt));

        puck.move(puck_velocity.x * dt, puck_velocity.y * dt, puck_velocity.z * dt);

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
            paddles[0].setPosition(-15, 1.5f, 0);
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
        /*
         * If puck crashed into paddle: reverse the puck's velocity and add the paddle's velocity
         * If not (the paddle pushed on the puck): set the puck's velocity to the paddle's
         * In either case, put the puck in front of the paddle by N amount. 
         */

        Vector3f paddle_velocity = paddle.getVelocity();

        Vector3f last_paddle_pos = new Vector3f(
                paddle.getPos().x - paddle_velocity.x,
                paddle.getPos().y - paddle_velocity.y,
                paddle.getPos().z - paddle_velocity.z);

        Vector3f last_puck_pos = new Vector3f(
                puck.getPos().x - puck_velocity.x,
                puck.getPos().y - puck_velocity.y,
                puck.getPos().z - puck_velocity.z);

        boolean paddle_below_puck = last_paddle_pos.x < last_puck_pos.x;
        boolean puck_going_downwards = puck_velocity.x < 0;
        boolean paddle_going_downwards = paddle_velocity.x < 0;

        boolean a = paddle_going_downwards;
        boolean b = puck_going_downwards;
        boolean c = paddle_below_puck;

        if (paddle_velocity.x == 0 || (b && c || !b && !c)) { // reverse
            puck_velocity.x = -puck_velocity.x + paddle_velocity.x;
            puck.setPosition(paddle.getPos().x + 3f * (puck_velocity.x > 0 ? 1 : -1), puck.getPos().y, puck.getPos().z);
        } else if ((!a && !b && c) || (a && !b && !c)) { // set
            puck_velocity.x = paddle_velocity.x;
            puck.setPosition(paddle.getPos().x + 3f * (puck_velocity.x > 0 ? 1 : -1), puck.getPos().y, puck.getPos().z);

        } else {
            System.out.println("This should never happen!" + Math.random());
        }
    }
}
