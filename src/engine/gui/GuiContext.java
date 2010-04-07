package engine.gui;

import java.util.LinkedList;

import engine.Engine;
import engine.GraphicContext;

public class GuiContext {

    private LinkedList<GuiElement> elements = new LinkedList<GuiElement>();
    private LinkedList<GuiButton> buttons = new LinkedList<GuiButton>();
    
    public void addGuiElement(GuiElement ge) {
        if (ge instanceof GuiButton) buttons.add((GuiButton) ge);
        elements.add(ge);
    }
    
    public void removeGuiElement(GuiElement ge) {
        if (ge instanceof GuiButton) buttons.remove((GuiButton) ge);
        elements.remove(ge);
    }
    
    public void click(int x, int y) {
        for (GuiButton gc: buttons) {
            gc.clicked(x, y);
        }
    }
    
    public void render(Engine e, GraphicContext gc) {
        for (GuiElement ge: elements) {
            ge.render();
        }
    }
}
