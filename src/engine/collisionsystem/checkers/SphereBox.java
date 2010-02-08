package engine.collisionsystem.checkers;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import engine.Entity;
import engine.collisionsystem.BoundingBox;
import engine.collisionsystem.BoundingSphere;
import engine.collisionsystem.CollisionBounds;
import engine.collisionsystem.CollideableEntity;

public abstract class SphereBox implements Checker {
	
	@Override
	public Class<? extends CollisionBounds> getAcceptedClass1() {
		return BoundingSphere.class;
	}

	@Override
	public Class<? extends CollisionBounds> getAcceptedClass2() {
		return BoundingBox.class;
	}
	
	@Override
	public boolean collides(CollideableEntity a, CollideableEntity b) {
		BoundingSphere sphere = (BoundingSphere)a.getCollisionBounds();
		BoundingBox box = (BoundingBox)b.getCollisionBounds();
//		
//		Vector4f spherepos = new Vector4f(a.getPos().x, a.getPos().y, a.getPos().z, 0);
//		Vector4f sphereVertex = Matrix4f.transform(a.getTransformMatrix(),
//												   spherepos,null);
//		Vector4f boxpos = new Vector4f(b.getPos().x, b.getPos().y, b.getPos().z, 0);
//		Vector4f[] boxvertices = box.getTransformedVertices(b.getTransformMatrix());
//		
		return false;
	}

}
