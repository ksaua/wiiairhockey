package engine;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class TextureLoader {
    private static HashMap<String, Texture> cache = new HashMap<String, Texture>();

    /**
     * Load a texture in the data/textures folder.
     * @param name Filename
     * @param mipmap Is this texture only supposed to be rendered as a 2D sprite? Then it shouldn't be mipmapped
     * @return OpenGL bindable texture
     */
    public static Texture loadTexture(String name, boolean mipmap) {
        return loadTexture(name, true, mipmap);
    }

    /**
     * Load a texture in the data/textures folder.
     * @param name Filename
     * @param flip Flip the texture upside down?
     * @param mipmap Is this texture only supposed to be rendered as a 2D sprite? Then it shouldn't be mipmapped
     * @return OpenGL bindable texture
     */
    public static Texture loadTexture(String name, boolean flip, boolean mipmap) {

        if (!cache.containsKey(name)) {
            Texture t = loadTexture(new File("data/textures/" + name), flip, mipmap);
            cache.put(name, t);
        }

        return cache.get(name);
    }

    private static Texture loadTexture(File file, boolean flip, boolean mipmap) {
        try {
            return loadTexture(ImageIO.read(file), flip, mipmap);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Texture loadTexture(BufferedImage img, boolean flip, boolean mipmap) {
        img = convertToABGR(img);

        byte[] source = ((DataBufferByte)img.getAlphaRaster().getDataBuffer()).getData();

        // Number of bytes / pixel
        int pixbytes = 4;

        // OpenGL stores the bytes in reverse order
        byte[] temp = new byte[4];
        for (int i = 0; i < source.length; i += pixbytes) {
            temp[0] = source[i + 0];
            temp[1] = source[i + 1];
            temp[2] = source[i + 2];
            temp[3] = source[i + 3];

            source[i + 0] = temp[3];
            source[i + 1] = temp[2];
            source[i + 2] = temp[1];
            source[i + 3] = temp[0];
        }

        // Find the resolution it needs to use in opengl.
        int glwidth = nextPow2(img.getWidth());
        int glheight = nextPow2(img.getHeight());

        // Transfer bytes from real pixel position to opengl pixel position.
        ByteBuffer pixels = (ByteBuffer)BufferUtils.createByteBuffer(glwidth * glheight * 4);

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                for (int i = 0; i < pixbytes; i++ ) {

                    // Do we flip it upside down?
                    int realy = flip ? img.getHeight() - 1 - y : y;

                    pixels.put(pixbytes * (x + realy * glwidth) + i,
                            source[pixbytes * (x + y * img.getWidth()) + i]);
                }
            }
        }


        IntBuffer textures = BufferUtils.createIntBuffer(1);
        GL11.glGenTextures(textures);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.get(0));
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, glwidth, glheight,
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
                
        if (!mipmap) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        } else { // Mip mapping
            GLU.gluBuild2DMipmaps( GL11.GL_TEXTURE_2D, 3, glwidth, glheight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
        }
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        
        // Anisotropic filtering
        FloatBuffer maximumAnistropy = FloatBuffer.allocate(16);
        GL11.glGetFloat(0x84FF, maximumAnistropy);        
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 0x84FE, maximumAnistropy.get(0));
        


        float wratio = img.getWidth() / (float)glwidth;
        float hratio = img.getHeight() / (float)glheight;
        return new Texture(textures.get(0), img.getHeight(), img.getWidth(), wratio, hratio);
    }

    /**
     * Given a number x, finds the next power of 2.
     * @param x
     * @return
     */
    private static int nextPow2(int x) {
        int i = 2;
        while (x > i) i *= 2;
        return i;
    }

    private static BufferedImage convertToABGR(BufferedImage bi) {
        int w = bi.getWidth();
        int h = bi.getHeight();
        BufferedImage newimg = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        int[] rgbarray = new int[w * h];
        bi.getRGB(0, 0, w, h, rgbarray, 0, w);
        newimg.setRGB(0, 0, w, h, rgbarray, 0, w);
        return newimg;
    }
}
