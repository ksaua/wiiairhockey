package engine.events;

public interface KeyboardListener {
    public void keyPushed(int lwjgl_id, char key_char);
    public void keyReleased(int lwjgl_id, char key_char);
}
