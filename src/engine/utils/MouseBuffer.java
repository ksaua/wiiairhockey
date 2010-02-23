package engine.utils;

public class MouseBuffer {

    private float maxPrGet;
        
    private float xbuffer;

    private float ybuffer;
    
    public MouseBuffer(float maxPrGet) {
        this.maxPrGet = maxPrGet;
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
    
}
