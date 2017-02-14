import CustomUtils.Time;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.out;


public class GLbullet{
	int x;
	private int enl = 0;
	int y;
	double dX;
	double dY;
	double damage;
	int rad = 10;
	boolean moving = false;
	boolean active = true;
	int wait = 0;
	private String pth = "Assets\\Art\\Tiles\\";
	ArrayList<Texture> frames = new ArrayList<>();
	Texture texture;
	private Time delay=new Time();
	char cur = 0;
	int pp =0;
	char[] exe;
	public GLbullet(int x, int y, String exe) throws IOException
	{
		this.x = x;
		this.y = y;
		texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+"b-1.png"));
		this.exe = exe.toCharArray();
		cur = this.exe[pp];
		pp++;

	}
	public void vel(double dx, double dy)
	{
		dX = dx;
		dY = dy;
		moving = true;
	}
	public void stop()
	{
		dX = 0;
		dY = 0;
		moving = false;
	}
	public void update() throws IOException
	{

		this.x += this.dX;
		this.y += this.dY;
		if(wait < 1 && active)
		{

			if((""+cur).equals("f")||(""+cur).equals("b")||(""+cur).equals("l")||(""+cur).equals("r"))
			{
				try
				{
					char save =cur;
					cur = this.exe[pp];
					pp++;
					char no1 = cur;
					cur = this.exe[pp];
					pp++;
					char no2 = cur;
					int amt = 0;
					if(isInt(""+no2))
					{
						amt += Integer.parseInt(""+no2) + 10*Integer.parseInt(""+no1);
					}
					else
					{
						amt += Integer.parseInt(""+no1);
					}
					addDt(""+save,amt);
				}
				catch (Exception e)
				{

				}
			}
			if((""+cur).equals("e"))
			{
				active = false;
			}
			if((""+cur).equals("="))
			{
				wait = 1000;
				delay.start();
			}
			if((""+cur).equals("-"))
			{
				wait = 300;
				delay.start();
			}
			if((""+cur).equals("."))
			{
				wait = 100;
				delay.start();
			}
			if(active)
			{
				cur = this.exe[pp];
				pp++;
			}
		}
		if(wait > 0)
		{
			if(delay.getTime()>wait)
			{
				wait = 0;
				delay.clear();
			}
		}
	}

	public boolean isInt(String a)
	{
		try{Integer.parseInt(a);}catch (Exception e){return false;}
		return true;
	}

	public void addDt(String dir, int power)
	{
		if(dir.equals("f"))
		{
			dX = pn((int)dX)*(power);
			dY = pn((int)dY)*(power);
		}
		if(dir.equals("b"))
		{
			dX = -pn((int)dX)*(power);
			dY = -pn((int)dY)*(power);
		}
	}

	public int pn(int a)
	{
		return (a/Math.abs(a));
	}

	public void render() throws IOException
	{

		if(active)
		{
			Color.white.bind();
			texture.bind();
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(x-enl,y-enl);
			GL11.glTexCoord2f(1,0);
			GL11.glVertex2f(x+texture.getTextureWidth()+enl,y-enl) ;
			GL11.glTexCoord2f(1,1);
			GL11.glVertex2f(x+texture.getTextureWidth()+enl,y+texture.getTextureHeight()+enl);
			GL11.glTexCoord2f(0,1);
			GL11.glVertex2f(x-enl,y+texture.getTextureHeight()+enl);
			GL11.glEnd();
		}

	}

	public void enlarge(int s)
	{
		enl = s;
	}
}
