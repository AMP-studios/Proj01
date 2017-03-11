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
    private boolean limit_L = false;
    private boolean limit_R = false;
    private boolean limit_U = false;
    private boolean limit_D = false;
    int nextMovePointIndex = 0;
    private Time collCd = new Time();
    private int ANGLE = 300;
    private Time turnTime = new Time();
    private static ArrayList<String> coll = new ArrayList<>();
    private static GLtile[][][] grid = new GLtile[3][640/32][800/32];
    public GLenemy(int x, int y, double health, double speed, double rate) throws IOException
    {
        turnTime.start();
        collCd.start();
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
        shooting=true;
        patt=new java.util.ArrayList<>();
        curWeapon=new GLweapon("a9:3:!#f1-e");
        setGrid(LibTest.grid);
        //getNextPoint();
    }
    public void setTex()
    {
        texture = getImg(facing);
    }
    public void setGrid(GLtile[][][] a)
    {
        grid = a;
    }
    public int getDir(String spec)
    {
        String[] a = spec.split(":");
        int ox = Integer.parseInt(a[1])+16;
        int oy = Integer.parseInt(a[2])+16;
        int mx = (int)this.x+16;
        int my = (int)this.y+16;
        if(ox > mx && (oy>my-16 && oy<my+16))
        {
            limit_R = true;
            return 0;
        }
        if(ox <= mx && (oy>my-16 && oy<my+16))
        {
            limit_L = true;
            return 0;
        }
        if(oy > my && (ox>mx-16 && ox<mx+16))
        {
            limit_D = true;
            return 0;
        }
        if(oy <= my && (ox>mx-16 && ox<mx+16))
        {
            limit_U = true;
            return 0;
        }
        return -1;
    }
    public double distTo(int x1, int y1, int x2, int y2)
    {
        return Math.sqrt(Math.pow(Math.abs(x1-x2),2)+Math.pow(Math.abs(y1-y2),2));
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

    public boolean chkCol() throws IOException {
        boolean k = false;
        int q = 0;
        int w = 0;
        int si = 28;
        int ct = (32 - si) / 2;

        for (GLtile[] a : grid[1]) {
            for (GLtile l : a) {
                int x = l.x;
                int y = l.y;
                int px = (int) this.x + ct;
                int py = (int) this.y + ct;
                if ((l.tp + "").equals("#") && (x < px + si && x + si > px) && (y < py + si && y + si > py)) {
                    String ip = "true:" + l.x + ":" + l.y;
                    if (coll.contains(ip)) {
                        coll.remove(ip);
                    }
                    //coll.add(ip);
                    //Tools.bp("collided with "+l.tp);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean LOS(GLplayer p)
    {
        double x1 = this.x;
        double y1 = this.y;
        double x2 = p.x;
        double y2 = p.y;
        return false;
    }

    public void act() throws IOException
    {
        if(turnTime.getTime()>2000)
        {
            turn();
            turnTime.clear();
            turnTime.start();
        }
        if(collCd.getTime()>100&&chkCol())
        {
            turn();
            turn();
            collCd.clear();
            collCd.start();
        }
        for(String a : coll)
        {
            getDir(a);
        }

    }

    public void turn()
    {
        ANGLE+=45;
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

    public void updateDS()
    {
        ANGLE=ANGLE%360;
        double angle = Math.toRadians(ANGLE);
        this.moveDx = Math.cos(angle);
        this.moveDy = Math.sin(angle);
    }

    public void updt() throws IOException
    {
        updateDS();
        act();
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
