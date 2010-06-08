package airhockey;

import motej.CalibrationDataReport;
import motej.Extension;
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

//	private Paddle paddle;
    private PaddlePositionBuffer pos_buffer;
	
//	private Kalman kalman = new Kalman();
	
//	private int zeroX;
//	private int gravX;
	
	private double yaw;
	private final float lightDist = 0.09f;
	private final float wiiDist = 1f;
	private float digitalDistPerRealDist = 0;
	
	private boolean calibrate;
	
	public WiiPaddleController(Mote mote, PaddlePositionBuffer pb) {
	    pos_buffer = pb;
	    
		System.out.println("doing stuff to mote");
		while (mote.getStatusInformationReport() == null) {
			System.out.println("waiting for status information report");
			try {
				Thread.sleep(10l);
			} catch (InterruptedException e) {
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
     
        wPoints = new WeightedPoint[] {new WeightedPoint(), new WeightedPoint()}; 
	}
	
//	float pos = 0;
//	IrPoint[] last_points = new IrPoint[4];
//	@Override
//	public void irImageChanged(IrCameraEvent ice) {
	    // get average movement
//	    float dMov = 0;
//	    int amount = 0;
//	    for (int i = 0; i < 4; i++) {
//	        if (last_points[i] != null && last_points[i].x != 1023 &&
//	            ice.getIrPoint(i) != null && ice.getIrPoint(i).x != 1023) {
//	            dMov = 2 * ((ice.getIrPoint(i).x - last_points[i].x) / 1023.0f);
//	            amount++;
//	        }
//	    }
//	    
//	    // Set the last points
//	    for (int i = 0; i < 4; i++) {
//	        last_points[i] = ice.getIrPoint(i);
//	    }
//	    
//	    if (amount != dMov) {
//            pos += dMov / amount;
//            pos_buffer.goToZ(pos * 15f);
//        }
//	}
	
	IrPoint[] calibpoints = new IrPoint[2];
	
	WeightedPoint[] wPoints;
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
	        
	        wPoints[0].pushPoint(points[0]);
	        wPoints[1].pushPoint(points[1]);
	        
//	        IrPoint avgPoint0 = new IrPoint(
//	                (points[0].x + lastPoints[0].x) / 2,
//	                (points[0].y + lastPoints[0].y) / 2);
//	        
//	        IrPoint avgPoint1 = new IrPoint(
//                    (points[1].x + lastPoints[1].x) / 2,
//                    (points[1].y + lastPoints[1].y) / 2);
	        
	    
	        double phi = Math.atan2(wPoints[1].getWeightedY() - wPoints[0].getWeightedY(),
	                wPoints[1].getWeightedX() - wPoints[0].getWeightedX());
	        double cos = Math.cos(phi);
	        double sin = Math.sin(phi);

	        double x1 = 2 * (wPoints[0].getWeightedX() / 1023.0d) - 1d;
	        double y1 = 2 * (wPoints[0].getWeightedY() / 1023.0d) - 1d;

	        double x2 = 2 * (wPoints[1].getWeightedX() / 1023.0d) - 1d;
	        double y2 = 2 * (wPoints[1].getWeightedY() / 1023.0d) - 1d;

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
//	            System.out.println(dist + ", " + calibdist);
	            pos_buffer.goToX((float)(-24 + 200 * (dist - calibdist)));
//	            paddle.setPosition(, 1.5f, paddle.getPos().z);
	        }
	        float pos = (posa + posb) / 2 + yawTranslate;
//	        System.out.println(yaw + ", " + yawTranslate);
	        pos_buffer.goToZ(pos * 15f);
//	        paddle.setPosition(paddle.getPos().x, 1.5f, pos * 15f);
	        pos_buffer.goToYaw((float) -Math.toRadians(yaw) * 2);
//	        paddle.setYaw((float) -Math.toRadians(yaw) * 2);
	    }
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

	@Override
	public void accelerometerChanged(AccelerometerEvent<Mote> ae) {
//		synchronized (pos_buffer) {
//			paddle.setPosition(-20f, 1.5f, kalman.pushAccel(ae.getX() - zeroX) * 10f);
//		}
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
