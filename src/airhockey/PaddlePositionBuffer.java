package airhockey;

public class PaddlePositionBuffer implements Updateable {
    
    // The paddle which's position is buffered
    private Paddle paddle;
    
    // Needs to be moved when updated
    private float trans_x;
    private float trans_z;
    private float trans_yaw;
    
    private float max_x;
    private float max_z;
    private float max_yaw;
    
    /**
     * @param max_x Maximum x movement per second
     * @param max_z Maximum z movement per second
     * @param max_yaw Maximum yaw rotation per second 
     */
    public PaddlePositionBuffer(Paddle paddle, float max_x, float max_z, float max_yaw) {
        this.paddle = paddle;
        this.max_x = max_x;
        this.max_z = max_z;
        this.max_yaw = max_yaw;
    }
    
    /**
     * Set the position where the paddle should be
     * @param x
     */
    public void goToX(float x) {
        trans_x = x - paddle.getPos().x;
    }
    
    /**
     * Set the position where the paddle should be
     * @param z
     */
    public void goToZ(float z) {
        trans_z = z - paddle.getPos().z;
    }
    
    /**
     * Set the yaw it should be
     * @param yaw
     */
    public void goToYaw(float yaw) {
        trans_yaw = yaw - paddle.getRot().y;
    }
    
    @Override
    public synchronized void update(float dt) {
        if (paddle != null) {
            if (trans_x != 0) {
                float dx = max_x * dt * (trans_x > 0 ? 1 : -1);
                if (Math.abs(trans_x) > Math.abs(dx)) {
                    trans_x -= dx;
                    paddle.setX(paddle.getPos().x + dx);
                } else {
                    paddle.setX(paddle.getPos().x + trans_x);
                    trans_x = 0;
                }
            }
            
            if (trans_z != 0) {
                float dz = max_z * dt * (trans_z > 0 ? 1 : -1);
                if (Math.abs(trans_z) > Math.abs(dz)) {
                    trans_z -= dz;
                    paddle.setZ(paddle.getPos().z + dz);
                } else {
                    paddle.setZ(paddle.getPos().z + trans_z);
                    trans_z = 0;
                }
            }
            
            if (trans_yaw != 0) {
                float dyaw = max_yaw * dt * (trans_yaw > 0 ? 1 : -1);
                if (Math.abs(trans_yaw) > Math.abs(dyaw)) {
                    trans_yaw -= dyaw;
                    paddle.setYaw(paddle.getRot().y + dyaw);
                } else {
                    paddle.setYaw(paddle.getRot().y + trans_yaw);
                    trans_yaw = 0;
                }
            }
        }
    }

    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }
}
 