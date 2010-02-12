package engine.utils;

import java.util.HashMap;
import java.util.LinkedList;

public class SubscribeableChanges {
	private HashMap<Integer, LinkedList<ChangeListener>> subscriptions = new HashMap<Integer, LinkedList<ChangeListener>>();
	
	public void addSubcription(int id, ChangeListener subscriber) {
		getListenerList(id).add(subscriber);
	}
	
	public void pushChange(int id) {
		for (ChangeListener cl: getListenerList(id)) {
			cl.change(this, id);
		}
	}
	
	private LinkedList<ChangeListener> getListenerList(int id) {
		if (!subscriptions.containsKey(id)) {
			subscriptions.put(id, new LinkedList<ChangeListener>());
		}
			
		return subscriptions.get(id);			
	}
}
