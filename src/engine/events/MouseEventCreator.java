package engine.events;

import java.util.LinkedList;

import org.lwjgl.input.Mouse;

public class MouseEventCreator {

    LinkedList<MouseListener> mouseListeners = new LinkedList<MouseListener>();

    public void addMouseListener(MouseListener ml) {
        mouseListeners.add(ml);
    }
    
    public void removeMouseListener(MouseListener ml) {
        mouseListeners.remove(ml);
    }

    public void poll() {
        Mouse.poll();
        while (Mouse.next()) {

            int eventButton = Mouse.getEventButton();

            if (eventButton == -1) {
                sendMove(Mouse.getEventDX(), Mouse.getEventDY());
            } else {
                
                int x = Mouse.getEventX();
                int y = Mouse.getEventY();
                
                if (Mouse.getEventButtonState()) {
                    sendPush(x, y, eventButton);
                } else {
                    sendRelease(x, y, eventButton);
                }

            }			

        }
    }

    private void sendMove(int dx, int dy) {
        for (MouseListener ml: mouseListeners) {
            ml.mouseMoved(dx, dy);
        }
    }

    private void sendPush(int x, int y, int button) {
        for (MouseListener ml: mouseListeners) {
            ml.mouseButtonPressed(x, y, button);
        }
    }
    private void sendRelease(int x, int y, int button) {
        for (MouseListener ml: mouseListeners) {
            ml.mouseButtonReleased(x, y, button);
        }
    }
}
