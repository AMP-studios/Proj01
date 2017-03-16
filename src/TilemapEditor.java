import CustomUtils.AudioController;
import CustomUtils.AudioControllerException;
import CustomUtils.Time;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import javax.tools.Tool;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.*;
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

public class TilemapEditor{
    @SuppressWarnings("FieldCanBeLocal")
    private static boolean playingMusic = false;
    private static String curMusic = "";
    private static boolean onMusic = false;
    private static AudioController ac = new AudioController();
    private static boolean toAutoSave = false;
    private static String tool = "pen";
    public static boolean outDoorSel = false;
    public static boolean isFullScreen=false;
    //screen width and height controlling variables.
    private static final int W = 1100;
    private static final int H = 740;

    private static boolean doorCrComp = true;

    private static int cL = 1;

    private static GLinput name_this;

    private static int[] xyzDoor = new int[3];
    private static GLinput doorInput;

    private static boolean hasName = false;

    private static char curDoorChar = (char)9000000;
    private static GLtile curSel;
    private static GLtile[][][] grid = new GLtile[3][640/32][800/32];
    private static ArrayList<GLtile> tiles = new ArrayList<>();
    public static ArrayList<GLtile> enemies = new ArrayList<>();
    public static ArrayList<GLpic> pics = new ArrayList<>();
    public static ArrayList<String> names = new ArrayList<>();
    //will load from paths.dor
    public static ArrayList<String> dWrite = new ArrayList<>();
    public static char tempChar = (char)9000+1; // OVER 9000
    public static int dA = 0;
    private static boolean showCol = false;
    private static boolean tileMode = true;
    private static Time colShowTimer = new Time();
    private static GLtile[][] enemyDraw = new GLtile[640/32][800/32];
    private static String[][] colDraw = new String[640/32][800/32];
    private static ArrayList<String> BGM = new ArrayList<>();
    private static GLtile[][] colWrite = new GLtile[640/32][800/32];
    //currently variables to save player position to be removed later.
    public static int x = 200;
    public static int y = 100;

    private static Time autosave = new Time();

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

    private static Time saveDelay=new Time();

    private static Time dpDelay=new Time();

    private static ArrayList<GLinput> inputs = new ArrayList<>();

    private static ArrayList<String> newTiles = new ArrayList<>();

    public static double dt;

    public static ArrayList<String> existingTiles = new ArrayList<>();
    public static GLtile[][][] tempMap = new GLtile[3][640/32][800/32];
    public static String tempName = "";
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

        SET_TILES();
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
                String path = "src\\Assets\\Art\\Tiles\\";
                PrintWriter in = new PrintWriter(path+"tiles.txt","UTF-8");
                PrintWriter in2 = new PrintWriter(path+"enemies.txt","UTF-8");
                for(String a : existingTiles)
                {
                    in.print(a);
                    in.println();
                }
                for(String a : newTiles)
                {
                    in.print(a);
                    in.println();
                }

