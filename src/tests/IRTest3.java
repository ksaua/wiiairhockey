package tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import motej.Extension;
import motej.IrCameraMode;
import motej.IrCameraSensitivity;
import motej.IrPoint;
import motej.Mote;
import motej.MoteFinder;
import motej.MoteFinderListener;
import motej.event.CoreButtonEvent;
import motej.event.CoreButtonListener;
import motej.event.ExtensionEvent;
import motej.event.ExtensionListener;
import motej.event.IrCameraEvent;
import motej.event.IrCameraListener;
import motej.request.ReportModeRequest;
import motejx.extensions.motionplus.MotionPlus;
import motejx.extensions.motionplus.MotionPlusEvent;
import motejx.extensions.motionplus.MotionPlusListener;

public class IRTest3 extends JPanel implements IrCameraListener, ExtensionListener, MotionPlusListener, CoreButtonListener {

	IrPoint[] points;

	public IRTest3() {
		setPreferredSize(new Dimension(800, 600));
		setBackground(Color.WHITE);

		points = new IrPoint[4];
	}
	boolean calibrate = false;
//	IrPoint[] calibpoints = new IrPoint[2];
	float calibpointa;
	float calibpointb;
	float calibdist;
	float digitalDistPerRealDist = 0;
	float lightDist = 0.09f; // 9 cm
	float wiiDist = 1f; // 1m
	
	double yaw;
	double roll;
	
	private double getRotation(IrPoint p0, IrPoint p1) {
        if (p0.x < p1.x) {
            IrPoint temp = p1;
            p0 = p1;
            p1 = temp; 
        }
	    
        return Math.atan2(p1.y - p0.y, p1.x - p0.x);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(getWidth() / 2, getHeight() / 2);
        g2.drawString(String.valueOf((int)yaw), -300, -200);
		g2.scale(1, -1);
		
		IrPoint[] irpoints = new IrPoint[4];
        LinkedList<IrPoint> current_points = new LinkedList<IrPoint>();
		for (int i = 0; i < 4; i++) {
		    IrPoint p = this.points[i];
		    if (p != null && p.x != 1023 && p.y != 1023) {
		        irpoints[i] = p;
		        current_points.add(p);
		    }
		}

		// Find out the roll
		if (current_points.size() == 2) {
		    roll = getRotation(current_points.get(0), current_points.get(1));
		} else if (current_points.size() > 2) {
		    roll = 0;
		    IrPoint last = current_points.getLast();
		    for (IrPoint p: current_points) {
		        roll += getRotation(p, last);
		        last = p;
		    }
		    roll = roll / current_points.size();
		}
	      g2.scale(1, -1);
		g2.drawString(String.valueOf(roll), -300, 200);
	      g2.scale(1, -1);

        
		// Rotate the points to one axis
        double cos = Math.cos(roll);
        double sin = Math.sin(roll);
		
		for (int i = 0; i < 4; i++) {
		    IrPoint p = irpoints[i];
		    
		    if (p != null) {
    		    double x = 2 * (p.x / 1023.0d) - 1d;
    		    double y = 2 * (p.y / 1023.0d) - 1d;
    		    
    		    float pos = (float) (x * cos - y * sin);
                g2.setColor(Color.black);
                g2.fillOval((int) (x * 400), 0, 10, 10);
		    }
		}
	}
	MotionPlusEvent lastEvent = null;
    @Override
    public void speedChanged(MotionPlusEvent evt) {
        if (lastEvent != null) {
            double dt = (evt.getEventTime() - lastEvent.getEventTime()) / 1000d;
            yaw += evt.getYawLeftSpeed() * dt;
        }
        lastEvent = evt;
        repaint();
    }

    float pos = 0;
    IrPoint[] last_points = new IrPoint[4];
	@Override
	public void irImageChanged(IrCameraEvent ice) {
        // get average movement
        float dMov = 0;
        int amount = 0;
        for (int i = 0; i < 4; i++) {
            if (last_points[i] != null && last_points[i].x != 1023 &&
                ice.getIrPoint(i) != null && ice.getIrPoint(i).x != 1023) {
                dMov = 2 * ((ice.getIrPoint(i).x - last_points[i].x) / 1023.0f);
                amount++;
            }
        }
        
        // Set the last points
        for (int i = 0; i < 4; i++) {
            last_points[i] = ice.getIrPoint(i);
        }
        
        if (amount != 0) {
            pos += dMov / amount;
            System.out.println(pos);
        }
	    
		for (int i = 0; i < 4; i++) {
			points[i] = ice.getIrPoint(i);
		}
		repaint();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final IRTest3 irtest = new IRTest3();
		frame.add(irtest);
		frame.pack();
		frame.setVisible(true);

		MoteFinderListener listener = new MoteFinderListener() {

			public void moteFound(Mote mote) {
				System.out.println("Found mote: " + mote.getBluetoothAddress());
				mote.setReportMode(ReportModeRequest.DATA_REPORT_0x36);
				mote.addExtensionListener(irtest);
                mote.setPlayerLeds(new boolean[] {true, false, false, false} );
				mote.activateMotionPlus();
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


    @Override
    public void extensionConnected(ExtensionEvent evt) {
        System.out.println("extension" + evt.getExtension());
        Extension ext = evt.getExtension();
        if (ext instanceof MotionPlus) {
            MotionPlus motionplus = (MotionPlus) ext;
            motionplus.addMotionPlusEventListener(this);
            motionplus.newCalibration();
//            evt.getSource().enableIrCamera(IrCameraMode.BASIC, IrCameraSensitivity.INIO);
            evt.getSource().enableIrCamera();
            evt.getSource().addCoreButtonListener(this);
            evt.getSource().addIrCameraListener(this);        
            evt.getSource().setReportMode(ReportModeRequest.DATA_REPORT_0x36);
        }        
    }


    @Override
    public void extensionDisconnected(ExtensionEvent evt) {
        
    }

    @Override
    public void buttonPressed(CoreButtonEvent evt) {
        if (evt.isButtonAPressed()) {
            calibrate = true;
            yaw = 0;
        }
        
        if (evt.isButtonBPressed()) {
            MotionPlus mp = evt.getSource().getExtension();
            mp.newCalibration();
        }
    }





}
