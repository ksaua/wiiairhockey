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
	
	private Kalman kalman = new Kalman();
	
	private int zeroX;
	private int gravX;
	
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
		zeroX = cali.getZeroX();
		gravX = cali.getGravityX();
//        mote.setReportMode(ReportModeRequest.DATA_REPORT_0x3e);
//        mote.enableIrCamera(IrCameraMode.FULL, IrCameraSensitivity.INIO);
//        mote.rumble(200);
//        mote.addCoreButtonListener(this);
//        mote.addIrCameraListener(this);
        mote.addExtensionListener(this);
		mote.setPlayerLeds(new boolean[] {true, false, false, false} );
        mote.activateMotionPlus();
	}
	
	ConsistentIrPoint[] points = new ConsistentIrPoint[2];
	@Override
	public void irImageChanged(IrCameraEvent ice) {
		boolean somethingMoved = false;
		
		for (int i = 0; i < 2; i++) {
			IrPoint point = ice.getIrPoint(i);
			
			if (point.x == 1023 && point.y == 1023) {
				points[i] = null;
			}
			else if (points[i] == null) {
				points[i] = new ConsistentIrPoint(point.x, point.y);
			} else {
				points[i].moveTo(point.x, point.y);
				points[i].didmove = true;
				somethingMoved = true;
			}
		}
		
		if (somethingMoved)
			meh();
		
		for (int i = 0; i < 2; i++) {
			if (points[i] != null) points[i].didmove = false;
		}		
	}
	
	public synchronized void meh() {
		ConsistentIrPoint cip = points[0] != null ? points[0] : points[1];
		
		if (cip != null && cip.didmove) {
			float dx = cip.oldx - cip.posx;
			paddle.move(0, 0, dx * 0.025f);
		}
		
		if (points[0] != null && points[1] != null &&
			points[0].didmove && points[1].didmove) {
			float len1 = (float) Math.sqrt(Math.pow(points[0].posx - points[1].posx, 2) + Math.pow(points[0].posy - points[1].posy, 2));
			float len2 = (float) Math.sqrt(Math.pow(points[0].oldx - points[1].oldx, 2) + Math.pow(points[0].oldy - points[1].oldy, 2));
			
			paddle.move(-(len2 - len1) * 0.5f, 0, 0);
		}
	}	

	@Override
	public void buttonPressed(CoreButtonEvent evt) {
		if (evt.isButtonHomePressed()) {
			synchronized (paddle) {
				paddle.setPosition(-20, 1.5f, 0);
				kalman.reset();
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
			evt.getSource().addAccelerometerListener(this);
			evt.getSource().setReportMode(ReportModeRequest.DATA_REPORT_0x32);
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
			paddle.increaseRotation(0, (float)lastEvent.getYawLeftSpeed() * (-1/90f) * dt, 0);
		}		
		lastEvent = evt;
	}

}
