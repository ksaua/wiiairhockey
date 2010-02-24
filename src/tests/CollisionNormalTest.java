package tests;

import java.awt.Font;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.EmptyState;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.Renderable;
import engine.TrueTypeFont;
import engine.collisionsystem2D.BoundingBox;
import engine.collisionsystem2D.CollisionHandler;
import engine.collisionsystem2D.CollisionResponse;
import engine.collisionsystem2D.Collisionsystem;

public class CollisionNormalTest extends EmptyState implements CollisionHandler {
    public static class Square {
        public static void render(float size) {
            GL11.glPushMatrix();
            GL11.glScalef(size, size, size);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor3f(1, 0, 0);
            GL11.glVertex2i(-25, -25);
            GL11.glColor3f(0, 1, 0);
            GL11.glVertex2i(25, -25);
            GL11.glColor3f(0, 0, 1);
            GL11.glVertex2i(25, 25);
            GL11.glColor3f(0.5f, 0.5f, 0.5f);
            GL11.glVertex2i(-25, 25);
            GL11.glEnd();
            GL11.glPopMatrix();
        }
    }
    
    private Entity mousemoveable;
    private Entity stationary;
    
    private Collisionsystem cs;
    private boolean collision = false;
    private Vector2f normal1;
    private Vector2f normal2;
    
    private TrueTypeFont ttf;
    
    public void init(Engine e, GraphicContext gc) {
        
        mousemoveable = new Entity(gc.getScreenWidth() / 2, 0, gc.getScreenHeight() / 2);
        stationary = new Entity(gc.getScreenWidth() / 2, 0, gc.getScreenHeight() / 2);
        
        Mouse.setCursorPosition(gc.getScreenWidth() / 2, gc.getScreenHeight() / 2);
        
        cs = new Collisionsystem();
        cs.addEntity(mousemoveable, new BoundingBox(mousemoveable, 100, 100));
        cs.addEntity(stationary, new BoundingBox(stationary, 50, 50));
        cs.addCollisionHandler(this);
        
        mousemoveable.move(1, 0, 0);
        
        Font font = new Font("Courier New", Font.BOLD, 20);
        ttf = new TrueTypeFont(font, true);
    }
    
    @Override
    public void update(Engine e, GraphicContext gc, float dt) {
    }
    
    public void render(Engine e, GraphicContext gc) {
        gc.start2dDrawing();
        
        drawSquare(mousemoveable.getPos(), 2);
        drawSquare(stationary.getPos(), 1);
        
        cs.check();
        
        if (collision) {
            GL11.glColor3f(1, 1, 1);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            if (normal1 != null) {
                drawVector(normal1);
                ttf.drawString(50, 50, normal1.toString(), 1, 1);
            }
            if (normal2 != null) {
                drawVector(normal2);
                ttf.drawString(50, 75, normal2.toString(), 1, 1);
            }
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            
        }
        

        
    }
    
    public void drawSquare(Vector3f pos, float size) {
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.x, pos.z, 0);
        Square.render(size);
        GL11.glPopMatrix();
    }
    
    public void drawVector(Vector2f v) {
        double rot = Math.atan2(v.y, v.x);
        GL11.glPushMatrix();
        GL11.glRotatef((float) Math.toDegrees(rot), 0, 0, 1);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2i(-25, -5);
        GL11.glVertex2i(25, -5);
        GL11.glVertex2i(25, 5);
        GL11.glVertex2i(-25, 5);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    
    public void mouseMoved(int dx, int dy) {
        mousemoveable.move(dx, 0, dy);
        collision = false;
    }    
    public static void main(String[] args) {
        Engine e = new Engine("Test spill");
        e.addState("Meh", new CollisionNormalTest() );
        e.init();
        e.loop();
    }

    @Override
    public void collisionOccured(CollisionResponse cr) {
        System.out.println("Collision!");
        collision = true;
        normal1 = cr.getNormal1();
        normal2 = cr.getNormal2();
    }
}
