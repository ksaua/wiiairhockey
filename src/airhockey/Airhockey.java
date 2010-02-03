package airhockey;


import engine.Engine;

public class Airhockey extends Engine {
	public Airhockey(String title) {
		super(title);
	}

	public static void main(String[] args) {
		Airhockey ah = new Airhockey("Airhockey");
//		ah.setUpDisplay(800, 600);
		ah.addState("menu", new Menu());
		ah.addState("ingame", new Ingame());
		ah.addState("options", new Options());
		ah.init();
		ah.loop();
	}
}
