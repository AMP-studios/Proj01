import CustomUtils.AudioController;
import CustomUtils.AudioControllerException;
import CustomUtils.Time;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import javax.tools.Tool;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
    private int currentFrame = 0;
    private boolean limit_L = false;
    private boolean limit_R = false;
    private boolean limit_U = false;
    private Time lookTimer = new Time();
    private int visionRange = 300;
    private boolean limit_D = false;
    private int delay = 100;
    private Time frameTimer = new Time();
    int nextMovePointIndex = 0;
    private Time collCd = new Time();
    private int ANGLE = 300;
    private boolean pointsAdded = false;
    private Time turnTime = new Time();
    private int circle = 0;
    private ArrayList<Texture> frames = new ArrayList<>();
    private ArrayList<Texture> framesReg = new ArrayList<>();
    private int turn = 1000;
    private boolean playerInVision = false;
    private ArrayList<String> coll = new ArrayList<>();
    private static GLtile[][][] grid = new GLtile[3][640/32][800/32];
    private GLimage pointer = new GLimage("pointer.png",-100,-100);
    public boolean alive;
    private GLhealthbar hpBar;

    private AudioController ac = new AudioController();
    public GLenemy(int x, int y, double health, double speed, double rate) throws IOException, AudioControllerException {
    	ac.addSound("/src/Assets/Audio/SFX/ptoo.wav","Shot");
        hpBar = new GLhealthbar(x,y,(int)health,50);
        hpBar.setTether(this);
        frameTimer.start();
        turnTime.start();
        collCd.start();
        alive = true;
        this.rate = rate;
        shootTimer=new Time();
        shootTimer.start();
        gridUpdateTimer=new Time();
        gridUpdateTimer.start();
        lookTimer.start();
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
        texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+"default.png"));
        for( String s : names)
        {
            Texture e = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+s));
            dir.add(e);
            a++;
        }
        shooting=true;
        patt=new java.util.ArrayList<>();
        curWeapon=new GLweapon("a1:3:!#f4=e");
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

            for(GLtile[] r : grid[1])
            {
                for(GLtile t : r)
                {
                    if(t!=null&&x>t.x&&y>t.y&&x<t.x+32&&y<t.y+32)
                    {
                        return t;
                    }
                }
            }

        return grid[0][0][0];
    }

    public void die()
    {
        alive =false;
    }

    public void addPoints()
    {
        LibTest.SCORE += this.health*33+LibTest.rRn(1000,3000);
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
        int si = 28;
        int ct = (32 - si) / 2;

        for (GLtile[] a : grid[1]) {
            for (GLtile l : a) {
                if (l != null) {
                    int x = l.x;
                    int y = l.y;
                    int px = (int) this.x + ct;
                    int py = (int) this.y + ct;
                    if ((l.tp + "").equals("#") && (x < px + si && x + si > px) && (y < py + si && y + si > py))
                    {
                        String ip = "true:" + l.x + ":" + l.y;
                        if (coll.contains(ip))
                        {
                            coll.remove(ip);
                        }
                        return true;
                    }
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
        double dist = distTo((int)x1,(int)y1,(int)x2,(int)y2);
        double dx = x2-x1;
        double dy = y2-y1;
        double mx = dx/dist;
        double my = dy/dist;
        if(dist>visionRange)
        {
            return false;
        }
        for(int i = 0; i < (int)dist; i++)
        {
            GLtile cur = tileAt((int)(mx*i+x),(int)(my*i+y));
            if(cur!=null&&cur.tp=='#')
            {
                return false;
            }
        }
        return true;
    }

    public void addImage(String name) throws IOException
    {
        frames.add(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth + name)));
    }

    public void addRegular(String name) throws IOException
    {
        framesReg.add(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth + name)));
    }

    public void setAnimDelay(int d)
    {
        delay = d;
    }

    public void bulletCol()
    {
        GLplayer me = LibTest.PLAYER;
        for(int i = 0; i < me.curWeapon.bullets.size(); i++)
        {
            GLbullet cur = me.curWeapon.bullets.get(i);
            if(cur.x+16 > this.x && cur.x+16 < this.x + 32)
            {
                if(cur.y+16 > this.y && cur.y+16 < this.y+32 && cur.active)
                {
                    cur.active = false;
                    health-=cur.damage;
                    hpBar.subtract((int)cur.damage);
                }
            }
        }
    }

    public void run()
    {
        this.x-=speed*5;
        if(this.x < -40)
        {
            die();
        }
    }

    protected void act() throws IOException, AudioControllerException {

        if(health<=0)
        {
            run();
            if(!pointsAdded)
            {
                if(framesReg.size()>0)
                {
                    frames=framesReg;
                }
                addPoints();
                pointsAdded=true;
            }

        }
        else
        {
            if(lookTimer.getTime()>10)
            {
                playerInVision = LOS(LibTest.PLAYER);
                lookTimer.clear();
                lookTimer.start();
                //Tools.bp(playerInVision);
            }
            if(circle>=8)
            {
                turn = LibTest.rRn(100,4000);
                circle = 0;
            }
            if(turnTime.getTime()>turn)
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
        //mark010


    }

    public void turn()
    {
        ANGLE+=45;
        circle++;
    }

    public String playerIs(GLplayer me)
    {
        try {
            double dx = me.x-x;
            double dy = me.y-y;
            double dist = distTo((int)x,(int)y,(int)me.x,(int)me.y);
            int quadrant = 1;
            if(!isp(dx)&&isp(dy))
            {
                quadrant = 2;
            }
            if(!isp(dx)&&!isp(dy))
            {
                quadrant = 3;
            }
            if(isp(dx)&&!isp(dy))
            {
                quadrant = 4;
            }
            double sin = dy/dist;
            double ang = Math.toDegrees(Math.asin(sin));
            ang =Math.abs(ang);
            if(quadrant==3)
            {
                ang = 180-ang;
            }
            else if(quadrant==2)
            {
                ang = 180+ang;
            }
            else if(quadrant==1)
            {
                ang = 360-ang;
            }

            //Tools.bp(ang);

            if(ang<45&&ang>0||ang>315&&ang<360)
            {
                return "right";
            }
            else if(ang<135&& ang >45)
            {
                return "up";
            }
            else if(ang< 225&& ang >135)
            {
                return "left";
            }
            else if(ang< 315&& ang >225)
            {
                return "down";
            }
        }
        catch (Exception e)
        {
            Tools.bp(e);
        }
        return "up";
    }

    public boolean isp(double a)
    {
        if(a < 0)
        {
            return false;
        }
        return true;
    }

    public void render() throws IOException, AudioControllerException {
        //setTex();
        //Tools.bp(shooting);
        //shooting = playerInVision;

            if (!pointsAdded) {
                hpBar.render();
            }
            if (frameTimer.getTime() > delay) {
                currentFrame++;
                currentFrame %= frames.size();
                frameTimer.clear();
                frameTimer.start();
            }
            texture = frames.get(currentFrame);
            Color.white.bind();
            texture.bind();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0);
            GL11.glVertex2f((int) x, (int) y);
            GL11.glTexCoord2f(1, 0);
            GL11.glVertex2f((int) x + texture.getTextureWidth(), (int) y);
            GL11.glTexCoord2f(1, 1);
            GL11.glVertex2f((int) x + texture.getTextureWidth(), (int) y + texture.getTextureHeight());
            GL11.glTexCoord2f(0, 1);
            GL11.glVertex2f((int) x, (int) y + texture.getTextureHeight());
            GL11.glEnd();
            updt();
            //pointer.render();
            curWeapon.fcg = facing;
            curWeapon.x = (int) this.x;
            curWeapon.y = (int) this.y;
            curWeapon.render();

    }

    public void setShootStyle(String style)
    {
        curWeapon=new GLweapon(style);
    }

    public void updateDS()
    {
        ANGLE=ANGLE%360;
        double angle = Math.toRadians(ANGLE);
        this.moveDx = Math.cos(angle);
        this.moveDy = Math.sin(angle);
    }

    public void updt() throws IOException, AudioControllerException {
        updateDS();
        act();
        bulletCol();
        if(gridUpdateTimer.getTime() > 1000)
        {
            setGrid(LibTest.grid);
            gridUpdateTimer.clear();
            gridUpdateTimer.start();
        }
        if(shooting && shootTimer.getTime()>rate)
        {
            if(playerInVision) {
                facing = playerIs(LibTest.PLAYER);
                shoot();
                curWeapon.render();
                shootTimer.clear();
                shootTimer.start();
            }
        }
        this.x+=moveDx*speed;
        this.y+=moveDy*speed;
    }


    public void shoot() throws AudioControllerException, IOException  {
    	ac.playSoundEffect("Shot", 0.2);
        curWeapon.step = "create";
        curWeapon.render();
    }
}
