package engine;

import engine.events.KeyboardListener;
import engine.events.MouseListener;

public interface State extends KeyboardListener, MouseListener {
	public void init(Engine e, GraphicContext gc);
	public void onEnter(Engine e, GraphicContext gc);
	public void onExit(Engine e, GraphicContext gc);
	public void update(Engine e, GraphicContext gc,float dt);
	public void render(Engine e, GraphicContext gc);
}
