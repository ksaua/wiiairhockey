package engine.Wii;


import motej.Mote;
import motej.MoteFinder;
import motej.MoteFinderListener;

public class SimpleMoteFinder implements Runnable, MoteFinderListener {

	private MoteFinder finder;
	private int timeout;
	private Thread runningThread;
	private MoteFinderListener moteFinderListener;

	public void moteFound(Mote mote) {
		System.out.println("SimpleMoteFinder received notification of a found mote.");
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
			System.out.println("Sleep interrupted");
		}
		System.out.println("Stopping discovery");
//		moteFinderListener.moteFound(mote);
//		finder.stopDiscovery();
//		finder.removeMoteFinderListener(this);
		
//		this.run();
	}

}
