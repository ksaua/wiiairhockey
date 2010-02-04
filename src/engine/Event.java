package engine;

import motej.Mote;

public class Event {
	public enum Type {
		mouse_moved, mouse_clicked,
		key_pressed, key_released,
		mote_connected, mote_disconnected,
		wii_accelerometer_changed, wii_button_pressed};
		
	public final Type type;
	public int x, y, z;
	public Mote mote;
	public int wii_button;
	
	public Event(Type type) {
		this.type = type;
	}
}