                in.close();
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
        if(isExistingT(img))
        {
            use=existingTiles.get(findExistingT(img)).split(",")[1].toCharArray()[0];
        }
        else
        {
            newTiles.add(img+","+use);
            Tools.bp("added new tile: "+img+" as "+use);
        }
        GLtile tex = new GLtile("tl-"+img,x,y,use,type);
        tex.tag = img;
        Tools.p("["+(tiles.size()+1)+"] ld-> "+tex.tag);
        tiles.add(tex);
    }

    private static void createEnemy(String img, int x, int y, char use, String script) throws IOException
    {
        GLtile tex = new GLtile(img,x,y,use,'*');
        tex.temp = script;
        tex.tag = img;
        Tools.p("["+(enemies.size()+1)+"] ld-> "+tex.tag);
        enemies.add(tex);
    }

    /**
     * Creates text with a size of @param size containing the string (s).
     * @param s the text to be  displayed
     * @param x the x position of the first letter.
     * @param y the y position of the first letter.
     * @param size changes the size of text, minimum is 0
     * @throws IOException when improper path is given
     */
    public static GLtext createText(String s, int x, int y, int size) throws IOException
    {
        GLtext tex = new GLtext(s,x,y,size);
        text.add(tex);
        currentText++;
        return tex;
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

    private static GLtile findEnemy(String script)
    {
        for(GLtile a :enemies) {
            if(a.temp.equals(script))
            {
                return a;
            }
        }
        return enemies.get(0);
    }

    /**
     * This is used to load images into the game before the first update loop.
     * Things like backgrounds.
     * @throws FileNotFoundException if a file not found exception occurs during GLimage creation.
     */
    public static void init() throws CustomUtils.AudioControllerException, IOException{

        try {
            if(hasName)
            {
                createImage("mainBack.png",0,0);
                saveDelay.start();
                dpDelay.start();
                colShowTimer.start();
                autosave.start();
            }
            else
            {
                name_this = createInput("whiteBack.png", W/2-90,H/2-25,"hue");
                name_this.sel = true;
            }


        } catch (Exception ignored) {}
        if(hasName)
        {
            loadExisting();
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
                            GLtile t = new GLtile("tl-default.png",w*32+50,q*32+50,(char)10000000,'*');
                            t.tag = "def";
                            t.layer = e;
                            grid[e-1][q][w] =t;
                            String r = "invisible.png";
                            createButton(r,r,r,w*32+50,q*32+50,"-+-:"+w+":"+q);
                        }else{
                            GLtile t = new GLtile("invisible.png",w*32+50,q*32+50,(char)10000000,'*');
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
            String current2 = new java.io.File( "." ).getCanonicalPath();
            File folder2 = new File(current2+"/src");
            File[] listOfFiles2 = folder2.listFiles();
            assert listOfFiles2!=null;
            for(java.io.File listOfFile : listOfFiles2) {
                if (listOfFile.isFile() && listOfFile.getName().startsWith("EN")) {
                    Tools.bp("Loading enemy script: "+listOfFile.getName());
                    createEnemy("enemyDefault.png",-100,-100,(char)(26*237*33*29*3),listOfFile.getName());
                    //marker
                    GLtext snap = createText(listOfFile.getName().substring(0,9),p1+1,p2+1,0);
                    GLbutton v = createButton("WhiteBox.png","WhiteBox.png","WhiteBox.png",p1,p2,listOfFile.getName());
                    v.innocent = false;
                    v.isEnemy = true;
                    snap.tag = listOfFile.getName();
                    p2 += 32+10;

                }
            }

            p1 = 900;
            p2 = 50;

            String current3 = new java.io.File( "." ).getCanonicalPath();
            File folder3 = new File(current3+"/src/Assets/Audio/BGM");
            File[] listOfFiles3 = folder3.listFiles();
            assert listOfFiles3!=null;
            for(java.io.File listOfFile : listOfFiles3) {
                if (listOfFile.isFile()) {
                    Tools.bp("created music : "+listOfFile.getName());
                    BGM.add(listOfFile.getName());
                    GLbutton v = createButton("WhiteBox.png","WhiteBox.png","WhiteBox.png",p1,p2,listOfFile.getName());
                    v.spec2 = "music";
                    GLbutton play = createButton("bSample1.png","bSample1.png","bSample1.png",p1+66,p2,"[play],"+listOfFile.getName());
                    GLbutton stop = createButton("bSample2.png","bSample2.png","bSample2.png",p1+66+11,p2,"[stop],"+listOfFile.getName());
                    play.spec2 = "music";
                    stop.spec2 = "music";
                    v.innocent = false;
                    stop.innocent = false;
                    play.innocent = false;
                    ac.addSound("/src/Assets/Audio/BGM/"+listOfFile.getName(),listOfFile.getName());
                    p2+=13;
                }
            }

            p1 = 900;
            p2 = 50;

            GLbutton cur_b;
            int i = 0;
            for(String a:names)
            {
                createTile(a.substring(3),-100,-100,(char)(tiles.size()+1000),'*');
                if(a.startsWith("tl--"))
                {
                    cur_b = createButton(a,a,a,p1,p2,a);
                    cur_b.isEnemy=false;
                    cur_b.innocent = false;
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

            int m1 = -120;

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

            createButton("bShowHide.png","bShowHide.png","bShowHide.png",380+80,10,"<>hide");
            createButton("bShowCol.png","bShowCol.png","bShowCol.png",500,10,"<>show");

            createButton("bEnem.png","bEnem.png","bEnem.png",540,10,"[E]");
            createButton("bTiles.png","bTiles.png","bTiles.png",580,10,"[T]");

            createButton("bAS.png","bAS.png","bAS.png",580+40,10,">AS");
            createButton("bMS.png","bMS.png","bMS.png",580+80,10,">MS");


            createButton("bSounds.png","bSounds.png","bSounds.png",580+120,10,">Sound");

            curSel = tiles.get(0);

            Tools.bp(newTiles);
        }
    }

    //name,char
    //example:
    //  dor1,^
    private static boolean isExistingT(String name)
    {
        int at = findExistingT(name);
        if(at>0&&existingTiles.get(at).split(",")[0].equals(name))
        {
            return true;
        }
        return false;
    }

    private static int findExistingT(String name)
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
        Scanner in2 = new Scanner(new FileReader(path+"enemies.txt"));
        while(in.hasNextLine())
        {
            existingTiles.add(in.nextLine());
        }
    }

    private static void SET_TILES() throws IOException
    {
        //open doors.txt
        String path = "src\\Assets\\Scenes\\";
        Scanner temp = new Scanner(new FileReader(path+"doors.txt"));
        while(temp.hasNextLine())
        {
            String s = temp.nextLine();
            Tools.bp("existing door: "+s);
            dA++;
        }

    }

    private static GLtile[][][] clnGLtile(GLtile[][][] a)
    {
        GLtile[][][] ret = new GLtile[a.length][a[0].length][a[0][0].length];
        int x = 0;
        int y = 0;
        int z = 0;
        for(GLtile[][] gr :grid)
        {
            for(GLtile[] b : gr)
            {
                for(GLtile l : b)
                {
                    ret[z][y][x] = a[z][y][x];
                    x++;
                }
                y++;
                x=0;
            }
            z++;
            y=0;
        }
        return ret;

    }

    /**
     * Where you do things to GLimages to make the game function.
     * Yes I know it's vague but that is what it is.
     * @throws IOException if a file not found exception occurs during GLimage creation.
     */
    private static void UPDATE(double dt) throws IOException, AudioControllerException
    {
        if(doorInput!=null)
        {
            if(doorInput.getEntry()!=null)
            {
                String entry = doorInput.getEntry();
                //Tools.p(doorInput.getEntry());
                GLtile d = curSel;
                curDoorChar = (char)(1000000+dWrite.size()+dA);
                GLtile q = new GLtile("tl-"+d.tag,xyzDoor[0]*32+50,xyzDoor[1]*32+50,curDoorChar,d.tp);
                //Tools.bp("door initialized with char: "+q.sm+" from "+dA);
                q.spread = curSel.spread;
                q.layer = cL;
                tempChar = d.sm;
                grid[xyzDoor[2]][xyzDoor[1]][xyzDoor[0]] = q;
                grid[xyzDoor[2]][xyzDoor[1]][xyzDoor[0]].command =entry;
                // x,y,z,levelname
                //dWrite.add(""+xyzDoor[0]+","+xyzDoor[1]+","+xyzDoor[2]+","+entry+","+name);
                //Tools.p(dWrite);
                tempMap = clnGLtile(grid);
                tempName = entry;
                LOAD(entry);
                doorInput = null;
                outDoorSel = true;

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
                        if(b.tag.startsWith("tl-")&&!b.spec.equals("lie")&&tileMode)
                        {
                            b.y+=num/10;
                        }
                        if(b.tag.startsWith("EN")&&!tileMode)
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
                    if(a.tag.startsWith("EN"))
                    {
                        curSel = findEnemy(a.tag);
                    }
                    if(a.tag.startsWith("-+-:"))
                    {
                        //Tools.p(curSel.sm);
                        if(curSel.tp=='@'&&doorInput==null&&!outDoorSel&&dpDelay.getTime()>1000)
                        {
                            for(int i = inputs.size()-1; i>=0; i--)
                            {
                                inputs.remove(i);
                            }
                            doorInput = createInput("whiteBack.png", W/2-90,H/2-25,"dipt");
                            doorInput.sel = true;
                            String[] k = a.tag.split(":");
                            int gx = Integer.parseInt(k[1]);
                            int gy = Integer.parseInt(k[2]);
                            xyzDoor[0] = gx;
                            xyzDoor[1] = gy;
                            xyzDoor[2] = cL;
                            dpDelay.clear();
                            dpDelay.start();
                        }
                        else if(!outDoorSel)
                        {
                            if(tileMode)
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
                            else
                            {
                                String[] k = a.tag.split(":");
                                int gx = Integer.parseInt(k[1]);
                                int gy = Integer.parseInt(k[2]);
                                GLtile d = curSel;
                                GLtile q = new GLtile("enemyDefault.png",gx*32+50,gy*32+50,d.sm,d.tp);
                                q.spread = curSel.spread;
                                q.temp = curSel.temp;
                                q.layer = cL;
                                enemyDraw[gy][gx] = q;
                            }

                        }
                        else if(outDoorSel&&dpDelay.getTime()>1000)
                        {
                            String path = "src\\Assets\\Scenes\\";
                            PrintWriter out;
                            PrintWriter out2;
                            int move = 0;
                            int i = 1;
                            String[] k = a.tag.split(":");
                            int gx = Integer.parseInt(k[1]);
                            int gy = Integer.parseInt(k[2]);
                            GLtile d = curSel;
                            GLtile q = new GLtile("tl-"+d.tag,gx*32+50,gy*32+50,curDoorChar,d.tp);
                            String write = ""+
                                    curDoorChar+","+
                                    d.sm+","+
                                    tempChar+","+
                                    gx+","+
                                    xyzDoor[0]+","+
                                    gy+","+
                                    xyzDoor[1]+","+
                                    cL+","+
                                    xyzDoor[2]+","+
                                    tempName+","+
                                    name;
                            dWrite.add(write);
                            q.spread = curSel.spread;
                            q.layer = cL;
                            q.sm=curDoorChar;
                            grid[cL][gy][gx] = q;
                            Tools.bp(grid[cL][gy][gx].sm);
                            Tools.bp("========================================================");
                            for(GLtile[][] gr : grid)
                            {
                                out = new PrintWriter(path+tempName+"_"+move+".map","UTF-8");
                                out2 = new PrintWriter(path+tempName+"_"+move+".col","UTF-8");
                                for(GLtile[] e : gr)
                                {
                                    i = 1;
                                    for(GLtile b : e)
                                    {
                                        out.print(""+b.sm);
                                        Tools.p("wrote "+b.sm+" to "+path+tempName+"_"+move+".map\t\t\t["+i+"/"+e.length+"]");
                                        out2.print(""+b.tp);
                                        Tools.p("wrote "+b.tp+" to "+path+tempName+"_"+move+".col\t\t\t["+i+"/"+e.length+"]");
                                        i++;
                                    }
                                    out.println();
                                    out2.println();
                                }
                                out.close();
                                Tools.p("Finihed writing "+path+tempName+".map");
                                out2.close();
                                Tools.p("Finihed writing "+path+tempName+".col");
                                move++;
                            }
                            Tools.bp("========================================================");
                            grid = tempMap;
                            outDoorSel = false;
                            dpDelay.clear();
                            dpDelay.start();
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
                    if(a.tag.startsWith(">Sound"))
                    {
                        onMusic = true;
                    }
                    if(a.tag.startsWith("[play]")&&!playingMusic)
                    {
                        ac.playMusic(a.tag.split(",")[1],1,true);
                        playingMusic = true;
                    }
                    if(a.tag.startsWith("[stop]")&&playingMusic)
                    {
                        ac.stopSound(a.tag.split(",")[1]);
                        playingMusic = false;
                    }
                    if(a.tag.startsWith("<>show"))
                    {
                        collSquash();
                        setColDraw();
                        showCol = true;
                    }
                    if(a.tag.startsWith("<>hide"))
                    {
                        showCol = false;
                    }
                    if(a.tag.startsWith("<>p"))
                    {
                        curSel.tp = '*';
                    }
                    if(a.tag.startsWith("[T]"))
                    {
                        tileMode = true;
                        onMusic = false;
                    }
                    if(a.tag.startsWith("[E]"))
                    {
                        tileMode = false;
                        onMusic = false;
                    }
                    if(a.tag.startsWith(">AS"))
                    {
                        toAutoSave = true;
                    }
                    if(a.tag.startsWith(">MS"))
                    {
                        toAutoSave = false;
                    }
                    if(a.tag.startsWith("[save]"))
                    {
                        if(saveDelay.getTime()>1000)
                        {
                            Tools.p("=========== SAVE INITIALIZED ===========");
                            SAVE();
                            Tools.p("============ SAVE FINALIZED ============");
                            saveDelay.clear();
                            saveDelay.start();
                        }

                    }
                    if(a.tag.startsWith("[load]"))
                    {
                        try{
                            LOAD(name);
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
                                        grid[e-1][q][w] = new GLtile("tl-default.png", w * 32 + 50, q * 32 + 50, (char) 10000000, '*');
                                    }
                                    else{
                                        grid[e-1][q][w] = new GLtile("invisible.png", w * 32 + 50, q * 32 + 50, (char) 10000000, '*');
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

    private static void addWrite(String file, String s) throws IOException
    {
        ArrayList<String> save = new ArrayList<>();
        Scanner in = new Scanner(new FileReader(file));
        while(in.hasNextLine())
        {
            save.add(in.nextLine());
        }
        PrintWriter out = new PrintWriter(file,"UTF-8");
        for(int i = 0; i < save.size(); i++)
        {
            out.println(save.get(i));
        }
        out.println(s);
    }

    private static void collSquash()
    {
        colDraw = new String[640/32][800/32];
        int x = 0;
        int y = 0;
        for(GLtile[][] gr : grid)
        {
            for(GLtile[] a : gr)
            {
                for(GLtile b : a)
                {
                    if(colDraw[y][x]==null)
                    {
                        colDraw[y][x] = ""+b.tp;
                    }
                    else if(!colDraw[y][x].equals("#"))
                    {
                        colDraw[y][x] = ""+b.tp;
                    }
                    x++;
                }
                y++;
                x = 0;
            }
            y=0;
        }
    }

    private static void setColDraw() throws IOException
    {
        for(int y = 0; y < colDraw.length; y++)
        {
            for(int x = 0; x < colDraw[0].length; x++)
            {
                String cur = colDraw[y][x];
                if(cur.equals("#"))
                {
                    colWrite[y][x]= new GLtile("mCol.png",x*32+50,y*32+50,'1','*');
                }
                else if(cur.equals("@"))
                {
                    colWrite[y][x]= new GLtile("mDor.png",x*32+50,y*32+50,'1','*');
                }
                else if(cur.equals("*"))
                {
                    colWrite[y][x]= new GLtile("mThru.png",x*32+50,y*32+50,'1','*');
                }
                else
                {
                    colWrite[y][x]= new GLtile("tl--invis.png",x*32+50,y*32+50,'1','*');
                }
            }
        }
    }

    private static void SAVE() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        //Tools.p("Saving ...");


        String path = "src\\Assets\\Scenes\\";
        PrintWriter out;
        PrintWriter out2;
        String curWrite = "";

        PrintWriter out4 = new PrintWriter(path+name+".ene","UTF-8");
        PrintWriter out3 = new PrintWriter(path+"doors.txt","UTF-8");

        for(GLtile[] l : enemyDraw)
        {
            for(GLtile e : l)
            {
                if(e==null)
                {

                    out4.print("none,");
                }else{

                    out4.print(e.temp+",");
                }
            }
            out4.println();
        }
        out4.close();

        Tools.p("Doors:");
        Tools.bp(dWrite+"[][]");
        int move = 0;
        for(String add: dWrite)
        {
            out3.println(add);
        }
        out3.close();
        Tools.p("finished writing doors");
        int i = 0;
        for(GLtile[][] gr : grid)
        {
            out = new PrintWriter(path+name+"_"+move+".map","UTF-8");
            out2 = new PrintWriter(path+name+"_"+move+".col","UTF-8");
            for(GLtile[] a : gr)
            {
                i = 1;
                for(GLtile b : a)
                {
                    out.print(""+b.sm);
                    Tools.p("wrote "+b.sm+" to "+path+name+"_"+move+".map\t\t\t["+i+"/"+a.length+"]");
                    out2.print(""+b.tp);
                    Tools.p("wrote "+b.tp+" to "+path+name+"_"+move+".col\t\t\t["+i+"/"+a.length+"]");
                    i++;
                }
                out.println();
                out2.println();
            }
            out.close();
            Tools.p("Finihed writing "+path+name+".map");
            out2.close();
            Tools.p("Finihed writing "+path+name+".col");
            move++;
        }

    }

    private static void LOAD(String name) throws IOException {
        Tools.enablePrinting();
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
                        grid[z][q][w] = new GLtile("tl-default.png",w*32+50,q*32+50,(char)10000000,'*');
                        Tools.p("["+w+":"+q+":"+z+"] loaded base");
                    }
                    else
                    {
                        grid[z][q][w] = new GLtile("invisible.png",w*32+50,q*32+50,(char)10000000,'*');
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

                    try
                    {
                        GLtile t = findTile(sp[w]);
                        b = new GLtile("tl-"+t.tag,w*32+50,q*32+50,sp[w],sp2[w]);
                    }
                    catch (Exception e)
                    {
                        Tools.p(e,31);
                        b = new GLtile("default.png",w*32+50,q*32+50,(char)20000000,'*');
                    }

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


    public static void p(char[] a)
    {
        System.out.print("[");
        for(char c:a)
        {
            System.out.print(c+",");
        }
        System.out.print("]");
        System.out.println();
    }
    /**
     * Renders all GLimages in images[] and runs their updates their information.
     */
    private static void RENDER(double dt) throws IOException , CustomUtils.AudioControllerException
    {
        if(hasName)
        {
            if(autosave.getTime()>15000&&toAutoSave)
            {
                SAVE();
                autosave.clear();
                autosave.start();
            }
            if(showCol&& colShowTimer.getTime()>1000)
            {
                collSquash();
                setColDraw();
                colShowTimer.clear();
                colShowTimer.start();
            }
            //System.out.println(dt);
            for(GLimage image : images) {
                image.render();
            }
            for(GLpic pic : pics) {
                pic.render();
            }
            for(GLbutton button : buttons) {
                if(onMusic) {
                    if (button.spec2.equals("music")) {
                        button.render();
                        button.update(org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), org.lwjgl.input.Mouse.isButtonDown(0), dt);
                    }
                }
                if(tileMode)
                {
                    if(!button.isEnemy&&!button.spec2.equals("music"))
                    {
                        button.render();
                        button.update(org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), org.lwjgl.input.Mouse.isButtonDown(0), dt);
                    }
                }else {
                    if(!tileMode&&button.isEnemy||button.innocent&&!button.spec2.equals("music"))
                    {
                        button.render();
                        button.update(org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), org.lwjgl.input.Mouse.isButtonDown(0), dt);
                    }
                }

                if(button.tag.equals("<>mrk"))
                {
                    GLbutton b = findBut("-l-"+(cL+1));
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
                        if(l!=null)
                        {
                            l.render();
                        }
                    }
                }
            }
            if(!tileMode&&!onMusic)
            {
                for(GLtile[] a : enemyDraw)
                {
                    for(GLtile b : a)
                    {
                        if(b!=null)
                        {
                            b.render();
                        }
                    }
                }
            }
            if(showCol)
            {
               for(GLtile[] a : colWrite)
               {
                   for(GLtile b : a)
                   {
                       b.render();
                   }
               }
            }
            for(GLtext aText : text) {
                if(tileMode)
                {
                    if(!aText.tag.startsWith("EN"))
                    {
                        aText.render();
                    }
                }else if(!tileMode && !onMusic){
                    if(aText.tag.startsWith("EN"))
                    {
                        GLbutton a = findBut(aText.tag);
                        aText.x = a.x+1;
                        aText.y = a.y+1;
                    }
                    aText.render();
                }else if(onMusic && !tileMode)
                {
                    if(aText.tag.endsWith(".wav"))
                    {
                        GLbutton a = findBut(aText.tag);
                        aText.x = a.x+1;
                        aText.y = a.y+1;
                    }
                }

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
                if(b==null)
                {
                    break;
                }
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
        if(!aa.iden)
        {
            aa.tp = curSel.tp;
        }
        aa.iden = true;
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
            Tools.p(""+Mouse.getX()+" : "+(H-Mouse.getY()));
        }
        if (Mouse.isButtonDown(1)) {

        }

    }

}

