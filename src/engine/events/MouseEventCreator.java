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
//		while (Mouse.next()) {
//			Event e = new Event(
//					Mouse.getEventButtonState() ? 
//						Event.Type.mouse_pressed :
//						Event.Type.mouse_released);
//			
//			e.lwjgl_id = Mouse.getEventButton();
//			
//			pushEvent(e);
//		}
	}
	
	public void pushEvent(Event e) {
		for (EventListener el: eventListeners) {
			el.event(e);
		}
	}
}
