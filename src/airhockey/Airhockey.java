package airhockey;


import motej.Mote;
import engine.Engine;

public class Airhockey extends Engine {
	public Airhockey(String title) {
		super(title);
	}
	
	Mote[] mote = new Mote[2];
	boolean wiienabled; 
	
	public void setMote(int id, Mote m) {
		mote[id] = m;
		System.out.println("setting mote");
	}
	
	public Mote getMote(int id) {
		return mote[id];
	}

	public static void main(String[] args) {
		Airhockey ah = new Airhockey("Airhockey");
//		ah.setUpDisplay(800, 600);
        ah.addState("ingame", new Ingame());
        ah.addState("menu", new Menu());
        ah.addState("options", new Options());
		ah.init();		
        ah.setState("menu");
		ah.loop();
	}

	public boolean isWiiEnabled() {
	    return wiienabled;
	}
    public void setWiiEnabled(boolean b) {
        wiienabled = b;
    }
}
