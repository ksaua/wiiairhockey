package airhockey;

import engine.Entity;

public class AIPaddleController implements Controller {

    private Entity paddle;
    private Entity puck;
    private float speed;

    public AIPaddleController(Entity paddle, Entity puck, float xmin, float xmax, float ymin, float ymax, float speed) {
        this.paddle = paddle;
        this.puck = puck;
        this.speed = speed;
    }

    @Override
    public void update(float dt) {
        if (paddle.getPos().z > puck.getPos().z) {
            paddle.move(0, 0,-speed * dt);
        } else {
            paddle.move(0, 0, speed * dt);
        }
    }

}
