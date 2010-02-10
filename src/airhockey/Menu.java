package airhockey;


import java.awt.Font;

import motej.Mote;
import motej.MoteFinderListener;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.EmptyState;
import engine.Engine;
import engine.GraphicContext;
import engine.Sprite;
import engine.TextureLoader;
import engine.TrueTypeFont;
import engine.Wii.SimpleMoteFinder;
import engine.events.Event;


public class Menu extends EmptyState {

	Sprite background;
	Sprite mouse;
	
	TrueTypeFont ttf;
	
	@Override
	public void event(Engine e, GraphicContext gc, Event ev) {
		//if event
		//somethingsomething.startDiscovery();
		
		//if motefindevent
		
		if (ev.type == Event.Type.wii_accelerometer_changed) {
//			System.out.println(ev.x + ", " + ev.y + ", " + ev.z);
		} else if (ev.type == Event.Type.wii_button_pressed) {
			System.out.println(ev.wii_button);
		}
		
		if (ev.type == Event.Type.key_released && ev.lwjgl_id == Keyboard.KEY_G) {
			e.setState("ingame");
		}
	}

	@Override
	public void init(Engine e, GraphicContext gc) {		
		Font font = new Font("Courier New", Font.BOLD, 32);
		ttf = new TrueTypeFont(font, true);
		
		background = new Sprite(TextureLoader.loadTexture("menubg.jpg"));
		mouse = new Sprite(TextureLoader.loadTexture("mouse-red.png"));
		
		final Airhockey ah = (Airhockey) e;
		SimpleMoteFinder smf = new SimpleMoteFinder();
		smf.findMote(new MoteFinderListener() {
			@Override
			public void moteFound(Mote mote) {
				ah.initializeMote(mote);
				ah.setMote(0, mote);
			}
		}, 30);
		

	}

	@Override
	public void render(Engine e, GraphicContext gc) {
		
		gc.start2dDrawing();		
		GL11.glTranslatef(400, 300, 0);
		background.draw(0,0);
		
		
		ttf.drawString(0, 200, "Wii Airhockey", 1, 1, TrueTypeFont.ALIGN_CENTER);
		
		GL11.glLoadIdentity();
		
		mouse.draw(Mouse.getX(), Mouse.getY());
	}
}
