package tests;

import java.awt.Font;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.GraphicContext;
import engine.Renderable;
import engine.State;
import engine.TrueTypeFont;
import engine.collisionsystem.CollisionChecker;
import engine.collisionsystem.CollisionHandler;
import engine.events.Event;

public class Test implements State {

	public static class Square implements Renderable {

		@Override
		public void render() {
			GL11.glPushMatrix();			
			GL11.glBegin( GL11.GL_QUADS );                 /* Draw A Quad                      */
			GL11.glColor3f(   0.0f,  1.0f,  0.0f ); /* Set The Color To Green           */
			GL11.glVertex3f(  1.0f,  1.0f, -1.0f ); /* Top Right Of The Quad (Top)      */
			GL11.glVertex3f( -1.0f,  1.0f, -1.0f ); /* Top Left Of The Quad (Top)       */
			GL11.glVertex3f( -1.0f,  1.0f,  1.0f ); /* Bottom Left Of The Quad (Top)    */
			GL11.glVertex3f(  1.0f,  1.0f,  1.0f ); /* Bottom Right Of The Quad (Top)   */

			GL11.glColor3f(   1.0f,  0.5f,  0.0f ); /* Set The Color To Orange          */
			GL11.glVertex3f(  1.0f, -1.0f,  1.0f ); /* Top Right Of The Quad (Botm)     */
			GL11.glVertex3f( -1.0f, -1.0f,  1.0f ); /* Top Left Of The Quad (Botm)      */
			GL11.glVertex3f( -1.0f, -1.0f, -1.0f ); /* Bottom Left Of The Quad (Botm)   */
			GL11.glVertex3f(  1.0f, -1.0f, -1.0f ); /* Bottom Right Of The Quad (Botm)  */

			GL11.glColor3f(   1.0f,  0.0f,  0.0f ); /* Set The Color To Red             */
			GL11.glVertex3f(  1.0f,  1.0f,  1.0f ); /* Top Right Of The Quad (Front)    */
			GL11.glVertex3f( -1.0f,  1.0f,  1.0f ); /* Top Left Of The Quad (Front)     */
			GL11.glVertex3f( -1.0f, -1.0f,  1.0f ); /* Bottom Left Of The Quad (Front)  */
			GL11.glVertex3f(  1.0f, -1.0f,  1.0f ); /* Bottom Right Of The Quad (Front) */

			GL11.glColor3f(   1.0f,  1.0f,  0.0f ); /* Set The Color To Yellow          */
			GL11.glVertex3f(  1.0f, -1.0f, -1.0f ); /* Bottom Left Of The Quad (Back)   */
			GL11.glVertex3f( -1.0f, -1.0f, -1.0f ); /* Bottom Right Of The Quad (Back)  */
			GL11.glVertex3f( -1.0f,  1.0f, -1.0f ); /* Top Right Of The Quad (Back)     */
			GL11.glVertex3f(  1.0f,  1.0f, -1.0f ); /* Top Left Of The Quad (Back)      */

			GL11.glColor3f(   0.0f,  0.0f,  1.0f ); /* Set The Color To Blue            */
			GL11.glVertex3f( -1.0f,  1.0f,  1.0f ); /* Top Right Of The Quad (Left)     */
			GL11.glVertex3f( -1.0f,  1.0f, -1.0f ); /* Top Left Of The Quad (Left)      */
			GL11.glVertex3f( -1.0f, -1.0f, -1.0f ); /* Bottom Left Of The Quad (Left)   */
			GL11.glVertex3f( -1.0f, -1.0f,  1.0f ); /* Bottom Right Of The Quad (Left)  */

			GL11.glColor3f(   1.0f,  0.0f,  1.0f ); /* Set The Color To Violet          */
			GL11.glVertex3f(  1.0f,  1.0f, -1.0f ); /* Top Right Of The Quad (Right)    */
			GL11.glVertex3f(  1.0f,  1.0f,  1.0f ); /* Top Left Of The Quad (Right)     */
			GL11.glVertex3f(  1.0f, -1.0f,  1.0f ); /* Bottom Left Of The Quad (Right)  */
			GL11.glVertex3f(  1.0f, -1.0f, -1.0f ); /* Bottom Right Of The Quad (Right) */
			GL11.glEnd( );                            /* Done Drawing The Quad            */
			GL11.glPopMatrix();			
		}

	}

	Entity s, s2;
	Camera cam;
	TrueTypeFont ttf;

	@Override
	public void event(Engine e, GraphicContext gc, Event ev) {

	}

	@Override
	public void init(Engine e, GraphicContext gc) {
		Renderable square = new Square();
		s = new Entity(1.5f,0,0);
		s.setRenderComponent(square);
		s2 = new Entity(-1.5f,0,0);
		s2.setRenderComponent(square);


		cam = new Camera(0, 0f, 6f);
		Font font = new Font("Courier New", Font.BOLD, 32);
		ttf = new TrueTypeFont(font, true);
	}

	@Override
	public void render(Engine e, GraphicContext gc) {
		cam.transform();
		s.render();
		s2.render();
		gc.start2dDrawing();
	    GL11.glPushMatrix();
	    GL11.glColor4f(1, 1, 1, 0.7f);
	    GL11.glTranslatef(Mouse.getX(), Mouse.getY(), 0);

	    GL11.glBegin(GL11.GL_QUADS);
	    GL11.glVertex2i(-25, -25);
	    GL11.glVertex2i(25, -25);
	    GL11.glVertex2i(25, 25);
	    GL11.glVertex2i(-25, 25);
	    GL11.glEnd();

	    GL11.glPopMatrix();
	    
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		ttf.drawString(50, 50, "Heisann på degsann", 1, 1);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

	}

	@Override
	public void update(Engine e, GraphicContext gc, float dt) {
		final float pi = (float) Math.PI;
		s.increaseRotation(pi * dt, 0, 0);
		s2.increaseRotation(0, pi * dt, pi * dt);
		
//		System.out.println(dt);
	}


	public static void main(String[] args) {
		Engine e = new Engine("Test spill");
		e.addState("Meh", new Test() );
		e.init();
		e.loop();
	}
}
