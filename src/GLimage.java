import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class that controls sprites
 * Can i get any more vague ... i think i can.
 * @author Andrey Grebenik (Kuro)
 * @version 1.0
 * @since 2016-11-19
 */

public class GLimage extends Component {

	private CustomUtils.AudioController a;
	// the texture.
	private Texture texture;
	// special id that is given to every object.
	private int identity;
	// x and y position.
	public int x;
	public int y;

	int enl = 0;

	// vertical and horisontal stretch
	private int sV = 0, sH = 0;
	// texture height and width.
	int height;
	int width;
	// the tag that the GLimage has/has not been given
	private String tag = "default.tag";
	// Temporary variable slots.

	private boolean normal = true;

	// list of all GLimages so the GLimage can know where what is.
	private ArrayList<GLimage> images = new ArrayList<>();
	//the filepath of the current image.
	private String path;
	private int max;
	
	/**
	 * When the GLimage is initialized this is called. 
	 * Creates a GLimage at [ @param Px, @param Py ] with an image of @param path.
	 * @param path the image path of the GLimage
	 * @param Px the start x position.
	 * @param Py the start y position.
	 * @throws IOException if you are a complete idiot and somehow can't get your filepaths right.
	 */
	public GLimage(String path, int Px, int Py) throws IOException
	{
		x = Px;
		y = Py;
		this.path = path;
		tag = "default";
		init();
	}
	public GLimage(String path, int Px, int Py, boolean letter) throws IOException
	{
		x = Px;
		y = Py;
		this.path = path;
		normal = !letter;
		tag = "default";
		init();
	}
	
	/**
	 * When the GLimage is initialized this is called. 
	 * Creates a GLimage at [ @param Px, @param Py ] with an image of @param path.
	 * @param path the image path of the GLimage
	 * @param Px the start x position.
	 * @param Py the start y position.
	 * @param tag the tag with which the object is going to be tagged.
	 * @throws IOException if you are a complete idiot and somehow can't get your filepaths right.
	 */
	public GLimage(String path, int Px, int Py, String tag) throws IOException
	{
		tag = tag;
		x = Px;
		y = Py;
		this.path = path;

		init();
	}
	
	/**
	 * Loads the image and sets width and height of it. 
	 * @throws IOException if you can't bother to change your '\'to a '\\' or a '/'
	 */
	public void init() throws IOException
	{
		String pth = "";
		if(normal)
		{
			pth = "Assets\\Art\\Tiles\\";
		}
		texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+path));
		height = texture.getImageHeight();
		width = texture.getImageWidth();
		Random rand = new Random();
		this.identity = rand.nextInt();
		a = new CustomUtils.AudioController();
		
	}
	
	/**
	 * render the GLimage on the screen at [ @param x, @param y ].
	 */
	public void render() {
		Color.white.bind();
		texture.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x - enl, y - enl);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + texture.getTextureWidth() + sH + enl, y - enl);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + texture.getTextureWidth() + sH + enl, y + texture.getTextureHeight() + sV + enl);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x - enl, y + texture.getTextureHeight() + sV + enl);
		GL11.glEnd();
	}

	public void enlarge(int size)
	{
		this.enl = size;
	}

	public void stretch(int width, int height)
	{
		this.sV = height;
		this.sH = width;
	}

	public void addFX(String path, String name) throws IOException, CustomUtils.AudioControllerException
	{
		a.addSound("Assets\\Audio\\SFX\\"+path, name+".sf");
		//Tools.p("loaded FX > Assets\\Audio\\SFX\\"+path+" as "+name+".sf");
	}

	public void playFX(String name,double volume) throws CustomUtils.AudioControllerException
	{
		a.playSoundEffect(name+".sf",volume);
		//Tools.p("playing FX > "+name+".sf");
	}

	public void addMU(String path, String name) throws IOException, CustomUtils.AudioControllerException
	{
		a.addSound("Assets\\Audio\\BGM\\"+path, name+".mu");
		//Tools.p("loaded MU > Assets\\Audio\\SFX\\"+path+" as "+name+".mu");
	}

	public void playMU(String name,double volume) throws CustomUtils.AudioControllerException
	{
		a.playMusic(name+".mu",volume,true);
		//Tools.p("playing MU > "+name+".mu");
	}
}
