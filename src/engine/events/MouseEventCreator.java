package engine.events;

import java.util.LinkedList;

import org.lwjgl.input.Mouse;

public class MouseEventCreator {

	LinkedList<EventListener> eventListeners = new LinkedList<EventListener>();

	public void addEventListener(EventListener el) {
		eventListeners.add(el);
	}
	
	public void poll() {
		Mouse.poll();
		while (Mouse.next()) {
			Event e = null;
			
			int eventButton = Mouse.getEventButton();
			
			if (eventButton == -1) { // Moved
				e = new Event(Event.Type.mouse_moved);
				e.x = Mouse.getEventX();
				e.y = Mouse.getEventY();
				e.dx = Mouse.getEventDX();
				e.dy = Mouse.getEventDY();
			} else {
				e = new Event(Mouse.getEventButtonState() ? 
						Event.Type.mouse_pressed : Event.Type.mouse_released);
				e.lwjgl_id = eventButton;
			}			
			
			pushEvent(e);
		}
	}
	
	public void pushEvent(Event e) {
		for (EventListener el: eventListeners) {
			el.event(e);
		}
	}
}
