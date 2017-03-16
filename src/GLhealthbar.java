import java.io.IOException;
import java.util.ArrayList;

public class GLhealthbar {
    public int x;
    public int y;
    public int maxHp;
    public int size;
    public int curHp;
    private double ratio = 1;
    private ArrayList<GLimage> bars = new ArrayList<>();
    private ArrayList<GLimage> back = new ArrayList<>();
    private int notRen;
    private GLenemy tether = null;

    public GLhealthbar(int x, int y, int health, int size) throws IOException
    {
        notRen = size;
        this.x = x;
        this.y = y;
        this.maxHp = health;
        this.curHp = maxHp;
        this.size = size;
        ratio = size/maxHp;
        for(int i = 0; i < size; i++)
        {
            back.add(new GLimage("grey_bar.png",x+i,y));
        }
        for(int i = 0; i < size; i++)
        {
            bars.add(new GLimage("red_bar.png",x+i,y));
        }
    }

    public GLhealthbar(int x, int y, int health, int size, String grey, String red) throws IOException
    {
        notRen = size;
        this.x = x;
        this.y = y;
        this.maxHp = health;
        this.curHp = maxHp;
        this.size = size;
        ratio = size/maxHp;
        for(int i = 0; i < size; i++)
        {
            back.add(new GLimage(grey,x+i,y));
        }
        for(int i = 0; i < size; i++)
        {
            bars.add(new GLimage(red,x+i,y));
        }
    }


    public void setTether(GLenemy t)
    {
        tether = t;
    }

    public void subtract(int amt)
    {
        curHp-=amt;
        if(ratio<1&&curHp%(maxHp/size)==0)
        {
            notRen--;
        }else
        {
            notRen-=(int)ratio;
        }
    }

    public void render()
    {
        if(tether!=null)
        {
            this.x = (int)tether.x-(size/2)+16;
            this.y = (int)tether.y-8;
        }
        int a = 0;
        for(GLimage i : back)
        {
            i.x = this.x+a;
            i.y = this.y;
            i.render();
            a++;
        }
        int b=0;
        for(GLimage i : bars)
        {
            i.x = this.x+b;
            i.y = this.y;
            if (b<notRen)
            {
                i.render();
            }
            b++;
        }
    }

}
