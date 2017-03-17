import CustomUtils.Time;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;

public class GLtile {
	private String image;
	private String pth = "Assets\\Art\\Tiles\\";
	public Texture texture;
	public char tp = '*';
	// # - wall
	// @n - nth door
	// * - empty tile
	public String me;
	public char sm;
	public int x = 0;
	public int y = 0;
	public int h;
	public int w;
	public int layer=0;
	public String tag;
	public String command = "";
	public String pt;
	public boolean spread = false;
	boolean iden = false;
	private int renderDelay = 0;
	public Time delay = new Time();
	public String temp = "";
	public GLtile(String path,int x, int y, char symbol, char type) throws IOException
	{
		pt = path;
		sm = symbol;
		tp = type;
		this.x = x;
		this.y = y;
		if(path.startsWith("tl--"))
		{
			me = path.substring(path.indexOf("-")+2,path.indexOf("."));
		}
		else
		{
			me = path.substring(path.indexOf("-")+1,path.indexOf("."));
			String s = "";
			for(char a : me.toCharArray())
			{
				if(!((a+"").matches("\\d"))) {
					s+=a;
				}
			}
			me = s;
		}
		//Tools.p(me);
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+path));
	}

	public void setRenderDelay(int d)
	{
		renderDelay = d;
	}

	public void render()
	{
			Color.white.bind();
			texture.bind();
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(x,y);
			GL11.glTexCoord2f(1,0);
			GL11.glVertex2f(x+texture.getTextureWidth(),y) ;
			GL11.glTexCoord2f(1,1);
			GL11.glVertex2f(x+texture.getTextureWidth(),y+texture.getTextureHeight());
			GL11.glTexCoord2f(0,1);
			GL11.glVertex2f(x,y+texture.getTextureHeight());
			GL11.glEnd();
			bulletColl();
	}

	public void bulletColl()
	{
		if(this.tp == '#' && LibTest.PLAYER != null)
		{
			GLplayer me = LibTest.PLAYER;
			for(int i = 0; i < me.curWeapon.bullets.size(); i++)
			{
				GLbullet cur = me.curWeapon.bullets.get(i);
				if(cur.x+16 > this.x && cur.x+16 < this.x + 32)
				{
					if(cur.y+16 > this.y && cur.y+16 < this.y+32)
					{
						cur.active = false;
					}
				}
			}
		}
	}

	public double distTo(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt(Math.pow(Math.abs(x1-x2),2)+Math.pow(Math.abs(y1-y2),2));
	}

	public String p()
	{
		return (this+"{\npath: "+pt+"\ntag: "+tag+"\nx,y: "+x+","+y+"\nsymbol: "+sm+"\ntype"+tp+"\nme: "+me+"\n}");
	}

}
