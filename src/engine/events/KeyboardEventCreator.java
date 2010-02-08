package engine.events;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

public class KeyboardEventCreator {
	LinkedList<EventListener> eventListeners = new LinkedList<EventListener>();
	
	public void addEventListener(EventListener el) {
		eventListeners.add(el);
	}
	
	public void poll() {
		Keyboard.poll();
		while (Keyboard.next()) {
			Event e = new Event(
					Keyboard.getEventKeyState() ? 
						Event.Type.key_pressed :
						Event.Type.key_released);
			
			e.lwjgl_id = Keyboard.getEventKey();
			e.key_char = Keyboard.getEventCharacter();
			
			pushEvent(e);
		}
	}
	
	public void pushEvent(Event e) {
		for (EventListener el: eventListeners) {
			el.event(e);
		}
	}

}
