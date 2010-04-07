package airhockey;


import java.awt.Font;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.EmptyState;
import engine.Engine;
import engine.GraphicContext;
import engine.Sprite;
import engine.TextureLoader;
import engine.TrueTypeFont;
import engine.gui.GuiButton;
import engine.gui.GuiButtonListener;
import engine.gui.GuiContext;
import engine.gui.GuiSprite;
import engine.gui.GuiText;


public class Menu extends EmptyState {

    Engine engine;

    GuiContext guicontext = new GuiContext();
    
    Sprite mouse;

    @Override
    public void init(final Engine e, GraphicContext gc) {
        this.engine = e;
        Font font = new Font("Courier New", Font.BOLD, 32);
        TrueTypeFont ttf32 = new TrueTypeFont(font, true);
        
        font = new Font("Courier New", Font.BOLD, 20);
        TrueTypeFont ttf20 = new TrueTypeFont(font, true);
        
        guicontext.addGuiElement(new GuiSprite(
                new Sprite(TextureLoader.loadTexture("menubg.jpg", false)),
                400, 300));
        
        guicontext.addGuiElement(new GuiText(
                "Wii Airhockey", ttf32,
                gc.getScreenWidth() / 2, gc.getScreenHeight() - 70,
                TrueTypeFont.ALIGN_CENTER));
        
        GuiButton start = new GuiButton("Start Game", ttf20,
                gc.getScreenWidth() / 2, gc.getScreenHeight() - 120);
        
        GuiButton options = new GuiButton("Options", ttf20,
                gc.getScreenWidth() / 2, gc.getScreenHeight() - 145);
        
        GuiButton exit = new GuiButton("Exit", ttf20,
                gc.getScreenWidth() / 2, 70);
        
        start.addListener(new GuiButtonListener() {
            public void guiButtonClicked(GuiButton source, int x, int y) {
                e.setState("ingame");
            }});
        
        options.addListener(new GuiButtonListener() {
            public void guiButtonClicked(GuiButton source, int x, int y) {
                e.setState("options");
            }});
        
        exit.addListener(new GuiButtonListener() {
            public void guiButtonClicked(GuiButton source, int x, int y) {
                e.quit();
            }});
        
        
        guicontext.addGuiElement(start);
        guicontext.addGuiElement(options);
        guicontext.addGuiElement(exit);

        mouse = new Sprite(TextureLoader.loadTexture("mouse-red.png", false));
    }

    @Override
    public void render(Engine e, GraphicContext gc) {
        gc.start2dDrawing();
        guicontext.render(e, gc);
        
        GL11.glLoadIdentity();
        mouse.draw(Mouse.getX(), Mouse.getY());
    }

    @Override
    public void keyReleased(int lwjglId, char keyChar) {
        if (lwjglId == Keyboard.KEY_G) {
            engine.setState("ingame");
        }
    }
    
    @Override
    public void mouseButtonPressed(int x, int y, int button) {
        guicontext.click(x, y);
    }
}
