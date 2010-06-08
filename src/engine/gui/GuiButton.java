package engine.gui;

import java.util.LinkedList;

import engine.TrueTypeFont;

public class GuiButton extends GuiText {
    
    private int width;
    private int height;
    private LinkedList<GuiButtonListener> listeners;

    public GuiButton(String text, TrueTypeFont font, int posx, int posy) {
        super(text, font, posx, posy, TrueTypeFont.ALIGN_CENTER);
        listeners = new LinkedList<GuiButtonListener>();
        calcBoundries();
    }
    
    private void calcBoundries() {
        this.width = getFont().getWidth(getText());
        this.height = getFont().getHeight(getText());
    }
    
    public void setFont(TrueTypeFont font) {
        super.setFont(font);
        calcBoundries();
    }
    
    public void setText(String text) {
        super.setText(text);
        calcBoundries();
    }
    
    public void addListener(GuiButtonListener gbl) {
        listeners.add(gbl);
    }
    
    public void clicked(int x, int y) {
        if (enabled) {
            if (posx -  width/2 < x && x < posx +  width/2 &&
                posy < y && y < posy + height) {
                for (GuiButtonListener gbl: listeners) {
                    gbl.guiButtonClicked(this, x, y);
                }
            }
        }
    }


}
