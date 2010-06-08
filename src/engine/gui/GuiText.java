package engine.gui;

import engine.TrueTypeFont;

public class GuiText extends GuiElement {
    
    public static TrueTypeFont defaultFont = null;
    
    protected boolean enabled;
    
    private TrueTypeFont font;
    private String text;
    private int anchor;

    public GuiText(String text, int posx, int posy) {
        this(text, null, posx, posy, TrueTypeFont.ALIGN_LEFT);
    }
    
    public GuiText(String text, int posx, int posy, int anchor) {
        this(text, null, posx, posy, anchor);
    }
    
    public GuiText(String text, TrueTypeFont font, int posx, int posy, int anchor) {
        super(posx, posy);
        this.text = text;
        this.font = font;
        this.anchor = anchor;
        this.enabled = true;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void setFont(TrueTypeFont font) {
        this.font = font;
    }
    
    public TrueTypeFont getFont() {
        if (font != null) return font;
        return defaultFont;
    }
    
    @Override
    public void render() {
        if (enabled) {
            getFont().drawString(posx, posy, text, 1, 1, anchor);
        }
    }
    
    public void setEnabled(boolean b) {
        this.enabled = b;
    }
    
}
