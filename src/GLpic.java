import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by bodyi on 1/23/2017.
 */
public class GLpic {
    private int x,y;
    private Texture texture;
    private String path, tag;
    public GLpic(String path, int x, int y) throws IOException
    {
        this.path = path;
        this.x = x;
        this.y = y;
        String pth = "Assets\\Art\\Tiles\\";
        //System.setErr(new PrintStream("NUL"));
        texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+path));
    }
    public void tag(String tag)
    {
        this.tag = tag;
    }
    public void render()
    {
        Color.white.bind();
        texture.bind();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x , y );
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + texture.getTextureWidth() , y );
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + texture.getTextureWidth() , y + texture.getTextureHeight() );
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x , y + texture.getTextureHeight() );
        GL11.glEnd();
    }
}
