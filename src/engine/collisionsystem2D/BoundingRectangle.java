package engine.collisionsystem2D;

import org.lwjgl.util.vector.Vector2f;

import engine.Entity;
import engine.utils.ChangeListener;
import engine.utils.Line;
import engine.utils.SubscribeableChanges;

public class BoundingRectangle implements Bounds, ChangeListener {
        
    private Vector2f[] vertices;
    private Line[] lines;
    private Entity entity;
    private float width;
    private float height;

    public BoundingRectangle(Entity entity, float width, float height) {
        this.entity = entity;
        this.width = width;
        this.height = height;

        vertices = new Vector2f[4];
        lines = new Line[4];
        
        for (int i = 0; i < 4; i++)
            vertices[i] = new Vector2f();
        
        for (int i = 0; i < 4; i++)
            lines[i] = new Line(null, null);
        
        entity.addSubcription(Entity.POSITION_CHANGED, this);
        entity.addSubcription(Entity.ROTATION_CHANGED, this);

        update();
    }    

    public Vector2f[] getVertices() {
        return vertices;
    }
    
    public Line[] getLines() {
        return lines;
    }

    @Override
    public void update() {

        float sin = (float)Math.sin(entity.getRot().y);
        float cos = (float)Math.cos(entity.getRot().y);

        float x = width / 2;
        float y = height / 2;
        float posx = entity.getPos().x;
        float posy = entity.getPos().z;
        
        // x' = x cos f - y sin f
        // y' = y cos f + x sin f
        
        vertices[0].x = posx + ( x) * cos - ( y) * sin;
        vertices[0].y = posy + ( y) * cos + ( x) * sin;
        
        vertices[1].x = posx + (-x) * cos - ( y) * sin;
        vertices[1].y = posy + ( y) * cos + (-x) * sin;
        
        vertices[2].x = posx + (-x) * cos - (-y) * sin;
        vertices[2].y = posy + (-y) * cos + (-x) * sin;
        
        vertices[3].x = posx + ( x) * cos - (-y) * sin;
        vertices[3].y = posy + (-y) * cos + ( x) * sin;
        
        for (int i = 0; i < 4; i++) {
            lines[i].start = vertices[i - 1 < 0 ? 3 : i - 1];
            lines[i].end = vertices[i];
        }
    }

    @Override
    public void change(SubscribeableChanges source, int id) {
        this.update();
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

}
