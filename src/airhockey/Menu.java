package airhockey;


import java.awt.Font;

import org.lwjgl.opengl.GL11;

import engine.Engine;
import engine.Event;
import engine.GraphicContext;
import engine.Sprite;
import engine.State;
import engine.Texture;
import engine.TextureLoader;
import engine.TrueTypeFont;


public class Menu implements State {

	Sprite background;
	Texture t;
	
	TrueTypeFont ttf;
	
	@Override
	public void event(Engine e, GraphicContext gc, Event ev) {
		
	}

	@Override
	public void init(Engine e, GraphicContext gc) {
		GL11.glEnable( GL11.GL_TEXTURE_2D );
		
		Font font = new Font("Courier New", Font.BOLD, 32);
		ttf = new TrueTypeFont(font, true);
		
		background = new Sprite(TextureLoader.loadTexture("menubg.png"));
	}

	@Override
	public void render(Engine e, GraphicContext gc) {
		gc.start2dDrawing();		
		
		GL11.glTranslatef(400, 300, 0);
		background.draw(0,0);
		
		ttf.drawString(0, 200, "Wii Airhockey", 1, 1, TrueTypeFont.ALIGN_CENTER);
	}

	@Override
	public void update(Engine e, GraphicContext gc, float dt) {}

}
