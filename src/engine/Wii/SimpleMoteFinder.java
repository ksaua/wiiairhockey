package engine.Wii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import motej.Mote;
import motej.MoteFinder;
import motej.MoteFinderListener;

public class SimpleMoteFinder implements Runnable, MoteFinderListener {

	private Logger log = LoggerFactory.getLogger(SimpleMoteFinder.class);
	private MoteFinder finder;
	private Object lock = new Object();
	private int timeout;
	private Thread runningThread;
	private MoteFinderListener moteFinderListener;

	public void moteFound(Mote mote) {
		log.info("SimpleMoteFinder received notification of a found mote.");
		moteFinderListener.moteFound(mote);
		runningThread.interrupt();
	}
	
//	public Mote findMote() {
//		if (finder == null) {
//			finder = MoteFinder.getMoteFinder();
//			finder.addMoteFinderListener(this);
//		}
//		finder.startDiscovery();
//		try {
//			synchronized(lock) {
//				lock.wait();
//			}
//		} catch (InterruptedException ex) {
//			log.error(ex.getMessage(), ex);
//		}
//		finder.stopDiscovery();
//		return mote;
//	}

	public void findMote(MoteFinderListener moteFinderListener, int timeout) {
		this.timeout = timeout;
		this.moteFinderListener = moteFinderListener;
		new Thread(this).start();
	}

	@Override
	public void run() {
		finder = MoteFinder.getMoteFinder();
		finder.addMoteFinderListener(this);
		
		finder.startDiscovery();
		runningThread = Thread.currentThread();
		System.out.println("Starting discovery, sleeping");
		try {
			Thread.sleep(timeout * 1000);
		} catch (InterruptedException e) {
			log.info("Sleep interrupted");
		}
		System.out.println("Stopping discovery");
//		moteFinderListener.moteFound(mote);
//		finder.stopDiscovery();
//		finder.removeMoteFinderListener(this);
		
//		this.run();
	}

}
