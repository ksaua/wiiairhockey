package airhockey;


import java.awt.Font;

import motej.Mote;
import motej.request.ReportModeRequest;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.Engine;
import engine.Event;
import engine.GraphicContext;
import engine.Sprite;
import engine.State;
import engine.TextureLoader;
import engine.TrueTypeFont;
import engine.Wii.SimpleMoteFinder;
import engine.Wii.WiiEventCreator;


public class Menu implements State {

	Sprite background;
	Sprite mouse;
	
	TrueTypeFont ttf;
	
	Mote mote;
	
	
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
	}

	@Override
	public void init(Engine e, GraphicContext gc) {
		GL11.glEnable( GL11.GL_TEXTURE_2D );
		
		Font font = new Font("Courier New", Font.BOLD, 32);
		ttf = new TrueTypeFont(font, true);
		
		background = new Sprite(TextureLoader.loadTexture("menubg.jpg"));
		mouse = new Sprite(TextureLoader.loadTexture("mouse-red.png"));
		
		
		try {
			SimpleMoteFinder smf = new SimpleMoteFinder();
			mote = smf.findMote();
			mote.setReportMode(ReportModeRequest.DATA_REPORT_0x31);

			WiiEventCreator wec = new WiiEventCreator();
			wec.addListener(e);
			
			mote.addAccelerometerListener(wec);
			mote.addCoreButtonListener(wec);
			
		} catch (Exception ex) {
			System.out.println("hei");
		}

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

	@Override
	public void update(Engine e, GraphicContext gc, float dt) {}

}
