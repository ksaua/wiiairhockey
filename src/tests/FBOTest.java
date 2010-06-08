package tests;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import engine.EmptyState;
import engine.Engine;
import engine.GraphicContext;
import engine.Sprite;
import engine.Texture;
import engine.TextureLoader;

public class FBOTest extends EmptyState {
    Sprite meh;
    
    int texId; 
    int fobId;
    
    @Override
    public void init(Engine e, GraphicContext gc) {
        Texture eh = TextureLoader.loadTexture("chesstex.jpg", false);
        meh = new Sprite(eh);
        

        
        IntBuffer textures = BufferUtils.createIntBuffer(1);        
        
        GL11.glGenTextures(textures);
        
        texId = textures.get(0);
//        eh.textureId = texId;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE); // automatic mipmap
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8,  256, 256, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(256 * 256 * 3));

        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        
        
        
        
        IntBuffer rboId = BufferUtils.createIntBuffer(1);
        EXTFramebufferObject.glGenRenderbuffersEXT(rboId);
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, rboId.get(0));
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL11.GL_DEPTH_COMPONENT,
                                 256, 256);
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
        
        
//        boolean FBOEnabled = GLContext.getCapabilities().GL_EXT_framebuffer_object;
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        EXTFramebufferObject.glGenFramebuffersEXT( buffer ); // generate 
        fobId = buffer.get(0);
        
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fobId );
        EXTFramebufferObject.glFramebufferTexture2DEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
                        GL11.GL_TEXTURE_2D, texId, 0);
        
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                EXTFramebufferObject.GL_RENDERBUFFER_EXT, rboId.get(0));
        
        int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT ); 

        switch ( framebuffer ) {
            case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
                break;
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                throw new RuntimeException( "FrameBuffer: " + fobId
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                throw new RuntimeException( "FrameBuffer: " + fobId
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                throw new RuntimeException( "FrameBuffer: " + fobId
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                throw new RuntimeException( "FrameBuffer: " + fobId
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                throw new RuntimeException( "FrameBuffer: " + fobId
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception" );
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                throw new RuntimeException( "FrameBuffer: " + fobId
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception" );
            default:
                throw new RuntimeException( "Unexpected reply from glCheckFramebufferStatusEXT: " + framebuffer );
        }
        


        
    }
    
    @Override
    public void render(Engine e, GraphicContext gc) {
        
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fobId );
        GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
        GL11.glViewport( 0, 0, 256, 256);
        GL11.glClearColor(1, 1, 1, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT |  GL11.GL_DEPTH_BUFFER_BIT );
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glTranslatef(0, 0, -6f);
        GL11.glColor3f(1, 1, 0);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(2, 0, 0);
        GL11.glVertex3f(2, 2, 0);
        GL11.glVertex3f(0, 2, 0);
        GL11.glEnd();
        
        EXTFramebufferObject.glBindFramebufferEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        GL11.glPopAttrib();
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL11.glTranslatef(0, 0, -10);
        GL11.glRotatef(0, 1, 0, 0);   // pitch
        GL11.glRotatef(0, 0, 1, 0);   // heading
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(1, 1, 1, 1);

        // face v0-v1-v2-v3
        GL11.glNormal3f(0,0,1);
        GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(1,1,1);
        GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(-1,1,1);
        GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(-1,-1,1);
        GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(1,-1,1);

        // face v0-v3-v4-v5
        GL11.glNormal3f(1,0,0);
        GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(1,1,1);
        GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(1,-1,1);
        GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(1,-1,-1);
        GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(1,1,-1);

        // face v0-v5-v6-v1
        GL11.glNormal3f(0,1,0);
        GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(1,1,1);
        GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(1,1,-1);
        GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(-1,1,-1);
        GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(-1,1,1);

        // face  v1-v6-v7-v2
        GL11.glNormal3f(-1,0,0);
        GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(-1,1,1);
        GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(-1,1,-1);
        GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(-1,-1,-1);
        GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(-1,-1,1);

        // face v7-v4-v3-v2
        GL11.glNormal3f(0,-1,0);
        GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(-1,-1,-1);
        GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(1,-1,-1);
        GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(1,-1,1);
        GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(-1,-1,1);

        // face v4-v7-v6-v5
        GL11.glNormal3f(0,0,-1);
        GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(1,-1,-1);
        GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(-1,-1,-1);
        GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(-1,1,-1);
        GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(1,1,-1);
        GL11.glEnd();
        
        gc.start2dDrawing();
        meh.draw(0, 0);
        
    }
    
    public static void main(String[] args) {
        Engine e = new Engine("Test spill");
        e.addState("Meh", new FBOTest());
        e.init();
        e.loop();
    }
}
