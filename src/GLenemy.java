import CustomUtils.Time;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bodyi on 2/9/2017.
 */
public class GLenemy {
    boolean paused = false;
    double rate;
    private Time shootTimer;
    private Time walkTimer;
    boolean shooting;
    double x;
    double y;
    String pth;
    Texture texture;
    double health;
    double speed;
    ArrayList<Texture> dir;
    String facing = "down";
    ArrayList<String> names;
    ArrayList<GLweapon> patt;
    String[] points;
    int radius;
    GLweapon curWeapon;
    public GLenemy(int x, int y, double health, double speed, double rate) throws IOException
    {
        this.rate = rate;
        shootTimer=new Time();
        shootTimer.start();
        this.x = x;
        this.health = health;
        this.speed = speed;
        this.y = y;
        String current = new java.io.File( "." ).getCanonicalPath();
        File folder = new File(current+"/src/Assets/Art/Tiles");
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles!=null;
        names=new java.util.ArrayList<>();
        for(java.io.File listOfFile : listOfFiles) {
            if(listOfFile.isFile()&&listOfFile.getName().startsWith("P-")) {
                names.add(listOfFile.getName());
            }
        }
        int a = 0;
        pth="Assets\\Art\\Tiles\\";
        dir=new java.util.ArrayList<>();
        for( String s : names)
        {
            Texture e = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+s));
            dir.add(e);
            a++;
        }
        shooting=false;
        patt=new java.util.ArrayList<>();
        curWeapon=new GLweapon("a9:3:)#f1-f2-b1-f2.b3-f10--..e");
    }
    public void setTex()
    {
        texture = getImg(facing);
    }

    public void selPoints(int radius,int num)
    {
        int complete = 0;
        while(complete < points.length)
        {
            
        }
    }

    public Texture getImg(String fce)
    {
        fce = "P-"+fce+"1.png";
        int idx =0;
        for(String s : names)
        {
            if(s.equals(fce))
            {
                return dir.get(idx);
            }
            idx++;
        }
        return dir.get(idx);
    }

    public void render() throws IOException
    {
        setTex();
        Color.white.bind();
        texture.bind();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0,0);
        GL11.glVertex2f((int)x,(int)y);
        GL11.glTexCoord2f(1,0);
        GL11.glVertex2f((int)x+texture.getTextureWidth(),(int)y) ;
        GL11.glTexCoord2f(1,1);
        GL11.glVertex2f((int)x+texture.getTextureWidth(),(int)y+texture.getTextureHeight());
        GL11.glTexCoord2f(0,1);
        GL11.glVertex2f((int)x,(int)y+texture.getTextureHeight());
        GL11.glEnd();
        updt();
        curWeapon.fcg = facing;
        curWeapon.x = (int)this.x;
        curWeapon.y = (int)this.y;
        curWeapon.render();
    }

    public void updt()
    {
        if(shooting && shootTimer.getTime()>rate)
        {
            shoot();
            shootTimer.clear();
            shootTimer.start();
        }
    }

    public void shoot()
    {
        curWeapon.step = "create";
    }
}
