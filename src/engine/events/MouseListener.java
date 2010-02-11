package engine.events;

public interface MouseListener {
    public void mouseMoved(int dx, int dy);
    public void mouseButtonPressed(int x, int y, int button);
    public void mouseButtonReleased(int x, int y, int button);
}
