package engine.events;

import motej.Mote;

public class Event {
	public enum Type {
		key_pressed, key_released,
		mouse_pressed, mouse_released, mouse_moved,
		mote_connected, mote_disconnected,
		wii_accelerometer_changed, wii_button_pressed,
	};
		
	public final Type type;
	public int x, y, z;
	public Mote mote;
	public int wii_button;
	public int lwjgl_id;
	public char key_char;
	
	public Event(Type type) {
		this.type = type;
	}
}
