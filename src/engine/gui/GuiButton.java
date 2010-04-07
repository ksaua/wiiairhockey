package engine.gui;

import java.util.LinkedList;

import engine.TrueTypeFont;

public class GuiButton extends GuiText {

    private int width;
    private int height;
    private LinkedList<GuiButtonListener> listeners;

    public GuiButton(String text, TrueTypeFont font, int posx, int posy) {
        super(text, font, posx, posy, TrueTypeFont.ALIGN_CENTER);
        this.width = font.getWidth(text);
        this.height = font.getHeight(text);
        listeners = new LinkedList<GuiButtonListener>();
    }
    
    public void addListener(GuiButtonListener gbl) {
        listeners.add(gbl);
    }
    
    public void clicked(int x, int y) {
        if (posx -  width/2 < x && x < posx +  width/2 &&
            posy < y && y < posy + height) {
            for (GuiButtonListener gbl: listeners) {
                gbl.guiButtonClicked(this, x, y);
            }
        }
                
    }
}
