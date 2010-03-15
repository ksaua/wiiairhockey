package tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

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

public class IRTest2 extends JPanel implements IrCameraListener, ExtensionListener, MotionPlusListener, CoreButtonListener {

	IrPoint[] points;

	public IRTest2() {
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


	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(getWidth() / 2, getHeight() / 2);
        g2.drawString(String.valueOf((int)yaw), -300, -200);
		g2.scale(1, -1);
		if (points[0] == null || points[1] == null || (
			points[0].x == 1023 && points[0].y == 1023 &&
			points[1].x == 1023 && points[1].y == 1023)) {
		} else {
			if (points[0].x != 1023 && points[1].x != 1023) {
			    
			    if (points[0].x < points[1].x) {
                    IrPoint temp = points[0];
                    points[0] = points[1];
                    points[1] = temp; 
                }
			    
			    double phi = Math.atan2(points[1].y - points[0].y, points[1].x - points[0].x);
			    
			    double cos = Math.cos(phi);
			    double sin = Math.sin(phi);
			    
			    double x1 = 2 * (points[0].x / 1023.0d) - 1d;
			    double y1 = 2 * (points[0].y / 1023.0d) - 1d;
			    
			    double x2 = 2 * (points[1].x / 1023.0d) - 1d;
			    double y2 = 2 * (points[1].y / 1023.0d) - 1d;
			    
			    float posa = (float) (x1 * cos - y1 * sin);
			    float posb = (float) (x2 * cos - y2 * sin);
			    

			    
			    float dist = posb - posa;
			    if (calibpointa != 0 && calibpointb != 0) {
			        float diff = 
			            Math.abs(calibpointa - posa) +
			            Math.abs(calibpointb - posb) +
			            Math.abs(calibdist - dist);
			        
			        if (diff < 0.1f) {
			            System.out.println("DIFF!!" + Math.random());
			            yaw = yaw * 0.9;
			        }
			    }
			    if (calibrate) {
		            System.out.println("Calibrating");
			        digitalDistPerRealDist = dist / lightDist; 
			        System.out.println("DIST: " + dist);
			        yaw = 0;
			        calibpointa = posa;
			        calibpointb = posb;
			        calibdist = dist;
			        calibrate = false;
			    }
			    
			    float yawTranslate = (float) (Math.tan(Math.toRadians(yaw)) * wiiDist * digitalDistPerRealDist);
			    
			    float pos = (posa + posb) / 2 + yawTranslate; 
			    
//			    System.out.println(posa + " + " + posb + ": " + yawTranslate);
			    g2.setColor(Color.black);
			    g2.fillOval((int) (posa * 400), 0, 10, 10);
			    g2.setColor(Color.green);
			    g2.fillOval((int) (posb * 400), 0, 10, 10);
			    g2.setColor(Color.red);
			    g2.fillOval((int) (pos * 400), 0, 10, 10);
			    			    
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

	@Override
	public void irImageChanged(IrCameraEvent ice) {
		for (int i = 0; i < 4; i++) {
			points[i] = ice.getIrPoint(i);
		}
		repaint();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final IRTest2 irtest = new IRTest2();
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
