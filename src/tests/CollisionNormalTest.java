package tests;

import java.awt.Font;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import engine.EmptyState;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.TrueTypeFont;
import engine.collisionsystem2D.BoundingRectangle;
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
        cs.addEntity(new BoundingRectangle(mousemoveable, 100, 100));
        cs.addEntity(new BoundingRectangle(stationary, 50, 50));
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
        
        drawSquare(mousemoveable, 2);
        drawSquare(stationary, 1);
        
        cs.check();
        
        if (collision) {
            GL11.glColor3f(1, 1, 1);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            if (normal1 != null) {
                ttf.drawString(50, 150, normal1.toString(), 1, 1);
            }
            if (normal2 != null) {

                ttf.drawString(50, 175, normal2.toString(), 1, 1);
            }
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            
            if (normal1 != null) {
                GL11.glTranslated(100f, 50f, 0);
                drawVector(normal1);
            }
            if (normal2 != null) {
                GL11.glTranslated(100f, 0, 0);
                drawVector(normal2);
            }
        }
        

        
    }
    
    public void drawSquare(Entity e, float size) {
        GL11.glPushMatrix();
        GL11.glTranslatef(e.getPos().x, e.getPos().z, 0);
        GL11.glRotatef((float) Math.toDegrees(e.getRot().y), 0, 0, 1);
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
    
    @Override
    public void keyReleased(int lwjglId, char keyChar) {
        if (lwjglId == Keyboard.KEY_A) {
            mousemoveable.increaseRotation(0, 0.5f, 0);
        }
        
        if (lwjglId == Keyboard.KEY_D) {
            mousemoveable.increaseRotation(0,-0.5f, 0);
        }
    }
    
    public static void main(String[] args) {
        Engine e = new Engine("Test spill");
        e.addState("Meh", new CollisionNormalTest() );
        e.init();
        e.loop();
    }

    @Override
    public void collisionOccured(CollisionResponse cr) {
        System.out.println(cr.getEntity1() + ", " + cr.getNormal1() + ", " + cr.getEntity2() + ", " + cr.getNormal2());
        collision = true;
        normal1 = cr.getNormal1();
        normal2 = cr.getNormal2();
    }
}
