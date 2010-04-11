package tests;

import engine.Camera;
import engine.Engine;
import engine.Entity;
import engine.ViewState;
import engine.GraphicContext;
import engine.Renderable;
import engine.View;
import airhockey.MediaLoader;

public class SplitScreenTest extends ViewState {
    Entity test;
    public void init(Engine e, GraphicContext gc) {
        Renderable model = MediaLoader.loadObj("test.obj");
        test = new Entity();
        test.setRenderComponent(model);
        View v1 = new View(gc.getScreenWidth() / 2, gc.getScreenHeight(), 0, 0);
        View v2 = new View(gc.getScreenWidth() / 2, gc.getScreenHeight(), gc.getScreenWidth() / 2, 0);
        v1.setCamera(new Camera(0, 0f, 6f));
        v2.setCamera(new Camera(0, 0f, 6f));
        addView(v1);
        addView(v2);
    }
    @Override
    public void renderView(View view, Engine e, GraphicContext gc) {
        test.render();
    }

    @Override
    public void update(Engine e, GraphicContext gc, float dt) {
        test.increaseRotation(0.005f, 0, 0);
    }
    
    public static void main(String[] args) {
        Engine e = new Engine("Test spill");
        e.addState("Meh", new SplitScreenTest());
        e.init();
        e.loop();
    }
}
