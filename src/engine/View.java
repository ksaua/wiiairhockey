package engine;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

public class View {
    
    private int width;
    private int height;
    
    private int posx;
    private int posy;
    
    private Camera camera;
    
    private int texId;
    private int fboId;
    
    public View(int width, int height, int posx, int posy) {
        setSize(width, height);
        this.posx = posx;
        this.posy = posy;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        
        texId = generateTexture();
        int rboId = generateRenderBuffer();
        fboId = generateFBO(texId, rboId);
    }
    
    public void preRender() {
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboId );
        GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
        GL11.glViewport( 0, 0, width, height);
        GL11.glClearColor(1, 1, 1, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT |  GL11.GL_DEPTH_BUFFER_BIT );
        
        if (camera != null) {
            camera.transform();
        }
    }
    
    public void postRender() {
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        GL11.glPopAttrib();
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void drawView() {
        GL11.glPushMatrix();
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        
        GL11.glBegin( GL11.GL_QUADS );
        GL11.glTexCoord2d(0, 0); GL11.glVertex2d(posx, posy);
        GL11.glTexCoord2d(1, 0); GL11.glVertex2d(posx + width, posy);
        GL11.glTexCoord2d(1, 1); GL11.glVertex2d(posx + width, posy + height);
        GL11.glTexCoord2d(0, 1); GL11.glVertex2d(posx, posy + height);
        GL11.glEnd();
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
        GL11.glPopMatrix();
    }
    
    
    
    private int generateFBO(int texId, int rboId) {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        EXTFramebufferObject.glGenFramebuffersEXT( buffer ); // generate 
        int fid = buffer.get(0);
        
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fid );
        EXTFramebufferObject.glFramebufferTexture2DEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
                        GL11.GL_TEXTURE_2D, texId, 0);
        
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                EXTFramebufferObject.GL_RENDERBUFFER_EXT, rboId);
        
        int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT ); 

        switch ( framebuffer ) {
            case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
                break;
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                throw new RuntimeException( "FrameBuffer: " + fid
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                throw new RuntimeException( "FrameBuffer: " + fid
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                throw new RuntimeException( "FrameBuffer: " + fid
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                throw new RuntimeException( "FrameBuffer: " + fid
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                throw new RuntimeException( "FrameBuffer: " + fid
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                throw new RuntimeException( "FrameBuffer: " + fid
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception" );
            default:
                throw new RuntimeException( "Unexpected reply from glCheckFramebufferStatusEXT: " + framebuffer );
        }
        return fid;
    }
    
    private int generateTexture() {
        IntBuffer textures = BufferUtils.createIntBuffer(1);        
        
        GL11.glGenTextures(textures);
        
        int tid = textures.get(0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tid);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE); // automatic mipmap
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8,  width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(width * height * 3));

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        
        return tid;
    }
    
    private int generateRenderBuffer() {
        IntBuffer rboId = BufferUtils.createIntBuffer(1);
        EXTFramebufferObject.glGenRenderbuffersEXT(rboId);
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, rboId.get(0));
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL11.GL_DEPTH_COMPONENT,
                                 width, height);
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
        
        return rboId.get(0);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
