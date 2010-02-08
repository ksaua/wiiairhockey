package engine.collisionsystem;

public class BoundingBox implements CollisionBounds {
	float width;
	float height;
		
	public BoundingBox(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public boolean contains(float x, float y) {
		return (Math.abs(x) < (width / 2)) && (Math.abs(y) < (height / 2));
	}
}
