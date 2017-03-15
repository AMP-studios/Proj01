import CustomUtils.AudioControllerException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.Random;

import static java.lang.System.*;
/**
 * This is the button class, creates a clickable button.
 * @author Andrey Grebenik (Kuro)
 * @version 1.0
 * @since 2016-11-19
 */
public class GLbutton {

	private CustomUtils.AudioController a;

	private Texture b_normal;
	private Texture b_hover;
	private Texture b_click;

	public String spec = "";
	public boolean isEnemy = false;
	public boolean innocent = true;

	public String n_path;
	public String h_path;
	public String c_path;

	public boolean hold = false;
	private boolean holding = false;

	private Texture currentTex;

	int identity;

	int x = 0;
	int y = 0;

	String t_me = "";

	int sV = 0;
	int sH = 0;

	int enl = 0;

	int height;
	int width;

	boolean hover;
	boolean click;

	int mouseX;
	int mouseY;

	String tag = "default.tag";

	/**
	 * creates a button
	 * @param normal the frame that will be displayed when the button is not selected or clicked.
	 * @param Px x position of button.
	 * @param Py y position of button.
	 * @param tag the tag of this button.
	 * @throws IOException
	 */
	public GLbutton(String normal, int Px, int Py, String tag) throws IOException{

		this.x = Px;
		this.y = Py;
		this.tag = tag;
		this.n_path = normal;
		out.println("loading button ...");
		String pth = "Assets\\Art\\Tiles\\";
		b_normal = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+normal));
		b_hover = b_normal;
		b_click = b_normal;
		out.println("ld >> "+normal);
		height = b_normal.getImageHeight();
		width = b_normal.getImageWidth();
		init();

	}

	/**
	 * creates a button
	 * @param normal the frame that will be displayed when the button is not selected or clicked.
	 * @param hover when you hover over the button this frame will  be  displayed.
	 * @param Px x position of button.
	 * @param Py y position of button.
	 * @param tag the tag of this button.
	 * @throws IOException
	 */
	public GLbutton(String normal, String hover, int Px, int Py, String tag) throws IOException{
		this.x = Px;
		this.y = Py;
		this.tag = tag;
		this.n_path = normal;
		this.h_path = hover;
		String pth = "Assets\\Art\\Tiles\\";
		out.println("loading button ...");
		b_normal = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+normal));
		out.println("ld >> "+normal);
		b_hover = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+hover));
		out.println("ld >> "+hover);
		b_click = b_normal;
		height = b_normal.getImageHeight();
		width = b_normal.getImageWidth();
		init();
	}

	/**
	 * creates a button
	 * @param normal the frame that will be displayed when the button is not selected or clicked.
	 * @param hover when you hover over the button this frame will  be  displayed.
	 * @param click if you click this frame wi ll be displayed.
	 * @param Px x position of button.
	 * @param Py y position of button.
	 * @param tag the tag of this button.
	 * @throws IOException
	 */
	public GLbutton(String normal,String hover, String click, int Px, int Py, String tag) throws IOException{
		this.x = Px;
		this.y = Py;
		this.tag = tag;
		this.n_path = normal;
		this.h_path = hover;
		this.c_path = click;
		//Tools.p("loading button ...");
		String pth = "Assets\\Art\\Tiles\\";
		b_normal = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+normal));
		//Tools.p("ld >> "+normal);
		b_hover = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+hover));
		//Tools.p("ld >> "+hover);
		b_click = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+click));
		//Tools.p("ld >> "+click);
		height = b_normal.getImageHeight();
		width = b_normal.getImageWidth();
		init();
	}

	/**
	 * initializes the button's variables
	 * @throws IOException if you cant input one damn filename right.
	 */
	public void init() throws IOException
	{
		Random rand = new Random();
		this.identity = rand.nextInt();
		a = new CustomUtils.AudioController();
		currentTex = b_normal;
		if(n_path.startsWith("tl--"))
		{
			t_me = n_path.substring(0,3)+n_path.substring(4,n_path.indexOf(".png"));
			String s = "";
			for(char a : t_me.toCharArray())
			{
				if(!((a+"").matches("\\d"))) {
					s+=a;
				}
			}
			t_me = s;
			out.println(t_me);
		}
	}

	/**
	 *updates the coordinates for mouse x and mouse y.
	 * @param mX
	 * @param mY
	 * @param c
	 */
	public void update(int mX, int mY, boolean c, double dt)
	{
		mouseX = mX;
		mouseY = Display.getHeight()-mY;
		height = currentTex.getImageHeight();
		width = currentTex.getImageWidth();
		if(click && holding)
		{
			holding = false;
		}
		if(mouseInBox() && c && !holding)
		{
			this.click = true;
			currentTex = b_click;
			enlarge(2);
			if(hold)
			{
				holding = true;
			}
			render();
		}
		else if(mouseInBox()&& !holding)
		{
			this.hover = true;
			this.click = false;
			currentTex = b_hover;
			enlarge(1);
			render();
		}
		else if(!holding)
		{
			this.click = false;
			this.hover = false;
			currentTex = b_normal;
			enlarge(0);
			render();
		}
	}

	/**
	 * renders the button adding stretches and enlarges
	 */
	public void render()
	{
		Color.white.bind();
		currentTex.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0,0);
		GL11.glVertex2f(x-enl,y-enl);
		GL11.glTexCoord2f(1,0);
		GL11.glVertex2f(x+currentTex.getTextureWidth()+sH+enl,y-enl) ;
		GL11.glTexCoord2f(1,1);
		GL11.glVertex2f(x+currentTex.getTextureWidth()+sH+enl,y+currentTex.getTextureHeight()+sV+enl);
		GL11.glTexCoord2f(0,1);
		GL11.glVertex2f(x-enl,y+currentTex.getTextureHeight()+sV+enl);
		GL11.glEnd();
	}

	/**
	 *
	 * @param size
	 */
	public void enlarge(int size)
	{
		this.enl = size;
	}

	/**
	 *
	 * @param width
	 * @param height
	 */
	public void stretch(int width, int height)
	{
		this.sV = height;
		this.sH = width;
	}

	public void Hold()
	{
		hold = true;
	}

	/**
	 *
	 * @return
	 */
	public boolean mouseInBox()
	{
		return (mouseX>=this.x&&mouseX<=this.x+width+sH)&&(mouseY>=this.y&&mouseY<=this.y+height+sV);
	}

	/**
	 *
	 * @param path The path to file
	 * @param name The name of the audio object
	 * @throws IOException For invalid path
	 * @throws AudioControllerException for invalid name
	 */
	public void addFX(String path, String name) throws IOException, AudioControllerException
	{
		a.addSound("Assets\\Audio\\SFX\\"+path, name+".sf");
	}

	/**
	 *
	 * @param name the name of the audio file to play
	 * @throws AudioControllerException When given an invalid name
	 */
	public void playFX(String name,double volume) throws AudioControllerException
	{
		a.playSoundEffect(name+".sf",volume);
	}
}
