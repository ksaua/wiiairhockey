package engine.Wii;

import java.util.LinkedList;

import engine.events.Event;
import engine.events.EventListener;

import motej.Mote;
import motej.event.AccelerometerEvent;
import motej.event.AccelerometerListener;
import motej.event.CoreButtonEvent;
import motej.event.CoreButtonListener;

public class WiiEventCreator implements AccelerometerListener<Mote>, CoreButtonListener {
	private LinkedList<EventListener> listeners;
	
	public WiiEventCreator() {
		listeners = new LinkedList<EventListener>();
	}
	
	public void addListener(EventListener el) {
		listeners.add(el);
	}
	
	private void pushEvent(Event e) {
		for (EventListener el: listeners)
			el.event(e);
	}

	@Override
	public void accelerometerChanged(AccelerometerEvent<Mote> ae) {
		Event e = new Event(Event.Type.wii_accelerometer_changed);
		e.mote = ae.getSource();
		e.x = ae.getX();
		e.y = ae.getY();
		e.z = ae.getZ();
		
		pushEvent(e);
	}

	@Override
	public void buttonPressed(CoreButtonEvent ce) {
		Event e = new Event(Event.Type.wii_button_pressed);
		e.mote = ce.getSource();
		e.wii_button = ce.getButton();
		
		pushEvent(e);
	}

}
