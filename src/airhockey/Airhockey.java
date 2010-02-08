package airhockey;


import motej.Mote;
import motej.request.ReportModeRequest;
import engine.Engine;
import engine.Wii.WiiEventCreator;

public class Airhockey extends Engine {
	public Airhockey(String title) {
		super(title);
	}
	
	public void setMote(int id, Mote mote) {
		System.out.println("setting mote");
	}
	
	public void initializeMote(Mote mote) {
		mote.setReportMode(ReportModeRequest.DATA_REPORT_0x31);

		WiiEventCreator wec = new WiiEventCreator();
		wec.addListener(this);
		
		mote.addAccelerometerListener(wec);
		mote.addCoreButtonListener(wec);
	}

	public static void main(String[] args) {
		Airhockey ah = new Airhockey("Airhockey");
//		ah.setUpDisplay(800, 600);
		ah.addState("ingame", new Ingame());
		ah.addState("menu", new Menu());
		ah.addState("options", new Options());
		ah.init();
		ah.loop();
	}
}
