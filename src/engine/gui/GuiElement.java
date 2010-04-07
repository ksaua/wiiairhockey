package engine.gui;

import engine.Renderable;

public abstract class GuiElement implements Renderable {
    
    protected int posx;
    protected int posy;

    public GuiElement() {
        posx = 0;
        posy = 0;
    }
    
    public GuiElement(int posx, int posy) {
        this.posx = posx;
        this.posy = posy;
    }
}
