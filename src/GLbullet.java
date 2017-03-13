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
	double x;
	private int enl = 0;
	double y;
	double dX;
	double dY;
	double damage;
	int rad = 10;
	private boolean moving = false;
	String dir = "";
	boolean active = true;
	private int wait = 0;
	ArrayList<Texture> frames = new ArrayList<>();
	private Texture texture;
	private Time delay=new Time();
	private char cur = 0;
	private int pp =0;
	private char[] exe;
	double ZERO = 10E-324;
	GLbullet(int x, int y, String exe) throws IOException
	{
		this.x = x;
		this.y = y;
		String pth = "Assets\\Art\\Tiles\\";
		texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth +"b-1.png"));
		this.exe = exe.toCharArray();
		cur = this.exe[pp];
		pp++;
		moving = true;

	}
	public void stop()
	{
		moving = false;
	}

	public void update() throws IOException
	{
		if(moving)
		{
			this.x += this.dX;
			this.y += this.dY;
		}
			if(wait < 1 && active)
		{
			if((""+cur).equals("S"))
			{
				this.stop();
			}

			if((""+cur).equals("f")||(""+cur).equals("b")||(""+cur).equals("l")||(""+cur).equals("r"))
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
		moving = true;
		if(dir.equals("f"))
		{
			dX = pn(dX)*(power);
			dY = pn(dY)*(power);
		}
		if(dir.equals("b"))
		{
			dX = -pn(dX)*(power);
			dY = -pn(dY)*(power);
		}
	}

	public double pn(double a)
	{
		if(a==0)
		{
			return 0;
		}
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
			GL11.glVertex2f(tf(x-enl),tf(y-enl));
			GL11.glTexCoord2f(1,0);
			GL11.glVertex2f(tf(x+texture.getTextureWidth()+enl),tf(y-enl)) ;
			GL11.glTexCoord2f(1,1);
			GL11.glVertex2f(tf(x+texture.getTextureWidth()+enl),tf(y+texture.getTextureHeight()+enl));
			GL11.glTexCoord2f(0,1);
			GL11.glVertex2f(tf(x-enl),tf(y+texture.getTextureHeight()+enl));
			GL11.glEnd();
		}

	}

	public float tf(double a)
	{
		return (float)a;
	}

	public void enlarge(int s)
	{
		enl = s;
	}
}
