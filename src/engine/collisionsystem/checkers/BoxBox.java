package engine.collisionsystem.checkers;

import engine.collisionsystem.BoundingBox;
import engine.collisionsystem.CollideableEntity;
import engine.collisionsystem.CollisionBounds;

public class BoxBox implements Checker {
	
	@Override
	public Class<? extends CollisionBounds> getAcceptedClass1() {
		return BoundingBox.class;
	}

	@Override
	public Class<? extends CollisionBounds> getAcceptedClass2() {
		return BoundingBox.class;
	}
	
	@Override
	public boolean collides(CollideableEntity a, CollideableEntity b) {
		BoundingBox box1 = (BoundingBox)a.getCollisionBounds();
		BoundingBox box2 = (BoundingBox)b.getCollisionBounds();
		
		float hw = box1.getWidth() / 2;
		float hh = box1.getHeight() / 2;
		
		float dx = b.getPos().x - a.getPos().x;
		float dy = b.getPos().x - a.getPos().x;

//		
//		Vector4f spherepos = new Vector4f(a.getPos().x, a.getPos().y, a.getPos().z, 0);
//		Vector4f sphereVertex = Matrix4f.transform(a.getTransformMatrix(),
//												   spherepos,null);
//		Vector4f boxpos = new Vector4f(b.getPos().x, b.getPos().y, b.getPos().z, 0);
//		Vector4f[] boxvertices = box.getTransformedVertices(b.getTransformMatrix());
//		
		return
			box2.contains(dx + hw, dy + hh) ||
			box2.contains(dx + hw, dy - hh) ||
			box2.contains(dx - hw, dy + hh) ||
			box2.contains(dx - hw, dy - hh);
	}
}
