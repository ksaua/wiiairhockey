package engine;

import org.lwjgl.opengl.GL11;

public class Sprite {
	
	Texture tex;

	public Sprite(Texture t) {
		this.tex = t;
	}

	public void draw(int x, int y) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		tex.bind();
		
		float h = tex.getImageHeight() / 2;
		float w = tex.getImageWidth() / 2;
		
	    GL11.glBegin( GL11.GL_QUADS );
	    GL11.glTexCoord2d(0.0,0.0);								GL11.glVertex2d(-w,-h);
	    GL11.glTexCoord2d(tex.widthRatio(),0.0); 				GL11.glVertex2d( w,-h);
	    GL11.glTexCoord2d(tex.widthRatio(),tex.heightRatio()); 	GL11.glVertex2d( w, h);
	    GL11.glTexCoord2d(0.0,tex.heightRatio());				GL11.glVertex2d(-w, h);
	    GL11.glEnd();
	    
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    
	    GL11.glPopMatrix();
	    
	}

}
