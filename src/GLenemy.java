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
    private Time gridUpdateTimer;
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
    ArrayList<String> points = new ArrayList<>();
    int radius;
    GLweapon curWeapon;
    String movement = "close";
    double moveDx = 0;
    double moveDy = 0;
    int nextMovePointIndex = 0;
    private static GLtile[][][] grid = new GLtile[3][640/32][800/32];
    public GLenemy(int x, int y, double health, double speed, double rate) throws IOException
    {

        this.rate = rate;
        shootTimer=new Time();
        shootTimer.start();
        gridUpdateTimer=new Time();
        gridUpdateTimer.start();
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
        curWeapon=new GLweapon("a9:3:!#f1-e");
        setGrid(LibTest.grid);
        selPoints(100,10);
        getNextPoint();
    }
    public void setTex()
    {
        texture = getImg(facing);
    }
    public void setGrid(GLtile[][][] a)
    {
        grid = a;
    }


    public void selPoints(int radius,int num)
    {
        ArrayList<String> options =  new ArrayList<>();
        int complete = 0;
        double smallR = Double.MAX_VALUE;
        for(int i = 0; i < 359; i += 10)
        {
            double a = distToWall(i,radius,(int)this.x+16,(int)this.y+16);
            //Tools.bp("-> "+a);
            if(a < smallR)
            {
                smallR = a;
            }
        }
        while(complete < num)
        {
            int x = Tools.random((int)(this.x+16+smallR),(int)(this.x+16-smallR));
            int y = Tools.random((int)(this.y+16+smallR),(int)(this.y+16-smallR));
            if(distTo((int)(this.x+16+smallR),(int)(this.y+16+smallR),x,y)<smallR)
            {
                options.add(x+","+y);
                complete++;
            }
        }
        switch (movement)
        {
            case "close":
                for(int i = 0; i < options.size(); i++)
                {
                    int min = Integer.MAX_VALUE;
                    String add = "";
                    for(int a = i; a < options.size(); a++)
                    {
                        String[] c = options.get(i).split(",");
                        int b1 = Integer.parseInt(c[0]);
                        int b2 = Integer.parseInt(c[1]);
                        if(a!=i)
                        {

                            String[] b = options.get(a).split(",");
                            int a1 = Integer.parseInt(b[0]);
                            int a2 = Integer.parseInt(b[1]);
                            if(distTo(b1,b2,a1,a2)<min)
                            {
                                min = (int)distTo(b1,b2,a1,a2);
                                add = a1+","+a2;
                            }
                        }
                    }
                    points.add(add);
                }
        }
    }

    public void setPath(String type)
    {
        movement = type;
    }

    public double distTo(int x1, int y1, int x2, int y2)
    {
        return Math.sqrt(Math.pow(Math.abs(x1-x2),2)+Math.pow(Math.abs(y1-y2),2));
    }

    public static double distToWall(double angle, int radius,int x, int y)
    {
        angle = Math.toRadians(angle);
        double dx = Math.cos(angle);
        double dy = Math.sin(angle);
        dx = round(dx);
        dy = round(dy);
        for(int i = 0; i < radius;i++)
        {
            GLtile cur = tileAt(x,y);
            if(cur!=null&&cur.tp=='#')
            {
                return i;
            }
            x+=dx;
            y+=dy;
        }
        return radius;
    }

    public static GLtile tileAt(int x, int y)
    {
        for(GLtile[][] gr : grid)
        {
            for(GLtile[] r : gr)
            {
                for(GLtile t : r)
                {
                    if(x>t.x&&y>t.y&&x<t.x+32&&y<t.y+320)
                    {
                        return t;
                    }
                }
            }
        }
        return grid[0][0][0];
    }
    public static double round(double ipt)
    {
        if(ipt<0.00001)
        {
            return 0.0;
        }
        else
        {
            return ipt;
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
        String cur = points.get(nextMovePointIndex);
        String[] b = cur.split(",");
        int a1 = Integer.parseInt(b[0]);
        int a2 = Integer.parseInt(b[1]);
        //Tools.bp(distTo(a1,a2,(int)this.x+16,(int)this.y+16));
        if(distTo(a1,a2,(int)this.x+16,(int)this.y+16)<30)
        {
            Tools.bp("===reached point===");
            getNextPoint();
        }
        if(gridUpdateTimer.getTime() > 1000)
        {
            setGrid(LibTest.grid);
            gridUpdateTimer.clear();
            gridUpdateTimer.start();
        }
        if(shooting && shootTimer.getTime()>rate)
        {
            shoot();
            shootTimer.clear();
            shootTimer.start();
        }
        this.x+=moveDx;
        this.y+=moveDy;
    }

    public void getNextPoint()
    {
        String cur = points.get(nextMovePointIndex);
        nextMovePointIndex++;
        String next = points.get(nextMovePointIndex);
        nextMovePointIndex = nextMovePointIndex%points.size();
        String[] b = cur.split(",");
        int a1 = Integer.parseInt(b[0]);
        int a2 = Integer.parseInt(b[1]);
        String[] c = next.split(",");
        int b1 = Integer.parseInt(c[0]);
        int b2 = Integer.parseInt(c[1]);
        double hyp = distTo(a1,a2,b1,b2);
        double adj = Math.abs(a1-b1);
        double opp = Math.abs(a2-b2);
        Tools.bp(opp+", "+adj+", "+hyp);
        moveDx = adj/hyp;
        moveDy = opp/hyp;
        Tools.bp(moveDx+", "+moveDy);
    }

    public void shoot()
    {
        curWeapon.step = "create";
    }
}
