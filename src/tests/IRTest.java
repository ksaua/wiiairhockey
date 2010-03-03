package tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import motej.IrCameraMode;
import motej.IrCameraSensitivity;
import motej.IrPoint;
import motej.Mote;
import motej.MoteFinder;
import motej.MoteFinderListener;
import motej.event.IrCameraEvent;
import motej.event.IrCameraListener;
import motej.request.ReportModeRequest;

public class IRTest extends JPanel implements IrCameraListener {

	IrPoint[] points;

	public IRTest() {
		setPreferredSize(new Dimension(800, 600));
		setBackground(Color.WHITE);

		points = new IrPoint[4];
	}

	float xscale = 800f / 1024;
	float yscale = 600f / 1024;

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(0, getHeight());
		g2.scale(1, -1);

		if (points[0] != null && points[1] != null &&
			points[0].x == 1023 && points[0].y == 1023 &&
			points[1].x == 1023 && points[1].y == 1023) {
			g2.fillRect(0, 0, 800, 600);
		} else {
			for (IrPoint point: points ) {
				if (point != null && point.x != 1023 && point.y != 1023) {
					g2.fillOval((int)(point.x * xscale), (int)(point.y * yscale),
							point.size * 2, point.size * 2);
				}
			}
			if (points[0] != null && points[1] != null &&
				points[0].x != 1023 && points[0].y != 1023 &&
				points[1].x != 1023 && points[1].y != 1023) {
				float len = (float) Math.sqrt(Math.pow(points[0].x - points[1].x, 2) + Math.pow(points[0].y - points[1].y, 2));
				len = Math.round(len);
				g2.scale(1, -1);
				g2.drawString(String.valueOf(len), 50, -250);
			}
		}

	}

	@Override
	public void irImageChanged(IrCameraEvent ice) {
		System.out.println("Changed!");
		for (int i = 0; i < 4; i++) {
			System.out.println("POINT: " + ice.getIrPoint(i).x + ", " + ice.getIrPoint(i).y);
			points[i] = ice.getIrPoint(i);
		}
		repaint();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final IRTest irtest = new IRTest();
		frame.add(irtest);
		frame.pack();
		frame.setVisible(true);

		MoteFinderListener listener = new MoteFinderListener() {

			public void moteFound(Mote mote) {
				System.out.println("Found mote: " + mote.getBluetoothAddress());
				mote.setReportMode(ReportModeRequest.DATA_REPORT_0x3e);
				mote.enableIrCamera(IrCameraMode.FULL, IrCameraSensitivity.INIO);
				mote.addIrCameraListener(irtest);
			}

		};

		MoteFinder finder = MoteFinder.getMoteFinder();
		finder.addMoteFinderListener(listener);

		finder.startDiscovery();
		try {
			Thread.sleep(30000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finder.stopDiscovery();
	}


}
