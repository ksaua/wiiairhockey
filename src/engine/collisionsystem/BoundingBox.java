package engine.collisionsystem;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class BoundingBox implements CollisionBounds {
	float width;
	float height;
	float depth;
	
	Vector4f[] vertices;
	
	public BoundingBox(float width, float height, float depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		
		float hw = width / 2;
		float hh = height / 2;
		float hd = depth / 2;
		
		vertices = new Vector4f[8];
		vertices[0] = new Vector4f(-hw,-hh,-hd, 0);
		vertices[1] = new Vector4f( hw,-hh,-hd, 0);
		vertices[2] = new Vector4f(-hw,-hh, hd, 0);
		vertices[3] = new Vector4f( hw,-hh, hd, 0);
		vertices[4] = new Vector4f(-hw, hh,-hd, 0);
		vertices[5] = new Vector4f( hw, hh,-hd, 0);
		vertices[6] = new Vector4f(-hw, hh, hd, 0);
		vertices[7] = new Vector4f( hw, hh, hd, 0);
	}
	
	public Vector4f[] getTransformedVertices(Matrix4f m) {
		Vector4f[] transformedVertices = new Vector4f[8];
		
		for (int i = 0; i < 8; i++) {
			transformedVertices[i] = Matrix4f.transform(m, vertices[i], null);
		}
		return transformedVertices;
	}
}
