package engine.collisionsystem2D;

import org.lwjgl.util.vector.Vector2f;

import engine.Entity;

public class BoundingBox implements Bounds {
    private Vector2f[] vertices;
    private Entity entity;
    private float width;
    private float height;

    public BoundingBox(Entity entity, float width, float height) {
        this.entity = entity;
        this.width = width;
        this.height = height;

        vertices = new Vector2f[4];

        updateVertices();
    }    

    public Vector2f[] getVertices() {
        return vertices;
    }

    @Override
    public void updateVertices() {

        float sin = (float)Math.sin(entity.getRot().y);
        float cos = (float)Math.cos(entity.getRot().y);

        float x = width / 2;
        float y = height / 2;
        float posx = entity.getPos().x;
        float posy = entity.getPos().z;
        
        // x' = x cos f - y sin f
        // y' = y cos f + x sin f
        
        vertices[0] = new Vector2f(posx + ( x) * cos - ( y) * sin, posy + ( y) * cos + ( x) * sin);
        vertices[1] = new Vector2f(posx + (-x) * cos - ( y) * sin, posy + ( y) * cos + (-x) * sin);
        vertices[2] = new Vector2f(posx + (-x) * cos - (-y) * sin, posy + (-y) * cos + (-x) * sin);
        vertices[3] = new Vector2f(posx + ( x) * cos - (-y) * sin, posy + (-y) * cos + ( x) * sin);
    }

}
