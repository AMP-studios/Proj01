import CustomUtils.AudioController;
import CustomUtils.AudioControllerException;
import CustomUtils.Time;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.ImageIOImageData;

import javax.imageio.ImageIO;
import javax.tools.Tool;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.*;

//import static javax.swing.plaf.metal.MetalBumps.createBuffer;

/**
 * The LibTest class is the main class. 
 * 
 * Within this class sprites are drawn, deleted, and updated.
 * Basically contains everything the game has to offer. 
 * @author Andrey Grebenik (Kuro)
 * @version 1.0
 * @since 2016-12-3
 * */
//jh
public class LibTest {
	public static int SCORE = 0;
	private static boolean debug =false;
	public static String Screen_state = "menu";
	private static boolean isFullScreen=false;
	//screen width and height controlling variables.
	private static final int W = 800;
	private static final int H = 640;

	private static ArrayList<GLenemy> torem = new ArrayList<>();

	private static boolean limit_U = false;
	private static boolean limit_D = false;
	private static boolean limit_L = false;
	private static boolean limit_R = false;
	
	//currently variables to save player position to be removed later.
	private static int x = 200;
	private static int y = 100;

	GLtile[][] grid_T = new GLtile[H/32][W/32];

	static GLplayer PLAYER;

	//A stopwatch which holds the time since the game started
	private static final Time gameTime=new Time();

	private static Time doorCooldown=new Time();

	private static int currentUnit = 0;

	private static String curUnit = "";

	private static long lastUpdateTime=0;

	private static ArrayList<GLtext> text = new ArrayList<>();

	private static String curLevel = "lr10";

	static GLtile[][][] grid = new GLtile[3][640/32][800/32];

	private static ArrayList<GLtile> tiles = new ArrayList<>();

	private static ArrayList<GLtile> images = new ArrayList<>();

	private static ArrayList<String> names = new ArrayList<>();

	private static ArrayList<String> coll = new ArrayList<>();

	private static ArrayList<GLbutton> buttons = new ArrayList<>();

	private static ArrayList<GLpreload> PRELOADS = new ArrayList<>();

	public static final ArrayList<GLenemy> enemies = new ArrayList<>();

	private static ArrayList<String> existingTiles = new ArrayList<>();
	private static ArrayList<String> newTiles = new ArrayList<>();
	private static AudioController ac = new AudioController();

	private static String path = "src\\Assets\\Art\\Tiles\\";
	private static PrintWriter in;

	private static ArrayList<GLdoor> doors = new ArrayList<>();


	/**
	 * start() initializes the GL and then renders, updates, and takes input.
	 * Usually this is where you would put anything you would want to happen every tick.
	 * @throws IOException if invalid path is specified
	 */
	public static void start() throws IOException , CustomUtils.AudioControllerException, ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException,InvocationTargetException
	{
		//in = new PrintWriter(path+"tiles.txt","UTF-8");

		initGL(W,H);
		init();
		//createImage("whiteBack.png",0,0);
		while (true) {
			long curTime=gameTime.getTime();
			double dt=(curTime-lastUpdateTime)/1000.0;
			lastUpdateTime=curTime;
			glClear(GL_COLOR_BUFFER_BIT);
			RENDER(dt);
			UPDATE(dt);
			INPUT(dt);
			Display.update();
			Display.sync(100);
			if (Display.isCloseRequested()) {
				Display.destroy();
				System.exit(0);
			}

		}
	}
 
