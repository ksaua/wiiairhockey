package engine;

public class Event {
	public enum Type { mouse_moved, mouse_clicked, key_pressed, key_released};
	public final Type type;
	
	public Event(Type type) {
		this.type = type;
	}
}
