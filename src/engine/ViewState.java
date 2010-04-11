package engine;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import engine.gui.GuiContext;

/**
 * ViewState uses the Frame Buffer Object to be able to
 * split the screen and draw a GUI on top of it.
 * @author canute
 *
 */

public abstract class ViewState extends EmptyState {
    
    private GuiContext guicontext;
    private LinkedList<View> views = new LinkedList<View>();

    public void setGuiContext(GuiContext gc) {
        this.guicontext = gc;
    }
    
    public GuiContext getGuiContext() {
        return guicontext;
    }
    
    public void addView(View view) {
        views.add(view);
    }
    
    @Override
    public void render(Engine e, GraphicContext gc) {
        // Render views
        for (View view: views) {
            GL11.glLoadIdentity();
            view.preRender();
            renderView(view, e, gc);
            view.postRender();
        }
        
        gc.start2dDrawing();
        
        for (View view: views) {
            view.drawView();
        }
        
        // Render gui on top of views
        if (guicontext != null) guicontext.render(e, gc);
    }
    
    public abstract void renderView(View view, Engine e, GraphicContext gc);
    
}
