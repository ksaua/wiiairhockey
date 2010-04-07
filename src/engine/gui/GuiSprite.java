package engine.gui;

import engine.Sprite;

public class GuiSprite extends GuiElement {

    private Sprite sprite;

    public GuiSprite(Sprite sprite, int posx, int posy) {
        super(posx, posy);
        this.sprite = sprite;
    }
    
    @Override
    public void render() {
        sprite.draw(posx, posy);
    }

}
