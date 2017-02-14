import CustomUtils.Time;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import javax.tools.Tool;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.*;
import static org.lwjgl.opengl.GL11.glCallLists;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glGetInteger;
//import static javax.swing.plaf.metal.MetalBumps.createBuffer;
//import static org.lwjgl.opengl.WindowsDisplay.createIcon;

//import static javax.swing.plaf.metal.MetalBumps.createBuffer;

/**
 *
 *
 * The LibTest class is the main class.
 *
 * Within this class sprites are drawn, deleted, and updated.
 * Basically contains everything the game has to offer.
 * @author Andrey Grebenik (Kuro)
 * @version 1.0
 * @since 2016-12-3
 * */

public class TilemapEditor {
    @SuppressWarnings("FieldCanBeLocal")
    private static String tool = "pen";
    public static boolean isFullScreen=false;
    //screen width and height controlling variables.
    private static final int W = 1100;
    private static final int H = 740;

    private static int cL = 1;

    private static GLinput name_this;

    private static int[] xyzDoor = new int[3];
    private static GLinput doorInput;

    private static boolean hasName = false;

    private static GLtile curSel;
    private static GLtile[][][] grid = new GLtile[3][640/32][800/32];
    private static ArrayList<GLtile> tiles = new ArrayList<>();
    public static ArrayList<GLtile> enemies = new ArrayList<>();
    public static ArrayList<GLpic> pics = new ArrayList<>();
    public static ArrayList<String> names = new ArrayList<>();

    //currently variables to save player position to be removed later.
    public static int x = 200;
    public static int y = 100;

    private static GLbutton tp;
    private static GLbutton bt;

    //A stopwatch which holds the time since the game started
    private static final Time gameTime=new Time();

    //images contains all the GLimages currently on screen.
    private static ArrayList<GLimage> images = new ArrayList<>();
    // buttons contains all GLbuttons on the screen.
    private static ArrayList<GLbutton> buttons = new ArrayList<>();
    // text contains all GLtexts on the screen.
    public static ArrayList<GLtext> text = new ArrayList<>();
    // currentTexture is an index of where to add the next GLimage.
    private static int currentTexture = 0;
    // currentTexture is an index of where to add the next GLbutton.
    private static int currentButton = 0;
    // currentTexture is an index of where to add the next GLtext.
    private static int currentText = 0;

    public static int currentUnit = 0;

    public static String curUnit = "";

    private static long lastUpdateTime=0;

    public static String name = "";

    private static Time udE=new Time();

    private static ArrayList<GLinput> inputs = new ArrayList<>();

    public static double dt;
    /**
     * start() initializes the GL and then renders, updates, and takes input.
     * Usually this is where you would put anything you would want to happen every tick.
     * @throws IOException for invalid path
     */
    public static void start() throws IOException , CustomUtils.AudioControllerException {
        //out.println("Level num %area%-%level% example 1-5");
        //out.println("if you are going to load a level enter the name of the level to load.");
        //out.println("if you are creating a new level enter the name for the new level.");
        //out.print("Enter level num: ");
        //name = new Scanner(in).nextLine();

        initGL(W,H);
        init();

        while (true) {
            long curTime=gameTime.getTime();
            dt=(curTime-lastUpdateTime)/1000.0;
            lastUpdateTime=curTime;
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            RENDER(dt);
            UPDATE(dt);
            INPUT();
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
            Display.setTitle(" please enter level name : ");

            //Texture texture = TextureLoader.getTexture("PNG",new FileInputStream("src\\Assets\\ico16.png"));
            //Texture texture1 = TextureLoader.getTexture("PNG",new FileInputStream("src\\Assets\\ico32.png"));
            //ByteBuffer[] icons = new ByteBuffer[4];
 
            //icons[0] = createIco(16,16,true,false,texture);
            //icons[1] = createIco(32,32,false,false,texture1);
            //icons[3] = createIco(128,128,true,true,texture1);

            Display.setVSyncEnabled(true);
            Display.create();

        }
        catch (Exception ignored) {}
        udE.start();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glViewport(0,0,width,height);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, height, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }



