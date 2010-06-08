package engine.utils;

import airhockey.Paddle;
import airhockey.Updateable;

public class MouseBuffer implements Updateable {

    private float maxPrGet;
        
    private float xbuffer;

    private float ybuffer;

    private Paddle paddle;
    
    public MouseBuffer(Paddle paddle, float maxPrGet) {
        this.maxPrGet = maxPrGet;
        this.paddle = paddle;
    }
    
    public void pushMove(int dx, int dy) {
        xbuffer += dx;
        ybuffer += dy;
    }
    
    public float getX() {
        if (Math.abs(xbuffer) > maxPrGet) {
            float x = maxPrGet * (xbuffer > 0 ? 1 : -1);
            xbuffer -= x;
            return x;   
        } else {
            float t = xbuffer;
            xbuffer = 0;
            return t;
        }
    }
    
    public float getY() {
        if (Math.abs(ybuffer) > maxPrGet) {
            float y = maxPrGet * (ybuffer > 0 ? 1 : -1);
            ybuffer -= y;
            return y;   
        } else {
            float y = ybuffer;
            ybuffer = 0;
            return y;
        }
    }

    @Override
    public void update(float dt) {
        if (paddle != null)
            paddle.move(getY() * 0.01f, 0, getX() * 0.01f);        
    }
    
    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }
    
}
