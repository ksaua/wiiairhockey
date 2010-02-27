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
import engine.collisionsystem2D.BoundingBox;
import engine.collisionsystem2D.CollisionHandler;
import engine.collisionsystem2D.CollisionResponse;
import engine.collisionsystem2D.Collisionsystem;
import engine.utils.Line;
import engine.utils.NormalCreator;

public class CircleCollisionTest extends EmptyState implements CollisionHandler {
    
    private TrueTypeFont ttf;
    
    public void init(Engine e, GraphicContext gc) {
        
        Mouse.setCursorPosition(gc.getScreenWidth() / 2, gc.getScreenHeight() / 2);
                
        Font font = new Font("Courier New", Font.BOLD, 20);
        ttf = new TrueTypeFont(font, true);
    }
    
    @Override
    public void update(Engine e, GraphicContext gc, float dt) {
    }
    
    private float radius = 20;
    private Vector2f circ = new Vector2f(500, 400);
    private Vector2f left = new Vector2f(300, 400);
    private Vector2f right = new Vector2f(500, 400);
    
    public void render(Engine e, GraphicContext gc) {
        gc.start2dDrawing();
        
        Vector2f normal = NormalCreator.findNormal(left, right);
//        Vector2f top = new Vector2f(circ.x + normal.x * radius, circ.y + normal.y * radius);
//        Vector2f bot = new Vector2f(circ.x - normal.x * radius, circ.y - normal.y * radius);
        
        Vector2f intersection = Line.lineIntersection(
                left.x, left.y,
                right.x, right.y,
                circ.x + normal.x * radius, circ.y + normal.y * radius,
                circ.x - normal.x * radius, circ.y - normal.y * radius);
        
        float radiussq = radius * radius;
        
        if (intersection != null || distanceSquared(circ, left) < radiussq || distanceSquared(circ, right) < radiussq) {
            ttf.drawString(100, 100, "Inside", 1, 1);
        }
        
//        float asquared = (circ.x - p2x) * (circ.x - p2x) + (circ.y - p2y) * (circ.y - p2y); 
//        float bsquared = (circ.x - p1x) * (circ.x - p1x) + (circ.y - p1y) * (circ.y - p1y);
//        float csquared = (   p1x - p2x) * (   p1x - p2x) + (   p1y - p2y) * (   p1y - p2y);
//
//        
//        float b = (float) Math.sqrt(bsquared); 
//        float c = (float) Math.sqrt(csquared);
//        
//        float vinkel = (float) Math.acos((bsquared + csquared - asquared) /  (2 * b * c));
//        System.out.println(Math.toDegrees(vinkel));
//        float d = b- (float) Math.sin(vinkel);
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        drawCircle();
        
        GL11.glPushMatrix();
        GL11.glTranslatef(400, 400, 0);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2i(-102,-2);
        GL11.glVertex2i( 102,-2);
        GL11.glVertex2i( 102, 2);
        GL11.glVertex2i(-102, 2);
        GL11.glEnd();
        GL11.glPopMatrix();
        
        
//        GL11.glPushMatrix();
//        GL11.glPopMatrix(); 
//        GL11.glPushMatrix();
//        ttf.drawString(80, 100, String.valueOf(d), 1, 1);
//        GL11.glPopMatrix(); 
    }
    
    private float distanceSquared(Vector2f a, Vector2f b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);  
    }
    
    public void drawCircle() {
        GL11.glPushMatrix();
        GL11.glTranslatef(circ.x, circ.y, 0);
        
        for(int i=0; i <= 360; i++) {
            GL11.glPushMatrix();
            float x = (float) (Math.sin(i*Math.PI/180)*radius);
            float y = (float) (Math.cos(i*Math.PI/180)*radius);
            GL11.glTranslatef(x, y, 0);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2i(-2,-2);
            GL11.glVertex2i( 2,-2);
            GL11.glVertex2i( 2, 2);
            GL11.glVertex2i(-2, 2);
            GL11.glEnd();
            GL11.glPopMatrix();
        }
        
        GL11.glPopMatrix();
    }
    
    public void drawSquare(Entity e, float size) {
        GL11.glPushMatrix();
        GL11.glTranslatef(e.getPos().x, e.getPos().z, 0);
        GL11.glRotatef((float) Math.toDegrees(e.getRot().y), 0, 0, 1);
//        Square.render(size);
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
        circ.x += dx;
        circ.y += dy;
    }    
    
    @Override
    public void keyReleased(int lwjglId, char keyChar) {
        if (lwjglId == Keyboard.KEY_A) {
//            mousemoveable.increaseRotation(0, 0.5f, 0);
        }
        
        if (lwjglId == Keyboard.KEY_D) {
//            mousemoveable.increaseRotation(0,-0.5f, 0);
        }
    }
    
    public static void main(String[] args) {
        Engine e = new Engine("Test spill");
        e.addState("Meh", new CircleCollisionTest() );
        e.init();
        e.loop();
    }

    @Override
    public void collisionOccured(CollisionResponse cr) {
        System.out.println(cr.getEntity1() + ", " + cr.getNormal1() + ", " + cr.getEntity2() + ", " + cr.getNormal2());
//        collision = true;
//        normal1 = cr.getNormal1();
//        normal2 = cr.getNormal2();
    }
}
