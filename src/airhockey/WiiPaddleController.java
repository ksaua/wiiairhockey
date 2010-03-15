package airhockey;

import engine.Wii.ConsistentIrPoint;
import engine.Wii.Kalman;
import motej.CalibrationDataReport;
import motej.Extension;
import motej.IrCameraMode;
import motej.IrCameraSensitivity;
import motej.IrPoint;
import motej.Mote;
import motej.event.AccelerometerEvent;
import motej.event.AccelerometerListener;
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

public class WiiPaddleController implements IrCameraListener, CoreButtonListener, AccelerometerListener<Mote>, ExtensionListener, MotionPlusListener {

	private Paddle paddle;
	
//	private Kalman kalman = new Kalman();
	
//	private int zeroX;
//	private int gravX;
	
	private double yaw;
	private final float lightDist = 0.09f;
	private final float wiiDist = 1f;
	private float digitalDistPerRealDist = 0;
	
	private boolean calibrate;
	
	public WiiPaddleController(Mote mote, Paddle paddle) {
		this.paddle = paddle;

		System.out.println("doing stuff to mote");
		while (mote.getStatusInformationReport() == null) {
			System.out.println("waiting for status information report");
			try {
				Thread.sleep(10l);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(mote.getStatusInformationReport());
		while (mote.getCalibrationDataReport() == null) {
			System.out.println("waiting for calibration data report");
			try {
				Thread.sleep(10l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		CalibrationDataReport cali = mote.getCalibrationDataReport();
		System.out.println(cali);
//		zeroX = cali.getZeroX();
//		gravX = cali.getGravityX();
//        mote.setReportMode(ReportModeRequest.DATA_REPORT_0x3e);
//        mote.enableIrCamera(IrCameraMode.FULL, IrCameraSensitivity.INIO);
//        mote.rumble(200);
//        mote.addCoreButtonListener(this);
//        mote.addIrCameraListener(this);
        mote.addExtensionListener(this);
		mote.setPlayerLeds(new boolean[] {true, false, false, false} );
        mote.activateMotionPlus();
	}
	
	IrPoint[] lastPoints = new IrPoint[2];
	IrPoint[] calibpoints = new IrPoint[2];
	float calibdist;
	@Override
	public void irImageChanged(IrCameraEvent ice) {
	    IrPoint[] points = new IrPoint[2];
	    for (int i = 0; i < 2; i++) points[i] = ice.getIrPoint(i);
	    
	    if (!(points[0] == null || points[1] == null || 
	            points[0].x == 1023 || points[0].y == 1023 ||
	            points[1].x == 1023 || points[1].y == 1023)) {
	        
	        if (points[0].x < points[1].x) {
                IrPoint temp = points[0];
                points[0] = points[1];
                points[1] = temp; 
            }
	        
	        IrPoint avgPoint0 = new IrPoint(
	                (points[0].x + lastPoints[0].x) / 2,
	                (points[0].y + lastPoints[0].y) / 2);
	        
	        IrPoint avgPoint1 = new IrPoint(
                    (points[1].x + lastPoints[1].x) / 2,
                    (points[1].y + lastPoints[1].y) / 2);
	        
	    
	        double phi = Math.atan2(points[1].y - points[0].y, points[1].x - points[0].x);
	        double cos = Math.cos(phi);
	        double sin = Math.sin(phi);

	        double x1 = 2 * (avgPoint0.x / 1023.0d) - 1d;
	        double y1 = 2 * (avgPoint0.y / 1023.0d) - 1d;

	        double x2 = 2 * (avgPoint1.x / 1023.0d) - 1d;
	        double y2 = 2 * (avgPoint1.y / 1023.0d) - 1d;

	        float posa = (float) (x1 * cos - y1 * sin);
	        float posb = (float) (x2 * cos - y2 * sin);

	        //      if (posa > posb) {
	        //          float t = posb;
	        //          posb = posa;
	        //          posa = t;
	        //      }

	        float dist = posb - posa;
	        if (calibpoints[0] != null && calibpoints[1] != null) {
	            int diff = 
	                Math.abs(calibpoints[0].x - points[0].x) +
	                Math.abs(calibpoints[0].y - points[0].y) +
	                Math.abs(calibpoints[1].x - points[1].x) +
	                Math.abs(calibpoints[1].y - points[1].y);

	            if (diff < 20) {
	                System.out.println("DIFF!!" + Math.random());
	                yaw = yaw * 0.75;
	            }
	        }
	        if (calibrate) {
	            System.out.println("Calibrating");
	            digitalDistPerRealDist = dist / lightDist; 
	            System.out.println("DIST: " + dist);
	            yaw = 0;
	            calibpoints[0] = points[0];
	            calibpoints[1] = points[1];
	            calibdist = dist;
	            calibrate = false;
	        }
	         
	        float yawTranslate = (float) (Math.tan(Math.toRadians(yaw)) * wiiDist * digitalDistPerRealDist);
	        if (calibpoints[0] != null && calibpoints[1] != null) {
//	            double dist1 = Math.sqrt(Math.pow(calibpoints[0].x - calibpoints[1].x,2)
//	                                   + Math.pow(calibpoints[0].y - calibpoints[1].y,2));
//	            double dist2 = Math.sqrt(Math.pow(avgPoint0.x - avgPoint1.x,2)
//	                                     + Math.pow(avgPoint0.y - avgPoint1.y,2));
	            
//	            System.out.println(dist1 + " " + dist2);
//	            paddle.setPosition((float)(-24 + 0.6 * (dist2 - dist1)), 1.5f, paddle.getPos().z);
	            System.out.println(dist + ", " + calibdist);
	            paddle.setPosition((float)(-24 + 200 * (dist - calibdist)), 1.5f, paddle.getPos().z);
	        }
	        float pos = (posa + posb) / 2 + yawTranslate;
//	        System.out.println(yaw + ", " + yawTranslate);
	        paddle.setPosition(paddle.getPos().x, 1.5f, pos * 15f);
	        paddle.setYaw((float) -Math.toRadians(yaw) * 2);
	    }
	    lastPoints[0] = points[0];
	    lastPoints[1] = points[1];
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
		if (evt.isButtonHomePressed()) {
			synchronized (paddle) {
				paddle.setPosition(-24, 1.5f, 0);
//				kalman.reset();
			}
		}
	}

	@Override
	public void accelerometerChanged(AccelerometerEvent<Mote> ae) {
		synchronized (paddle) {
//			paddle.setPosition(-20f, 1.5f, kalman.pushAccel(ae.getX() - zeroX) * 10f);
		}
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

	MotionPlusEvent lastEvent;
	@Override
	public void speedChanged(MotionPlusEvent evt) {
		if (lastEvent != null) {
			float dt = (evt.getEventTime() - lastEvent.getEventTime()) / 1000f;
            yaw += evt.getYawLeftSpeed() * dt;
//			paddle.increaseRotation(0, (float)lastEvent.getYawLeftSpeed() * (-1/90f) * dt, 0);
		}		
		lastEvent = evt;
	}

}
