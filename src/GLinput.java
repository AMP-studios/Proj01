import CustomUtils.Time;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import CustomUtils.AudioControllerException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.Key;
import java.security.KeyException;
import java.util.ArrayList;

import static java.lang.System.out;

/**
 * Created by bodyi on 2/4/2017.
 */
public class GLinput {
    boolean hover;
    boolean click;
    private static Time timeout=new Time();
    private CustomUtils.AudioController a;
    int mouseX;
    int mouseY;

    ArrayList<GLtile> top = new ArrayList<>();
    ArrayList<GLtile> bot = new ArrayList<>();

    int x;
    int y;

    private boolean shift = false;

    public String ret = null;

    public static boolean sel = false;

    private static Time udE=new Time();

    private boolean holding = false;

    Texture texture;

    ArrayList<String> in = new ArrayList<>();

    ArrayList<GLimage> letters = new ArrayList<>();

    boolean complete = false;

    int width;
    int height;

    int minSize = 3;

    String tag;
    public void createBox() throws IOException
    {
        int i = 0;
        top.add(new GLtile("ip-b1.png",i*32+x,y,'`','!'));
        bot.add(new GLtile("ip-b4.png",i*32+x,y+32,'`','!'));
        i++;
        for(int a = 0;a < minSize; a++)
        {
            top.add(new GLtile("ip-b6.png",i*32+x,y,'`','!'));
            bot.add(new GLtile("ip-b8.png",i*32+x,y+32,'`','!'));
            i++;
        }
        top.add(new GLtile("ip-b2.png",i*32+x,y,'`','!'));
        bot.add(new GLtile("ip-b3.png",i*32+x,y+32,'`','!'));
        Tools.p(top.size());
    }

    public GLinput(String normal, int Px, int Py, String tag) throws IOException {

        this.x = Px;
        this.y = Py;
        udE.start();
        this.tag = tag;
        out.println("loading button ...");
        String pth = "Assets\\Art\\Tiles\\";
        //texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+normal));
        createBox();
        //out.println("ld >> "+normal);
        width = top.size()*32;
        height = 64;
    }

    public void update(int mX, int mY, boolean c, double dt)  throws IOException{
        mouseX = mX;
        width = top.size()*32;
        //Tools.p(height+" : "+top.size());
        mouseY = Display.getHeight() - mY;

        if (mouseInBox() && c && !holding) {
            this.click = true;
            if(udE.getTime()>500)
            {
                //Tools.p("selected");
                sel = true;
                udE.clear();
                udE.start();
            }
            render();

        }
        else if(!mouseInBox() && c && !holding)
        {
            this.click = false;
            if(udE.getTime()>500)
            {
                //Tools.p("deselected");
                sel = true;
                udE.clear();
                udE.start();
            }
            render();
        }
        else if (mouseInBox() && !holding) {
            this.hover = true;
            this.click = false;
            render();

        } else if (!holding) {
            this.click = false;
            this.hover = false;
            render();
        }
    }
    public boolean mouseInBox() throws IOException
    {
        return (mouseX>=this.x&&mouseX<=this.x+width)&&(mouseY>=this.y&&mouseY<=this.y+height);
    }
    public void render() throws IOException {
        if (sel) {
            ipt();
        }
        if(!complete) {
            for (GLtile a : top) {
                a.render();
            }
            for (GLtile b : bot) {
                b.render();
            }

            for (GLimage a : letters) {
                a.render();
            }
        }
    }

    public String getEntry()
    {
        return ret;
    }

    public void ipt() throws IOException
    {
        //Tools.p(complete);
        if(complete)
        {
            ret = "";
            for(String s : in)
            {
                ret+=s;
            }
        }
        /*if(in.size()*7>top.size()*32-7)
        {
            top.remove(top.size()-1);
            bot.remove(bot.size()-1);
            top.add(new GLtile("ip-b6.png",(top.size()-1)*32+x,y,'`','!'));
            bot.add(new GLtile("ip-b8.png",(bot.size()-1)*32+x,y+32,'`','!'));
            top.add(new GLtile("ip-b2.png",(top.size()-1)*32+x,y,'`','!'));
            bot.add(new GLtile("ip-b3.png",(bot.size()-1)*32+x,y+32,'`','!'));
        }*/

        if(Keyboard.isKeyDown(Keyboard.KEY_RETURN) && sel)
        {
            complete = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)||Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
        {
            shift = true;
        }else
        {
            shift = false;
        }
        while(Keyboard.next() && sel && !complete)
        {
            if(Keyboard.getEventKeyState())
            {

                int prs = Keyboard.getEventKey();
                if(prs!=Keyboard.KEY_RETURN)
                {

                    String uu = Keyboard.getKeyName(prs);
                    if(!shift)
                    {
                        uu = uu.toLowerCase();
                    }else
                    {
                        uu = uu.toUpperCase();

                    }
                    if(prs==Keyboard.KEY_BACK)
                    {
                        if(in.size() > 0)
                        {
                            in.remove(in.size()-1);
                            letters.remove(letters.size()-1);
                        }
                    }else if(prs==Keyboard.KEY_SPACE)
                    {
                        in.add(" ");
                        letters.add(new GLimage(Tools.toLet(" "),in.size()*7+x,y+32-3,true));
                    }
                    else if(prs==Keyboard.KEY_PERIOD)
                    {
                        in.add(".");
                        letters.add(new GLimage(Tools.toLet("."),in.size()*7+x,y+32-3,true));
                    }
                    else if(prs!=Keyboard.KEY_RSHIFT && prs!=Keyboard.KEY_LSHIFT)
                    {
                        in.add(uu);
                        letters.add(new GLimage(Tools.toLet(uu),in.size()*7+x,y+32-3,true));
                    }
                }
            }
        }
    }
}