	/**
	 * Initializes the game and creates the screen on which everything is done.
	 * @param width the height of the screen.
	 * @param height the width of the screen.
	 */
	private static void initGL(int width, int height)
	{
		try 
		{
			Display.setDisplayMode(new DisplayMode(width,height));
			//Display.setFullscreen(true);
			//Display.setResizable(true);
			//Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
			Display.setTitle("Energy Crisis");
			Display.setIcon(new ByteBuffer[] {
					new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("src/Assets/ico16.png")), false, false, null),
					new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("src/Assets/ico32.png")), false, false, null)
			});
			Display.setVSyncEnabled(true);
			Display.create();

		} 
		catch (Exception e)
		{
			e.printStackTrace();
			//System.exit(0);
		} 
		glEnable(GL_TEXTURE_2D);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glViewport(0,0,width,height);
		glMatrixMode(GL_MODELVIEW);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, height, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
	}

	/**
	 * Ransoms and integer between @param min and @param max
	 * @param min minimum value to random.
	 * @param max maximum value to random.
	 * @return returns a random between @param min and @param max.
	 */
	static int rRn(int min, int max)
	{
		return (int)(Math.random()*max)+min;
	}
	
	/**
	 * Creates an GLimage at the position [ @param x, @param y ] with the image @param path.
	 * @param path the path of the image for the GLimage.
	 * @param x the x coordinate at which to create the GLimage.
	 * @param y the y coordinate at which to create the GLimage.
	 * @throws IOException if an invalid file path is specified.
	 */
	public static void createImage(String path, int x, int y, String tag) throws IOException
	{
		GLtile tex = new GLtile(path,x,y,'x','x');
		images.add(tex);
	}

	private static GLbutton createButton(String normal, String hover, String click, int x, int y, String tag) throws IOException
	{
		GLbutton tex = new GLbutton(normal,hover,click,x,y,tag);
		buttons.add(tex);
		return tex;
	}

	/**
	 * Creates text with a size of @param size containing the string (s).
	 * @param s the text to be  displayed
	 * @param x the x position of the first letter.
	 * @param y the y position of the first letter.
	 * @param size changes the size of text, minimum is 0
	 * @throws IOException if an invalid path is specified
	 */
	public static GLtext createText(String s, int x, int y, int size) throws IOException
	{
		GLtext tex = new GLtext(s,x,y,size);
		text.add(tex);
		return tex;
	}

	private static void createPlayer(int x, int y, double health, double speed, double rate) throws IOException, AudioControllerException {
		PLAYER = new GLplayer(x,y,health,speed,rate);
	}

	public static void createEnemy(int x, int y, double health, double speed, double rate) throws IOException, AudioControllerException {
		GLenemy tex = new GLenemy(x,y,health,speed,rate);
		enemies.add(tex);
	}

	public static void createImage(String name, int x, int y) throws IOException
	{
		GLtile tex = new GLtile(name,x,y,'x','x');

	}

	private static void createTile(String img, int x, int y, char use, char type) throws IOException
	{
		if(isExisting(img))
		{
			use=existingTiles.get(findExisting(img)).split(",")[1].toCharArray()[0];
		}
		else
		{
			img = "-invis.png";
		}
		GLtile tex = new GLtile("tl-"+img,x,y,use,type);
		tex.tag = img;
		Tools.p("["+(tiles.size()+1)+"] ld-> "+tex.tag);
		tiles.add(tex);
	}

	private static void createPreload(String name) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException, InvocationTargetException {
		GLpreload tex = new GLpreload(name);
		PRELOADS.add(tex);
	}



	/**
	 * This is used to load images into the game before the first update loop.
	 * Things like backgrounds.
	 * @throws FileNotFoundException if a file not found exception occurs during GLimage creation.
	 */
	public static void init() throws IOException, CustomUtils.AudioControllerException, ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException,InvocationTargetException
	{
		try {


			//createText("hello world",0,0,10);
			doorCooldown.start();
			loadExisting();
			Tools.p(existingTiles);
			preload();
			loadMusic();
			loadDoors();


			//createEnemy(100,200,2,2,1000);


		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static GLtile findTile(String tag)
	{
		for(GLtile a :tiles)
		{
			if(("tl-"+a.tag).equals(tag))
			{
				return a;
			}
		}
		return null;
	}
	private static boolean isExisting(String name)
	{
		int at = findExisting(name);
		return at > 0 && existingTiles.get(at).split(",")[0].equals(name);
	}

	private static int findExisting(String name)
	{
		int i = 0;
		for(String a : existingTiles)
		{

			String[] br = a.split(",");
			if(br[0].equals(name))
			{
				return i;
			}
			i++;
		}
		return -1;
	}

	private static void loadExisting() throws IOException
	{
		String path = "src\\Assets\\Art\\Tiles\\";
		Scanner in = new Scanner(new FileReader(path+"tiles.txt"));
		while(in.hasNextLine())
		{
			String x = in.nextLine();
			existingTiles.add(x);
			Tools.bp(x);
		}
	}

	private static GLtile findTile(char tag)
	{
		for(GLtile a :tiles)
		{
			if((""+a.sm).equals(""+tag))
			{
				return a;
			}
		}
		return tiles.get(0);
	}


	private static boolean chkCol() throws IOException, AudioControllerException
	{
		boolean k = false;
		int q = 0;
		int w = 0;
		int si = 28;
		int ct = (32-si)/2;

		for(GLtile[] a : grid[1])
		{
			for (GLtile l : a)
			{
				int x = l.x;
				int y = l.y;
				int px = (int)PLAYER.x+ct;
				int py = (int)PLAYER.y+ct;

				if((l.tp+"").equals("#") && (x<px+si && x+si>px) && (y<py+si && y+si>py))
				{
					String ip = "true:"+l.x+":"+l.y;
					if(coll.contains(ip))
					{
						coll.remove(ip);
					}
					coll.add(ip);

					k = true;

				}
				if((l.tp+"").equals("@") && (x<px+si && x+si>px) && (y<py+si && y+si>py)&&doorCooldown.getTime()>1000)
				{

					//Tools.bp(l.sm);
					for (GLdoor door : doors) {
						if (door.me == l.sm) {
							if (!door.to.equals(curLevel)) {
								loadMapFromPreload(door.to);
								PLAYER.x = door.x1 * 32;
								PLAYER.y = door.y1 * 32;
								Tools.bp("forward load");
							} else {
								loadMapFromPreload(door.from);
								PLAYER.x = door.x2 * 32;
								PLAYER.y = door.y2 * 32;
								Tools.bp("backward load");
							}
						}
					}
					doorCooldown.clear();
					doorCooldown.start();
				}
			}
		}
		return k;
	}

	private static void preload()throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException,InvocationTargetException
	{
		ArrayList<String> maps = new ArrayList<>();
		String current = new java.io.File( "." ).getCanonicalPath();
		File folder = new File(current+"/src/Assets/Scenes");
		File[] listOfFiles = folder.listFiles();
		assert listOfFiles!=null;
		for(java.io.File listOfFile : listOfFiles) {
			if(listOfFile.isFile()&&listOfFile.getName().endsWith("_0.map")) {

				maps.add(listOfFile.getName().split("_")[0]);
				//Tools.bp("Maps : "+listOfFile.getName());
			}
		}

		for (String map : maps) {
			createPreload(map);
		}
	}

	private static void loadMusic() throws AudioControllerException, IOException
	{
		for(GLpreload p : PRELOADS)
		{
			ac.addSound("/src/Assets/Audio/BGM/"+p.music,p.music);
		}
	}

	private static void loadMapFromPreload(String name) throws AudioControllerException, IOException
	{
		boolean fail = true;
		for (GLpreload PRELOAD : PRELOADS) {
			if (PRELOAD.getName().equals(name)) {
				grid = PRELOAD.getGrid();
				enemies.clear();
				for (int q = 0; q < PRELOAD.enemies.size(); q++) {
					enemies.add((GLenemy) PRELOAD.enemies.get(q));
				}
				fail = false;
				if(!ac.isPlaying(PRELOAD.music))
				{
					ac.stopAll();
					ac.playMusic(PRELOAD.music,1,true);
				}
			}
		}
		if(!fail)
		{
			curLevel = name;
		}

	}

	public static GLtile[][][] dupe(GLtile[][][] grid)
	{
		GLtile[][][] ret = new GLtile[grid.length][grid[0].length][grid[0][0].length];
		int q = 0;
		int w = 0;
		int z = 0;
		for(GLtile[][] gr : grid)
		{
			for (GLtile[] a : gr)
			{
				for (GLtile ignored : a)
				{
					ret[z][q][w] = grid[z][q][w];
					w++;
				}
				w=0;
				q++;
			}
			q=0;
			z++;
		}
		return ret;
	}

	private static void loadDoors() throws IOException
	{
		String path = "src\\Assets\\Scenes\\";
		Scanner temp = new Scanner(new FileReader(path+"doors.txt"));
		while(temp.hasNextLine())
		{
			String s = temp.nextLine();
			if(!s.equals(""))
			{
				Tools.bp("added door "+s);
				String[] br = s.split(",");
				//Cm,C1,C2,x,x,y,y,z,z,to,from
				if(br.length>1)
				{
					GLdoor door = new GLdoor(br[0].toCharArray()[0],br[1].toCharArray()[0],br[2].toCharArray()[0],
							Integer.parseInt(br[3]),
							Integer.parseInt(br[4]),
							Integer.parseInt(br[5]),
							Integer.parseInt(br[6]),
							Integer.parseInt(br[7]),
							Integer.parseInt(br[8]),
							br[9],br[10]);
					doors.add(door);
					door.p();
				}
			}
		}
	}

	public static void loadMap(String name) throws IOException
	{
		//Tools.disablePrinting();
		int q = 0;
		int w = 0;
		int z = 0;
		Tools.p("loading grid");
		for(GLtile[][] gr : grid)
		{
			for(GLtile[] a : gr)
			{
				for(GLtile ignored : a)
				{
					//Tools.p(q+":"+w);
					if(z==0)
					{
						grid[z][q][w] = new GLtile("black.png",w*32,q*32,(char)10000000,'*');
						Tools.p("["+w+":"+q+":"+z+"] loaded base");
					}
					else
					{
						grid[z][q][w] = new GLtile("invisible.png",w*32,q*32,(char)10000000,'*');
						Tools.p("["+w+":"+q+":"+z+"] loaded invis");
					}
					w++;
				}
				q++;
				w = 0;
			}
			q = 0;
			z++;
		}
		String current = new java.io.File( "." ).getCanonicalPath();
		File folder = new File(current+"/src/Assets/Art/Tiles");
		File[] listOfFiles = folder.listFiles();
		assert listOfFiles!=null;
		for(java.io.File listOfFile : listOfFiles) {
			if(listOfFile.isFile()&&listOfFile.getName().startsWith("tl-")) {
				names.add(listOfFile.getName());
			}
		}
		for(String a:names)
		{
			createTile(a.substring(3),-100,-100,(char)(tiles.size()+1000),'*');
		}

		String path = "src\\Assets\\Scenes\\";

		Scanner in;

		Scanner in2;
		q = 0;
		w = 0;
		z = 0;
		Tools.p("loading tiles from save");
		for(GLtile[][] gr : grid)
		{
			in = new Scanner(new FileReader(path+name+"_"+z+".map"));
			Tools.p("opened "+path+name+"_"+z+".map");
			in2  = new Scanner(new FileReader(path+name+"_"+z+".col"));
			Tools.p("opened "+path+name+"_"+z+".col");
			for(GLtile[] a : gr)
			{
				String s = in.nextLine();
				Tools.p("read : "+s+" from "+path+name+"_"+z+".map");
				String s2 = in2.nextLine();
				Tools.p("read : "+s2+" from "+path+name+"_"+z+".col");
				char[] sp = s.toCharArray();
				char[] sp2 = s2.toCharArray();
				for(GLtile b : a)
				{
					GLtile t = findTile(sp[w]);
					b = new GLtile("tl-"+t.tag,w*32,q*32,sp[w],sp2[w]);
					if(b.sm!=(char)10000000)
					{
						grid[z][q][w] = b;
						Tools.p("loaded tile : "+b.p());
					}
					w++;
				}
				w=0;
				q++;
			}
			q=0;
			z++;
		}
		Tools.p("finished loading");

	}
 
	/**
	 * Where you do things to GLimages to make the game function. 
	 * Yes I know it's vague but that is what it is. 
	 * @throws IOException if a file not found exception occurs during GLimage creation.
	 */
	private static void UPDATE(double dt) throws IOException {
		if(Screen_state.equals("Game"))
		{
			for(GLtext t: text)
			{
				if(t.tag.equals("SCORE"))
				{
					t.contents = ""+SCORE;
				}
			}
		}
		if(PLAYER!=null)
		{
			PLAYER.curWeapon.paused =debug;
		}
		for(GLbutton a : buttons)
		{
			if(a.click) {
				if(a.tag.startsWith("[PLAY]"))
				{
					Screen_state="loadFirstMap";
				}
			}
		}
	}
	
	/**
	 * Renders all GLimages in images[] and runs their updates their information.
	 */
	private static void RENDER(double dt) throws IOException , CustomUtils.AudioControllerException
	{

		if(Screen_state.equals("menu"))
		{
			createImage("background_m.png",0,0,"back");
			createButton("bPlay.png","bPlay.png","bPlay.png",300,420,"[PLAY]");
			Screen_state = "awaitPlay";
		}
		if(Screen_state.equals("loadFirstMap"))
		{

			createPlayer(200,200,100,100,100);
			createImage("scoreBox.png",0,0,">scoreBox");
			GLtext temp = createText(""+SCORE,40,3,0);
			temp.tag = ">SCORE";
			Screen_state="Game";
			loadMapFromPreload("l1r0");
		}
		for(GLtile image : images) {
			image.render();
		}

		for(GLbutton b : buttons)
		{
			b.render();
			b.update(org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), org.lwjgl.input.Mouse.isButtonDown(0), dt);
		}

		for(GLtile[][] gr: grid)
		{
			for(GLtile[] a : gr)
			{
				for(GLtile l : a)
				{
					if(l!=null&&l.tp=='@')
					{
						l.render();
					}
				}
			}
		}
		for(GLtile[][] gr: grid)
		{
			for(GLtile[] a : gr)
			{
				for(GLtile l : a)
				{
					if(l!=null&&l.tp!='@')
					{
						l.render();
					}
				}
			}
		}

		for(GLenemy e : enemies)
		{
			e.render();
			if(e!=null &&!e.alive)
			{
				torem.add(e);
			}
		}

		enemies.removeAll(torem);

		torem.clear();

		for(GLtile image : images) {
			if(image!=null&&image.tag!=null&&image.tag.startsWith(">"))
			{
				image.render();
			}
		}

		for(GLtext aText : text) {
			if(aText!=null){aText.render();}
		}
		if(PLAYER!=null)
		{
			PLAYER.render();
		}




	}
 
	/**
	 * You casual main(String [] args) function
	 * Starts the game ...
	 * @param args ... they are args ...
	 * @throws IOException if you suck at specifying file paths...
	 */
	public static void main(String[] args) throws IOException , CustomUtils.AudioControllerException, ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException,InvocationTargetException

	{
		gameTime.start();
		start();
	}



	/**
	 * Controls user input.
	 * Mouse and Keyboard.
	 */
	private static void INPUT(double dt) throws IOException , CustomUtils.AudioControllerException
	{
		if (Mouse.isButtonDown(0))
		{

		}
		else if (Mouse.isButtonDown(1)) {

		}
		if(PLAYER!=null&&Screen_state.equals("Game"))
		{
			if(Keyboard.isKeyDown(Keyboard.KEY_3))
			{
				for(int i=0; i < PRELOADS.size(); i++) {
					Tools.bp(PRELOADS.get(i).getName());
				}
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_4))
			{

			}

			if(Keyboard.isKeyDown(Keyboard.KEY_1))
			{
				debug = true;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_2))
			{
				debug = false;
			}

			String q = "";

			boolean W = Keyboard.isKeyDown(Keyboard.KEY_W);
			boolean S = Keyboard.isKeyDown(Keyboard.KEY_S);
			boolean A = Keyboard.isKeyDown(Keyboard.KEY_A);
			boolean D = Keyboard.isKeyDown(Keyboard.KEY_D);

			if (W) {
				q = "up";
				PLAYER.facing = q;
			}

			if (S) {
				q = "down";
				PLAYER.facing = q;
			}

			if (A) {
				q = "left";
				PLAYER.facing = q;
			}

			if (D) {
				q = "right";
				PLAYER.facing = q;
			}

			if (W && A)
			{
				q = "upleft";
			}

			if (W && D)
			{
				q = "upright";
			}

			if(S && A)
			{
				q = "downleft";
			}

			if(S && D)
			{
				q = "downright";
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_UP) ||
					Keyboard.isKeyDown(Keyboard.KEY_DOWN) ||
					Keyboard.isKeyDown(Keyboard.KEY_LEFT) ||
					Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			{
				PLAYER.shooting = true;
				if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
					PLAYER.facing = "up";
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
					PLAYER.facing = "down";
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					PLAYER.facing = "left";
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					PLAYER.facing = "right";
				}
			}
			else
			{
				PLAYER.shooting = false;
			}
			int m = 0;
			boolean s = chkCol();
			if(s)
			{
				for(String aColl : coll) {
					getDir(aColl);
				}
			}
			if (q.equals("up")&&!limit_U) {
				PLAYER.y -= (PLAYER.speed+m)*dt;
			}

			if (q.equals("down")&&!limit_D) {
				PLAYER.y += (PLAYER.speed+m)*dt;
			}

			if (q.equals("left")&&!limit_L) {
				PLAYER.x -= (PLAYER.speed+m)*dt;
			}

			if (q.equals("right")&&!limit_R) {
				PLAYER.x += (PLAYER.speed+m)*dt;
			}

			if (q.equals("upleft")) {
				if(!limit_U)
				{
					PLAYER.y -= (PLAYER.speed+m)*dt;
				}
				if(!limit_L)
				{

					PLAYER.x -= (PLAYER.speed+m)*dt;
				}
			}

			if (q.equals("upright")) {
				if(!limit_U)
				{
					PLAYER.y -= (PLAYER.speed+m)*dt;
				}
				if(!limit_R)
				{
					PLAYER.x += (PLAYER.speed+m)*dt;
				}
			}

			if (q.equals("downleft")) {
				if(!limit_D)
				{
					PLAYER.y += (PLAYER.speed+m)*dt;
				}
				if(!limit_L)
				{

					PLAYER.x -= (PLAYER.speed+m)*dt;
				}
			}

			if (q.equals("downright")) {
				if(!limit_D)
				{
					PLAYER.y += (PLAYER.speed+m)*dt;
				}
				if(!limit_R)
				{
					PLAYER.x += (PLAYER.speed+m)*dt;
				}
			}

			if(!s)
			{
				limit_R = false;
				limit_L = false;
				limit_D = false;
				limit_U = false;
			}
			coll.clear();
		}

	}

	private static int getDir(String spec)
	{
		String[] a = spec.split(":");
		int ox = Integer.parseInt(a[1])+16;
		int oy = Integer.parseInt(a[2])+16;
		int mx = (int)PLAYER.x+16;
		int my = (int)PLAYER.y+16;
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

	//levels start with 0 and so do areas
	//filename is %area%-%level% example 1-5;
	public static void LOAD(int area, int level)
	{

	}
}

