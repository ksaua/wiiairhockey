package airhockey;

import java.awt.Color;
import java.awt.Font;

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

public class Options extends EmptyState {
    
    Airhockey game;
    
    Sprite mouse;
    
    GuiButton tSplitscreen, controller1, controller2;
    
    GuiContext guicontext = new GuiContext();
    
    @Override
    public void init(final Engine e, GraphicContext gc) {
        game = (Airhockey) e;
        
        guicontext.addGuiElement(new GuiSprite(
                new Sprite(TextureLoader.loadTexture("menubg.jpg", false)),
                400, 300));
        
        Font font = new Font("Courier New", Font.BOLD, 32);
        TrueTypeFont ttf32 = new TrueTypeFont(font, true, Color.WHITE);
        
        font = new Font("Courier New", Font.BOLD, 20);
        TrueTypeFont ttf20 = new TrueTypeFont(font, true, Color.WHITE);
        
        guicontext.addGuiElement(new GuiText(
                "Instillinger", ttf32,
                gc.getScreenWidth() / 2, gc.getScreenHeight() - 70,
                TrueTypeFont.ALIGN_CENTER));

        tSplitscreen = new GuiButton("Splitscreen: Av", ttf20,
                gc.getScreenWidth() / 2, gc.getScreenHeight() - 120);

        
        tSplitscreen.addListener(new GuiButtonListener() {
            @Override
            public void guiButtonClicked(GuiButton source, int x, int y) {
                updatesplitscreen();
            }
        });        
        
        
        final Ingame ingame = (Ingame) game.getState("ingame");
        
        controller1 = new GuiButton("Kontroller for Spiller 1: " + ingame.getController(0), ttf20,
                gc.getScreenWidth() / 2, gc.getScreenHeight() - 160);
        
        controller1.addListener(new GuiButtonListener() {
            public void guiButtonClicked(GuiButton source, int x, int y) {
                ingame.changeController(0);
                updateControllers(ingame);
            }
        });
        
        controller2 = new GuiButton("Kontroller for Spiller 2: " + ingame.getController(1), ttf20,
                gc.getScreenWidth() / 2, gc.getScreenHeight() - 185);
        
        controller2.addListener(new GuiButtonListener() {
            public void guiButtonClicked(GuiButton source, int x, int y) {
                ingame.changeController(1);
                updateControllers(ingame);
            }
        });
                
        GuiButton back = new GuiButton("Gå Tilbake", ttf20,
                gc.getScreenWidth() / 2, 70);
        
        
        back.addListener(new GuiButtonListener() {
            public void guiButtonClicked(GuiButton source, int x, int y) {
                e.setState("menu");
            }});
        
        guicontext.addGuiElement(tSplitscreen);
        guicontext.addGuiElement(controller1);
        guicontext.addGuiElement(controller2);
        guicontext.addGuiElement(back);
        
        mouse = new Sprite(TextureLoader.loadTexture("mouse-red.png", false));
    }
    
    @Override
    public void render(Engine e, GraphicContext gc) {
        gc.start2dDrawing();
        guicontext.render(e, gc);
        
        GL11.glLoadIdentity();
        mouse.draw(Mouse.getX(), Mouse.getY());
    }
    boolean splitscreen = false;
    private void updatesplitscreen() {
        splitscreen = !splitscreen;
                
        tSplitscreen.setText("Splitscreen: " + (splitscreen ? "På" : "Av"));
        Ingame ingame = (Ingame) game.getState("ingame");
        ingame.toggleSplitscreen();
    }
    
    private void updateControllers(Ingame ingame) {
        controller1.setText("Kontroller for Spiller 1: " + ingame.getController(0));
        controller2.setText("Kontroller for Spiller 2: " + ingame.getController(1));
    }
    
    @Override
    public void mouseButtonPressed(int x, int y, int button) {
        guicontext.click(x, y);
    }
}
