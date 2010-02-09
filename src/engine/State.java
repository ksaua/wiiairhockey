package engine;

import engine.events.Event;

public interface State {
	public void init(Engine e, GraphicContext gc);
	public void onEnter(Engine e, GraphicContext gc);
	public void onExit(Engine e, GraphicContext gc);
	public void event(Engine e, GraphicContext gc, Event ev);
	public void update(Engine e, GraphicContext gc,float dt);
	public void render(Engine e, GraphicContext gc);
}
