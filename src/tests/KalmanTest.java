package tests;

public class KalmanTest {
	
	public static float timestep = 0.01f;
	
	public static float[] accel = new float[10];
	public static float[] veloc = new float[10];
	public static float[] pos = new float[10];
	public static int k = 0;
	
	public static void pumpAccel(float a) {
		accel[k + 1] = a;
		veloc[k + 1] = veloc[k] + accel[k + 1] * timestep;
		pos[k + 1] = pos[k] + veloc[k] * timestep;
		k++;
	}
	
	public static void main(String[] args) {
		pumpAccel(5);
		pumpAccel(5);
		pumpAccel(5);
		System.out.println(pos[0] + ", " + pos[1] + ", " + pos[2] + ", " + pos[3]);
	}
}