    /**
     * Ransoms and integer between @param min and @param max
     * @param min minimum value to random.
     * @param max maximum value to random.
     * @return returns a random between @param min and @param max.
     */
    public static int rRn(int min, int max)
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
    private static void createImage(String path, int x, int y) throws IOException
    {
	    GLimage tex = new GLimage(path,x,y);
        images.add(tex);
        currentTexture+=1;
    }

    public static ByteBuffer createIco(int width,int height,boolean fixAlphas,boolean makeBlackTransparent,Texture texture) {
    
        int drawBuffer = glGetInteger(GL11.GL_DRAW_BUFFER);
    //Draw & stretch a width by height icon onto the back buffer
    glDrawBuffer(GL11.GL_BACK);
    texture.bind();
    GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0,0); GL11.glVertex2f(0,0);
      GL11.glTexCoord2f(1,0); GL11.glVertex2f(width,0);
      GL11.glTexCoord2f(1,1); GL11.glVertex2f(width,height);
      GL11.glTexCoord2f(0,1); GL11.glVertex2f(0,height);
    GL11.glEnd();
 
    //Read the back buffer into the byte buffer icon
    GL11.glReadBuffer(GL11.GL_BACK);
    ByteBuffer icon = BufferUtils.createByteBuffer(width * height * 4);
    GL11.glReadPixels(0,0,width,height,GL11.GL_RGBA,GL11.GL_BYTE,icon);
    //fixAlphas:            In case of OpenGL blending and/or bitmap problems
    //makeBlackTransparent: Cycle through and set black to be transparent
    if(fixAlphas || makeBlackTransparent) {
      for(int y = 0; y < height; y++) {
        for(int x = 0; x < width; x++) {
          int color = y * 4 * width + x * 4;
          int red   = icon.get(color);
          int green = icon.get(color + 1);
          int blue  = icon.get(color + 2);
 
          if(makeBlackTransparent && red == 0 && green == 0 && blue == 0) {
            icon.put(color + 3,(byte)0);
            } else if(fixAlphas) {
            icon.put(color + 3,(byte)255);
            }
        }
            System.out.println();
        }
    }
    glDrawBuffer(drawBuffer);

