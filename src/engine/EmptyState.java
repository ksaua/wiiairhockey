package engine;

import engine.events.Event;

public abstract class EmptyState implements State {

    @Override
    public void event(Engine e, GraphicContext gc, Event ev) { }

    @Override
    public void init(Engine e, GraphicContext gc) { }

    @Override
    public void render(Engine e, GraphicContext gc) { } 

    @Override
    public void update(Engine e, GraphicContext gc, float dt) { }
    
    @Override
    public void onEnter(Engine e, GraphicContext gc) { }

    @Override
    public void onExit(Engine e, GraphicContext gc) { }

    
    @Override
    public void keyPushed(int lwjglId, char keyChar) { }

    @Override
    public void keyReleased(int lwjglId, char keyChar) { }

    @Override
    public void mouseButtonPressed(int x, int y, int button) { }

    @Override
    public void mouseButtonReleased(int x, int y, int button) { }

    @Override
    public void mouseMoved(int dx, int dy) { }
}
