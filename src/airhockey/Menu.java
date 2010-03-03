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


public class Menu extends EmptyState {

    Engine engine;

    Sprite background;
    Sprite mouse;

    TrueTypeFont ttf;

    @Override
    public void init(Engine e, GraphicContext gc) {
        this.engine = e;
        Font font = new Font("Courier New", Font.BOLD, 32);
        ttf = new TrueTypeFont(font, true);

        background = new Sprite(TextureLoader.loadTexture("menubg.jpg", false));
        mouse = new Sprite(TextureLoader.loadTexture("mouse-red.png", false));
    }

    @Override
    public void render(Engine e, GraphicContext gc) {

        gc.start2dDrawing();		
        background.draw(400,300);	

        ttf.drawString(gc.getScreenWidth() / 2, gc.getScreenHeight() - 70, "Wii Airhockey", 1, 1, TrueTypeFont.ALIGN_CENTER);

        GL11.glLoadIdentity();

        mouse.draw(Mouse.getX(), Mouse.getY());
    }

    @Override
    public void keyReleased(int lwjglId, char keyChar) {
        if (lwjglId == Keyboard.KEY_G) {
            engine.setState("ingame");
        }
    }
}
