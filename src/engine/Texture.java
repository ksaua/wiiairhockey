package engine;

import org.lwjgl.opengl.GL11;

/**
 * Class which stores the texture's OpenGL id, and allows it
 * to bound to the opengl state.
 *  
 * @author Knut Saua Mathiesen
 *
 */
public class Texture {

    private int textureId;
    private int imageHeight;
    private int imageWidth;
    private float widthRatio;
    private float heightRatio;

    Texture(int textureId, int imageHeight, int imageWidth,
            float widthRatio, float hightRatio) {
        this.textureId = textureId;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.widthRatio = widthRatio;
        this.heightRatio = hightRatio;
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }

    public float widthRatio() {
        return widthRatio;
    }

    public float heightRatio() {
        return heightRatio;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }
}