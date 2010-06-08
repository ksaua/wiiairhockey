package engine.events;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

public class KeyboardEventCreator {
    LinkedList<KeyboardListener> keyboardListeners = new LinkedList<KeyboardListener>();

    public void addKeyboardListener(KeyboardListener el) {
        keyboardListeners.add(el);
    }
    
    public void removeKeyboardListener(KeyboardListener el) {
        keyboardListeners.remove(el);
    }

    public void poll() {
        Keyboard.poll();
        while (Keyboard.next()) {

            int lwjgl_id = Keyboard.getEventKey();
            char key_char = Keyboard.getEventCharacter();
            
            if (Keyboard.getEventKeyState()) {
                sendPush(lwjgl_id, key_char);
            } else {
                sendRelease(lwjgl_id, key_char);
            }
            
        }
    }

    private void sendPush(int lwjgl_id, char key_char) {
        for (KeyboardListener kl: keyboardListeners) {
            kl.keyPushed(lwjgl_id, key_char);
        }
    }

    private void sendRelease(int lwjgl_id, char key_char) {
        for (KeyboardListener kl: keyboardListeners) {
            kl.keyReleased(lwjgl_id, key_char);
        }
    }
}
