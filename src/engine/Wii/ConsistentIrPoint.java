package engine.Wii;

public class ConsistentIrPoint {
	
	public int posx, posy;
	public int oldx, oldy;
	public boolean didmove;
	
	public ConsistentIrPoint(int x, int y) {
		this.posx = x;
		this.posy = y;
		this.oldx = x;
		this.oldx = y;
	}

	public void moveTo(int x, int y) {
		oldx = posx;
		oldy = posy;
		
		posx = x;
		posy = y;
	}
}