    return(icon);
  }

    /**
     * Creates an GLimage at the position [ @param x, @param y ] with the image @param path adding the @param tag to it
     * @param path the path of the image for the GLimage.
     * @param x the x coordinate at which to create the GLimage.
     * @param y the y coordinate at which to create the GLimage.
     * @param tag the tag to add to the GLimage
     * @throws IOException if an invalid file path is specified.
     */
    public static void createImage(String path, int x, int y, String tag) throws IOException
    {
        GLimage tex = new GLimage(path,x,y,tag);
        images.add(tex);
        currentTexture+=1;
    }

    /**
     * Creates a button on the screen which can be clicked.
     * to detect if a button is being clicked you would access.
     * buttons[0].click;
     * @param normal the normal, deselected picture.
     * @param hover the picture to be shown on hover.
     * @param click the picture to be shown on click.
     * @param x the x position of the button.
     * @param y the y position of the button.
     * @param tag the tag with which your button will be tagged.
     * @throws IOException if you are absolutely crap at specifying one file ...
     */
    private static GLbutton createButton(String normal, String hover, String click, int x, int y, String tag) throws IOException
    {
        GLbutton tex = new GLbutton(normal,hover,click,x,y,tag);
        buttons.add(tex);
        currentButton++;
        return tex;
    }

    private static GLinput createInput(String normal, int x, int y, String tag) throws IOException
    {
        GLinput tex = new GLinput(normal,x,y,tag);
        inputs.add(tex);
        return tex;
    }

    private static void createPic(String img, int x, int y) throws IOException
    {
        GLpic tex = new GLpic(img,x,y);
        pics.add(tex);
    }

    private static void createTile(String img, int x, int y, char use, char type) throws IOException
    {
        GLtile tex = new GLtile("tl-"+img,x,y,use,type);
        tex.tag = img;
        Tools.p("["+(tiles.size()+1)+"] ld-> "+tex.tag);
        tiles.add(tex);
    }

    /**
     * Creates text with a size of @param size containing the string (s).
     * @param s the text to be  displayed
     * @param x the x position of the first letter.
     * @param y the y position of the first letter.
     * @param size changes the size of text, minimum is 0
     * @throws IOException when improper path is given
     */
    public static void createText(String s, int x, int y, int size) throws IOException
    {
        GLtext tex = new GLtext(s,x,y,size);
        text.add(tex);
        currentText++;
    }

    private static GLbutton findBut(String tag)
    {

        for(GLbutton a :buttons)
        {
            if((a.tag).equals(tag))
            {
                return a;
            }
        }
        return null;
    }

    private static GLtile findTile(String tag)
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

    /**
     * This is used to load images into the game before the first update loop.
     * Things like backgrounds.
     * @throws FileNotFoundException if a file not found exception occurs during GLimage creation.
     */
    public static void init() throws CustomUtils.AudioControllerException, IOException{
        SET_TILES();
        try {
            if(hasName)
            {
                createImage("mainBack.png",0,0);
            }
            else
            {
                name_this = createInput("whiteBack.png", W/2-90,H/2-25,"hue");
                name_this.sel = true;
            }


        } catch (Exception ignored) {}
        if(hasName)
        {
            int q = 0;
            int w = 0;
            int e = 1;
            for(GLtile[][] gr : grid)
            {
                for(GLtile[] a : gr)
                {
                    for(GLtile ignored : a)
                    {
                        //Tools.p(q+":"+w);

                        if(e==1)
                        {
                            GLtile t = new GLtile("tl-default.png",w*32+50,q*32+50,(char)128,(char)128);
                            t.tag = "def";
                            t.layer = e;
                            grid[e-1][q][w] =t;
                            String r = "invisible.png";
                            createButton(r,r,r,w*32+50,q*32+50,"-+-:"+w+":"+q);
                        }else{
                            GLtile t = new GLtile("invisible.png",w*32+50,q*32+50,(char)128,(char)128);
                            t.tag = "def";
                            t.layer = e;
                            grid[e-1][q][w] =t;
                        }
                        w++;
                    }
                    q++;
                    w = 0;
                }
                q=0;
                w=0;
                e++;
            }


            //this bit finds all files that stary with "tl-" and adds their names to a list to be loaded later
            String current = new java.io.File( "." ).getCanonicalPath();
            File folder = new File(current+"/src/Assets/Art/Tiles");
            File[] listOfFiles = folder.listFiles();
            assert listOfFiles!=null;
            int aa = 1;
            for(java.io.File listOfFile : listOfFiles) {
                if(listOfFile.isFile()&&listOfFile.getName().startsWith("tl-")) {
                    names.add(listOfFile.getName());
                }
                if(listOfFile.isFile()&&listOfFile.getName().startsWith("lr-") && aa < grid.length+1) {
                    createButton(listOfFile.getName(),listOfFile.getName(),listOfFile.getName(),aa*42+170,700,"-l-"+aa);
                    Tools.p(aa);
                    aa++;
                }
            }

            int p1 = 900;
            int p2 = 50;
            GLbutton cur_b;
            int i = 0;
            for(String a:names)
            {
                createTile(a.substring(3),-100,-100,(char)(tiles.size()+33),'*');
                if(a.startsWith("tl--"))
                {
                    cur_b = createButton(a,a,a,p1,p2,a);
                    p2 += 32+10;
                    i++;
                    if(i == 1)
                    {
                        tp = cur_b;
                    }
                    else if(i == names.size())
                    {
                        bt = cur_b;
                    }
                }

            }

            int m1 = 40;

            createButton("bUp.png","bUp.png","bUp.png",940,10,"+++");
            createButton("bDown.png","bDown.png","bDown.png",940,700-32-10,"---");

            createButton("bWall.png","bWall.png","bWall.png",860+m1,700,"<>wall");
            createButton("bDoor.png","bDoor.png","bDoor.png",900+m1,700,"<>door");
            createButton("bPass.png","bPass.png","bPass.png",940+m1,700,"<>pass");

            createButton("bSave.png","bSave.png","bSave.png",10,10,"[save]");
            createButton("bLoad.png","bLoad.png","bLoad.png",50,10,"[load]");
            createButton("bNew.png","bNew.png","bNew.png",10,700,"[new]");

            createButton("bBkt.png","bBkt.png","bBkt.png",300,10,"<>bucket");
            createButton("bPen.png","bPen.png","bPen.png",340,10,"<>pen");

            createButton("bPs.png","bPs.png","bPs.png",380,10,"<>Ps");
            createButton("bPs1.png","bPs1.png","bPs1.png",380+40,10,"<>Ps1");

            createButton("mrk.png","mrk.png","mrk.png",170+42,700,"<>mrk");

            curSel = tiles.get(0);
        }


    }

    private static void SET_TILES() throws IOException
    {
        //createTile("dirt.png",0,0,'d','*');
        //reateTile("grass.png",0,0,'g','*');
    }

    /**
     * Where you do things to GLimages to make the game function.
     * Yes I know it's vague but that is what it is.
     * @throws IOException if a file not found exception occurs during GLimage creation.
     */
    private static void UPDATE(double dt) throws IOException
    {
        if(doorInput!=null)
        {
            if(doorInput.getEntry()!=null)
            {
                GLtile d = curSel;
                GLtile q = new GLtile("tl-"+d.tag,xyzDoor[0]*32+50,xyzDoor[1]*32+50,d.sm,d.tp);
                q.spread = curSel.spread;
                q.layer = cL;
                grid[xyzDoor[2]][xyzDoor[1]][xyzDoor[0]] = q;
                grid[xyzDoor[2]][xyzDoor[1]][xyzDoor[0]].command =doorInput.getEntry();
                doorInput = null;
                //Tools.p(curSel.command);
            }
        }
        if(hasName)
        {
            if(udE.getTime()>1000)
            {
                Edges();
                udE.clear();
                udE.start();
            }
            int num = Mouse.getDWheel();
            if(num != 0)
            {

                if(Mouse.getX()<955)
                {
                    for(GLbutton b : buttons)
                    {
                        if(b.tag.startsWith("tl-")&&!b.spec.equals("lie"))
                        {
                            b.y+=num/10;
                        }
                    }
                }
                else
                {
                    for(GLbutton b : buttons) {
                        if(b.tag.startsWith("tl-")&&b.spec.equals("lie"))
                        {
                            b.y+=num/10;
                        }
                    }
                }

            }


            boolean load = false;
            GLbutton bb = buttons.get(0);
            for(GLbutton a: buttons)
            {
                if(a.click)
                {
                    //Tools.p(a.tag);
                    int ss = 50;
                    if(a.tag.startsWith("tl-"))
                    {
                        curSel = findTile(a.tag);
                        if(!a.spec.equals("lie"))
                        {
                            load = true;
                        }
                        bb = a;
                    }
                    if(a.tag.startsWith("-+-:"))
                    {
                        //Tools.p(curSel.sm);
                        if(curSel.tp=='@'&&doorInput==null)
                        {
                            doorInput = createInput("ummok",W/2-90,H/2-32,"aa");
                            doorInput.sel = true;
                            String[] k = a.tag.split(":");
                            int gx = Integer.parseInt(k[1]);
                            int gy = Integer.parseInt(k[2]);
                            xyzDoor[0] = gx;
                            xyzDoor[1] = gy;
                            xyzDoor[2] = cL;
                        }
                        else
                        {
                            String[] k = a.tag.split(":");
                            int gx = Integer.parseInt(k[1]);
                            int gy = Integer.parseInt(k[2]);
                            GLtile d = curSel;
                            GLtile q = new GLtile("tl-"+d.tag,gx*32+50,gy*32+50,d.sm,d.tp);
                            q.spread = curSel.spread;
                            q.layer = cL;
                            grid[cL][gy][gx] = q;
                        }

                    }
                    if(a.tag.startsWith("+++"))
                    {
                        for(GLbutton b : buttons)
                        {
                            if(b.tag.startsWith("tl-"))
                            {
                                b.y-=3;
                            }
                        }
                    }
                    if(a.tag.startsWith("---"))
                    {

                        for(GLbutton b : buttons)
                        {
                            if(b.tag.startsWith("tl-"))
                            {
                                b.y+=3;
                            }
                        }
                    }
                    if(a.tag.startsWith("<>w"))
                    {
                        curSel.tp = '#';
                    }
                    if(a.tag.startsWith("<>d"))
                    {
                        curSel.tp = '@';

                    }
                    if(a.tag.startsWith("<>p"))
                    {
                        curSel.tp = '*';
                    }
                    if(a.tag.startsWith("[save]"))
                    {
                        SAVE();
                    }
                    if(a.tag.startsWith("[load]"))
                    {
                        try{
                            LOAD();
                        } catch (java.io.IOException ignored) {}
                    }
                    if(a.tag.startsWith("[new]"))
                    {
                        int q = 0;
                        int w = 0;
                        int e = 1;
                        for(GLtile[][] gr : grid)
                        {
                            for(GLtile[] b : gr)
                            {
                                for (GLtile ignored : b)
                                {
                                    if(e==1)
                                      {
                                        grid[e-1][q][w] = new GLtile("tl-default.png", w * 32 + 50, q * 32 + 50, (char) 1, (char) 1);
                                    }
                                    else{
                                        grid[e-1][q][w] = new GLtile("invisible.png", w * 32 + 50, q * 32 + 50, (char) 1, (char) 1);
                                    }
                                    w++;
                                }
                                q++;
                                w = 0;
                            }
                            q=0;
                            w=0;
                            e++;
                        }
                    }
                    if(a.tag.startsWith("<>pen"))
                    {
                        tool = "pen";
                    }
                    if(a.tag.startsWith("<>bucket"))
                    {
                        tool = "bucket";
                    }
                    if(a.tag.startsWith("<>Ps"))
                    {
                        curSel.spread = true;
                    }
                    if(a.tag.startsWith("<>Ps1"))
                    {
                        curSel.spread = false;
                    }
                    if(a.tag.startsWith("-l-"))
                    {
                        String l = a.tag.replaceAll("-l-","");
                        cL = Integer.parseInt(l)-1;
                    }
                }
            }
            if(load)
            {
                ArrayList<GLbutton> toRemove = new ArrayList<>();
                for(GLbutton t : buttons)
                {
                    if(t.spec.equals("lie"))
                    {
                        toRemove.add(t);
                    }
                }
                buttons.removeAll(toRemove);
                int ss = 50;
                for(GLtile t : tiles)
                {
                    if(t.pt.startsWith(bb.t_me))
                    {
                        GLbutton bu = createButton("tl-"+t.tag,"tl-"+t.tag,"tl-"+t.tag,980,ss,"tl-"+t.tag);
                        bu.spec = "lie";
                        ss+=32+10;
                    }
                }
            }
        }
    }

    private static void SAVE() throws FileNotFoundException, UnsupportedEncodingException {
        //Tools.p("Saving ...");
        String path = "src\\Assets\\Scenes\\";
        PrintWriter out = new PrintWriter(path+name+".map","UTF-8");
        PrintWriter out2 = new PrintWriter(path+name+".col","UTF-8");
        //Tools.p("Writing header ...");
        int move = 0;
        //out.println();
        //Tools.p("Writing content ...");
        for(GLtile[][] gr : grid)
        {
            out = new PrintWriter(path+name+"_"+move+".map","UTF-8");
            out2 = new PrintWriter(path+name+"_"+move+".col","UTF-8");
            for(GLtile[] a : gr)
            {
                for(GLtile b : a)
                {
                    out.print(""+b.sm);
                    out2.print(""+b.tp);
                }
                out.println();
                out2.println();
            }
            //Tools.p("Saved.");
            //Tools.p("-> "+path+name);
            out.close();
            out2.close();
            move++;
        }

    }

    private static void LOAD() throws IOException {
        String path = "src\\Assets\\Scenes\\";
        Scanner in = new Scanner(new FileReader(path+name+".map"));
        Scanner in2 = new Scanner(new FileReader(path+name+".col"));
        //[ed] end of header object
        //[cv] divisor between char and name of header obj
        //[sp] divisor between grids (this one is just in case a char get out of hand)
        //String header = in.nextLine();
        int move = 0;
        int q = 0;
        int w = 0;
        for(GLtile[][] gr : grid)
        {
            in = new Scanner(new FileReader(path+name+"_"+move+".map"));
            in2 = new Scanner(new FileReader(path+name+"_"+move+".col"));
            for(GLtile[] a : gr)
            {
                String s = in.nextLine();
                String s2 = in2.nextLine();
                char[] sp = s.toCharArray();
                char[] sp2 = s2.toCharArray();
                for(GLtile b : a)
                {
                    GLtile t = findTile(sp[w]);
                    //System.out.println(t);
                    b = new GLtile("tl-"+t.tag,w*32+50,q*32+50,sp[w],sp2[w]);
                    grid[move][q][w] = b;
                    w++;
                }
                w=0;
                q++;
            }
            w=0;
            q=0;
            move++;
            in.close();
            in2.close();
        }
    }

    /**
     * Renders all GLimages in images[] and runs their updates their information.
     */
    private static void RENDER(double dt) throws IOException , CustomUtils.AudioControllerException
    {
        if(hasName)
        {
            //System.out.println(dt);
            for(GLimage image : images) {
                image.render();
            }
            for(GLpic pic : pics) {
                pic.render();
            }
            for(GLbutton button : buttons) {
                button.render();
                button.update(org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), org.lwjgl.input.Mouse.isButtonDown(0), dt);
                if(button.tag.equals("<>mrk"))
                {
                    GLbutton b = findBut("-l-"+cL);
                    button.x = b.x;
                }
            }

            for(GLtile a : tiles)
            {
                a.render();
            }

            for(GLtile[][] gr :grid)
            {
                for(GLtile[] a : gr)
                {
                    for(GLtile l : a)
                    {
                        l.render();
                    }
                }
            }

            for(GLtext aText : text) {
                aText.render();
            }
        }

        for(GLinput button : inputs) {
            button.render();
            button.update(org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), org.lwjgl.input.Mouse.isButtonDown(0), dt);
        }
        //Tools.p(name_this.getEntry());
        if(name_this.getEntry()!=null && !hasName)
        {
            name = name_this.getEntry();
            hasName = true;
            Display.setTitle("Tilemap editor - "+name);
            init();
        }
    }


    private static void Edges() throws IOException
    {
        int x = 0;
        int y = 0;

            for(GLtile[] a : grid[cL])
            {
                for(GLtile b : a)
                {
                    if(!java.util.Objects.equals(b.tag, "def")&& b.spread)
                    {
                        GLtile sub = new GLtile("default.png",-100,-100,(char)1,'*');
                        GLtile up   = sub;
                        GLtile uplf = sub;
                        GLtile dn   = sub;
                        GLtile dnrt = sub;
                        GLtile lf   = sub;
                        GLtile dnlf = sub;
                        GLtile rt   = sub;
                        GLtile uprt = sub;
                        try{ up   = grid[cL][y-1][x];}  catch (Exception ignored){}
                        try{ uplf = grid[cL][y-1][x-1];}catch (Exception ignored){}
                        try{ dn   = grid[cL][y+1][x];}  catch (Exception ignored){}
                        try{ dnrt = grid[cL][y+1][x+1];}catch (Exception ignored){}
                        try{ lf   = grid[cL][y][x-1];}  catch (Exception ignored){}
                        try{ dnlf = grid[cL][y+1][x-1];}catch (Exception ignored){}
                        try{ rt   = grid[cL][y][x+1];}  catch (Exception ignored){}
                        try{ uprt = grid[cL][y-1][x+1];}catch (Exception ignored){}
                        String m = b.me;
                        String u = up.me;
                        String d = dn.me;
                        String l = lf.me;
                        String r = rt.me;
                        String ul = uplf.me;
                        String dr = dnrt.me;
                        String dl = dnlf.me;
                        String ur = uprt.me;
                        boolean ta = m.equals(u);
                        boolean tb = m.equals(d);
                        boolean tc = m.equals(l);
                        boolean td = m.equals(r);
                        boolean te = m.equals(ul);
                        boolean tf = m.equals(dr);
                        boolean tg = m.equals(dl);
                        boolean th = m.equals(ur);
                        boolean fa = !m.equals(u);
                        boolean fb = !m.equals(d);
                        boolean fc = !m.equals(l);
                        boolean fd = !m.equals(r);
                        boolean fe = !m.equals(ul);
                        boolean ff = !m.equals(dr);
                        boolean fg = !m.equals(dl);
                        boolean fh = !m.equals(ur);
                        String c = b.me;
                        String s = "";
                        for(char aa : c.toCharArray())
                        {
                            if(aa<=47||(aa>=58)) {
                                s+=aa;
                            }
                        }
                        c = s;
                        if(fa&&fb&&fc&&fd)
                        {
                            sT(b,"tl-"+c+"1");
                        }
                        else if(fa&&fb&&fc&&td)
                        {
                            sT(b,"tl-"+c+"2");
                        }
                        else if(fa&&tb&&fc&&fd)
                        {
                            sT(b,"tl-"+c+"3");
                        }
                        else if(fa&&fb&&tc&&fd)
                        {
                            sT(b,"tl-"+c+"4");
                        }
                        else if(ta&&fb&&fc&&fd)
                        {
                            sT(b,"tl-"+c+"5");
                        }
                        else if(ta&&fb&&fc&&td&&th)
                        {
                            sT(b,"tl-"+c+"6");
                        }
                        else if(fa&&tb&&fc&&td&&tf)
                        {
                            sT(b,"tl-"+c+"7");
                        }
                        else if(fa&&tb&&tc&&fd&&tg)
                        {
                            sT(b,"tl-"+c+"8");
                        }
                        else if(ta&&fb&&tc&&fd&&te)
                        {
                            sT(b,"tl-"+c+"9");
                        }
                        else if(tb&&tc&&fg&&fa&&fd)
                        {
                            sT(b,"tl-"+c+"21");
                        }
                        else if(ta&&tc&&fe&&fb&&fd)
                        {
                            sT(b,"tl-"+c+"22");
                        }
                        else if(ta&&td&&fh&&fc&&fb)
                        {
                            sT(b,"tl-"+c+"23");
                        }
                        else if(tb&&td&&ff&&fa&&fc)
                        {
                            sT(b,"tl-"+c+"24");
                        }

                        else if(ta&&tb&&fc&&td)
                        {
                            sT(b,"tl-"+c+"14");
                        }
                        else if(fa&&tb&&tc&&td)
                        {
                            sT(b,"tl-"+c+"15");
                        }
                        else if(ta&&tb&&tc&&fd)
                        {
                            sT(b,"tl-"+c+"16");
                        }
                        else if(ta&&fb&&tc&&td)
                        {
                            sT(b,"tl-"+c+"17");
                        }
                        else if(ta&&tb&&fc&&fd)
                        {
                            sT(b,"tl-"+c+"18");
                        }
                        else if(fa&&fb&&tc&&td)
                        {
                            sT(b,"tl-"+c+"19");
                        }
                        else if(ta&&tb&&tc&&td)
                        {
                            sT(b,"tl-"+c+"20");
                        }
                        else if(tb&&tc&&fg)
                        {
                            sT(b,"tl-"+c+"10");
                        }
                        else if(ta&&tc&&fe)
                        {
                            sT(b,"tl-"+c+"11");
                        }
                        else if(ta&&td&&fh)
                        {
                            sT(b,"tl-"+c+"12");
                        }
                        else if(tb&&td&&ff)
                        {
                            sT(b,"tl-"+c+"13");
                        }
                    }
                    x++;
                }
                y++;
                x = 0;
            }
    }

    private static void sT(GLtile a, String img) throws IOException
    {
        String pth = "Assets\\Art\\Tiles\\";
        String ft = img.replace("tl-","")+".png";
        GLtile me = tiles.get(0);
        for(GLtile t : tiles)
        {
            if(t.tag.equals(ft))
            {
                me = t;
            }
        }
        GLtile aa = grid[a.layer][(a.y-50)/32][(a.x-50)/32];
        aa.texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+img+".png"));
        aa.tag = me.tag;
        aa.sm = me.sm;
        aa.me = me.me;
        aa.pt = me.pt;
        aa.tp = curSel.tp;
    }

    /**
     * You casual main(String [] args) function
     * Starts the game ...
     * @param args ... they are args ...
     * @throws IOException if you suck at specifying file paths...
     */
    public static void main(String[] args) throws IOException , CustomUtils.AudioControllerException
    {
        gameTime.start();
        start();
    }

    /**
     * Controls user input.
     * Mouse and Keyboard.
     */
    private static void INPUT() throws IOException , CustomUtils.AudioControllerException
    {

        if (Mouse.isButtonDown(0))
        {
            //Tools.p(""+Mouse.getX()+" : "+(H-Mouse.getY()));
        }
        if (Mouse.isButtonDown(1)) {

        }

    }

}

