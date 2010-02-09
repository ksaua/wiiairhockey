package tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.Engine;
import engine.GraphicContext;
import engine.Renderable;
import engine.State;
import engine.Texture;
import engine.TextureLoader;
import engine.events.Event;

public class TextureTest implements State {

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

	Texture tex;
	@Override
	public void event(Engine e, GraphicContext gc, Event ev) {

	}

	@Override
	public void init(Engine e, GraphicContext gc) {
		GL11.glEnable( GL11.GL_TEXTURE_2D );

		tex = TextureLoader.loadTexture("chesstex.jpg");
	}

	@Override
	public void render(Engine e, GraphicContext gc) {
		gc.start2dDrawing();
	    
		GL11.glTranslatef(Mouse.getX(), Mouse.getY(), 0);

		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glScalef(Mouse.getX() / 100, Mouse.getY() / 100, 0);
		GL11.glTranslatef(60, 0, 0);
		
	    GL11.glBegin( GL11.GL_QUADS );
	    GL11.glTexCoord2d(0.0,0.0); GL11.glVertex2d(-50.0,-50.0);
	    GL11.glTexCoord2d(1.0,0.0); GL11.glVertex2d( 50.0,-50.0);
	    GL11.glTexCoord2d(1.0,1.0); GL11.glVertex2d( 50.0, 50.0);
	    GL11.glTexCoord2d(0.0,1.0); GL11.glVertex2d(-50.0, 50.0);
	    GL11.glEnd();
	}

	@Override
	public void update(Engine e, GraphicContext gc, float dt) {
	}


	public static void main(String[] args) {
		Engine e = new Engine("Test spill");
		e.addState("Meh", new TextureTest() );
		e.init();
		e.loop();
	}

	@Override
	public void onEnter(Engine e, GraphicContext gc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExit(Engine e, GraphicContext gc) {
		// TODO Auto-generated method stub
		
	}
}
