import CustomUtils.Time;
import com.sun.corba.se.impl.oa.toa.TOA;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import javax.imageio.ImageIO;
import javax.tools.Tool;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;


public class TileCreator {
    private final static int BLACK = -16777216;
    private final static int CLEAR = 16777215;
    private static final Time gameTime=new Time();
    private static long lastUpdateTime=0;
    private static final int W = 400;
    private static String img1 = "";
    private static String img2 = "";
    private static String finalName = "";
    private static final int H = 300;
    private static GLinput input;
    private static ArrayList<GLbutton> buttons = new ArrayList<>();
    private static GLtile d1;
    private static GLtile d2;
    private static ArrayList<GLbutton> row1 =new ArrayList<>();
    private static ArrayList<GLbutton> row2 =new ArrayList<>();
    public static void start() throws IOException , CustomUtils.AudioControllerException, ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException,InvocationTargetException
    {
        //in = new PrintWriter(path+"tiles.txt","UTF-8");
        gameTime.start();
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

    private static GLbutton createButton(String normal, int x, int y, String tag,ArrayList<GLbutton> toadd) throws IOException
    {
        GLbutton tex = new GLbutton(normal,normal,normal,x,y,tag);
        toadd.add(tex);
        return tex;
    }

    public static void loadAllPng() throws IOException
    {
        String current3 = new java.io.File( "." ).getCanonicalPath();
        File folder3 = new File(current3+"/src/Assets/Art/Tiles");
        File[] listOfFiles3 = folder3.listFiles();
        assert listOfFiles3!=null;
        int x = 50;
        int y = 50;
        for(java.io.File listOfFile : listOfFiles3) {
            if (listOfFile.isFile()&&listOfFile.getName().endsWith(".png")) {

                String pth = "Assets\\Art\\Tiles\\";
                Texture t = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+listOfFile.getName()));
                if(t.getTextureHeight()<33&&t.getTextureWidth()<33)
                {
                    createButton(listOfFile.getName(),x,y,listOfFile.getName(),row1);
                    createButton(listOfFile.getName(),x+100,y,listOfFile.getName(),row2);
                    y+=42;
                }
            }
        }
    }

    public static void init() throws IOException, CustomUtils.AudioControllerException, ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException,InvocationTargetException
    {
        loadAllPng();
        d1 = new GLtile("tl--invis.png",250, 125-32-10,'x','x');
        d1 = new GLtile("tl--invis.png",250+42, 125-32-10,'x','x');
        createButton("bCombine.png",250,125,"[GATTAI]",buttons);
    }

    private static void initGL(int width, int height)
    {
        try
        {
            Display.setDisplayMode(new org.lwjgl.opengl.DisplayMode(width,height));
            //Display.setFullscreen(true);
            //Display.setResizable(true);
            //Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
            Display.setTitle("Tile Creator");
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

    public static void RENDER(double dt) throws IOException
    {
        for(GLbutton b : buttons)
        {
            b.render();
            b.update(org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), org.lwjgl.input.Mouse.isButtonDown(0), dt);
        }
        for(GLbutton b : row1)
        {
            b.render();
            b.update(org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), org.lwjgl.input.Mouse.isButtonDown(0), dt);
        }
        for(GLbutton b : row2)
        {
            b.render();
            b.update(org.lwjgl.input.Mouse.getX(), org.lwjgl.input.Mouse.getY(), org.lwjgl.input.Mouse.isButtonDown(0), dt);
        }
        if(d1!=null){
            d1.render();
        }
        if(d2!=null){
            d2.render();
        }
        if(input!=null)
        {
            input.render();
        }

    }

    public static void checkCreation() throws IOException
    {
        if(input!=null)
        {
            String e = input.getEntry();
            if(e!=null)
            {
                LoadPixel(img1,img2,e);
                input=null;
            }
        }
    }

    public static void UPDATE(double dt) throws IOException
    {
        checkCreation();
        for(GLbutton b : buttons)
        {
            if(b.click)
            {
                Tools.bp(b.tag);
                if(b.tag.startsWith("[GATTAI]"))
                {
                    input = new GLinput("whiteBack.png",W/2-90,H/2-25,"nope");
                    input.sel = true;
                }
            }
        }
        for(GLbutton b : row1)
        {
            if(b.click)
            {
                Tools.bp(b.tag);
                img1 = b.tag;
                d1 = new GLtile(b.tag,250,125-32-10,'x','x');
            }
        }
        for(GLbutton b : row2)
        {
            if(b.click)
            {
                Tools.bp(b.tag);
                img2 = b.tag;
                d2 = new GLtile(b.tag,250+42,125-32-10,'x','x');
            }
        }

    }
    public static void INPUT(double dt)
    {
        if (Mouse.isButtonDown(0))
        {
            Tools.p(""+Mouse.getX()+" : "+(H-Mouse.getY()));
        }
        int num = Mouse.getDWheel();
        if(num != 0)
        {
            if(Mouse.getX()<117)
            {
                for(GLbutton b : row1)
                {
                        b.y+=num/10;
                }
            }
            else
            {
                for(GLbutton b : row2)
                {
                        b.y+=num/10;
                }
            }
        }
    }

    //LoadPixel("build_inline","build_outline", "ironFloor");
    public static void main(String[] foo) throws IOException , CustomUtils.AudioControllerException, ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException,InvocationTargetException
    {
        start();
    }

    public static void flipHorz(int[][] a)
    {
        for(int[] y : a)
        {
            for(int i = 0; i < (((y.length)-(y.length%2))/2);i++)
            {
                int temp = y[i];
                y[i]=y[y.length-1-i];
                y[y.length-1-i]=temp;
            }
        }
    }

    public static void LoadPixel(String outline, String filling, String name) throws IOException
    {
        //filling 32x32
        int[][] fill = toPix(filling);
        fill = rotate(fill);
        fill = rotate(fill);
        fill = rotate(fill);
        //flipHorz(fill);
        //top 2x2
        crop(0,0,2,2, outline, "tmp-top");
        int[][] corner = toPix("tmp-top");
        remBlk(corner);
        //top 2x32
        crop(0,0,2,32, outline, "tmp-line");
        int[][] line = toPix("tmp-line");
        remBlk(line);

        //int[][] add = overAonB(line,fill);
        //THIS SOMEHOW FUCKING WORKS NO CLUE HOW YESSS FUCK MY LIFE THIS TOOK ABOUT 5 HOURS TO COMPLETE

        ////////////////////////////////////////

        int i = 1;

        int[][] a = rotate(line);   // right
        int[][] b = rotate(a);      // up
        int[][] c = rotate(b);      // left
        int[][] d = rotate(c);      // down

        int[][] a1 = rotate(corner); // right
        int[][] b1 = rotate(a);      // up
        int[][] c1 = rotate(b);      // left
        int[][] d1 = rotate(c);      // down

        ///////////////////////////////////////

        /// OK NOW ITS TIME
        //  square 1
        int[][] tm1 = cmb(cl(a),cl(b));
        int[][] tm1_1 = cmb(cl(c),cl(d));
        int[][] tm1_2 = cmb(cl(tm1),cl(tm1_1));
        int[][] f1 = cmb(cl(tm1_2),cl(fill));
        Save("tl-"+name+i,f1);
        Save("tl--"+name+i,f1);
        i++;
        //  u right 2
        int[][] tm2 = cmb(cl(b),cl(a));
        int[][] tm2_1 = cmb(cl(tm2),cl(d));
        int[][] f2 = cmb(cl(tm2_1),cl(fill));
        Save("tl-"+name+i,f2);
        i++;
        //  u down 3
        int[][] tm3 = cmb(cl(a),cl(c));
        int[][] tm3_1 = cmb(cl(tm3),cl(d));
        int[][] f3 = cmb(cl(tm3_1),cl(fill));
        Save("tl-"+name+i,f3);
        i++;
        //  u left 4
        int[][] tm4 = cmb(cl(b),cl(c));
        int[][] tm4_1 = cmb(cl(tm4),cl(d));
        int[][] f4 = cmb(cl(tm4_1),cl(fill));
        Save("tl-"+name+i,f4);
        i++;
        //  u up 5
        int[][] tm5 = cmb(cl(a),cl(c));
        int[][] tm5_1 = cmb(cl(tm5),cl(b));
        int[][] f5 = cmb(cl(tm5_1),cl(fill));
        Save("tl-"+name+i,f5);
        i++;
        //  corner right 6
        int[][] tm6 = cmb(cl(a),cl(b));
        int[][] f6 = cmb(cl(tm6),cl(fill));
        Save("tl-"+name+i,f6);
        i++;
        //  corner down 7
        int[][] tm7 = cmb(cl(a),cl(d));
        int[][] f7 = cmb(cl(tm7),cl(fill));
        Save("tl-"+name+i,f7);
        i++;
        //  corner left 8
        int[][] tm8 = cmb(cl(c),cl(d));
        int[][] f8 = cmb(cl(tm8),cl(fill));
        Save("tl-"+name+i,f8);
        i++;
        //  corner up 9
        int[][] tm9 = cmb(cl(c),cl(b));
        int[][] f9 = cmb(cl(tm9),cl(fill));
        Save("tl-"+name+i,f9);
        i++;
        //  dot right 10
        int[][] f10 = cmb(cl(a1),cl(fill));
        Save("tl-"+name+i,f10);
        i++;
        //  dot bottom 11
        int[][] f11 = cmb(cl(d1),cl(fill));
        Save("tl-"+name+i,f11);
        i++;
        //  dot left 12
        int[][] f12 = cmb(cl(c1),cl(fill));
        Save("tl-"+name+i,f12);
        i++;
        //  dot up 13
        int[][] f13 = cmb(cl(b1),cl(fill));
        Save("tl-"+name+i,f13);
        i++;
        //  wall right 14

        int[][] f14 = cmb(cl(a),cl(fill));
        Save("tl-"+name+i,f14);
        i++;
        //  wall down 15
        int[][] f15 = cmb(cl(d),cl(fill));
        Save("tl-"+name+i,f15);
        i++;
        //  wall left 16
        int[][] f16 = cmb(cl(c),cl(fill));
        Save("tl-"+name+i,f16);
        i++;
        //  wall up 17
        int[][] f17 = cmb(cl(b),cl(fill));
        Save("tl-"+name+i,f17);
        i++;
        //  pipe up 18
        int[][] tm18 = cmb(cl(a),cl(c));
        int[][] f18 = cmb(cl(tm18),cl(fill));
        Save("tl-"+name+i,f18);
        i++;
        //  pipe right 19
        int[][] tm19 = cmb(cl(b),cl(d));
        int[][] f19 = cmb(cl(tm19),cl(fill));
        Save("tl-"+name+i,f19);
        i++;
        //  clear 20
        Save("tl-"+name+i,cl(fill));
        i++;
        //  dot corner right 21
        int[][] tm21 = cmb(cl(f8),cl(f10));
        int[][] f21 = cmb(cl(tm21),cl(fill));
        Save("tl-"+name+i,f21);
        i++;
        //  dot corner  down 22
        int[][] tm22 = cmb(cl(f9),cl(f11));
        int[][] f22 = cmb(cl(tm22),cl(fill));
        Save("tl-"+name+i,f22);
        i++;
        //  dot corner left 23
        int[][] tm23 = cmb(cl(f6),cl(f12));
        int[][] f23 = cmb(cl(tm23),cl(fill));
        Save("tl-"+name+i,f23);
        i++;
        //  dot corner up 24
        int[][] tm24 = cmb(cl(f7),cl(f13));
        int[][] f24 = cmb(cl(tm24),cl(fill));
        Save("tl-"+name+i,f24);
    }

    public static int[][] cl(int[][] a)
    {
        int[][] temp = new int[a.length][a.length];
        for(int y = 0; y < a.length; y++)
        {
            for(int x = 0; x < a.length; x++)
            {
                temp[y][x] = a[y][x];
            }
        }
        return temp;
    }

    public static void Save(String name,int[][] ifo) throws IOException
    {
        Image a = gifa(ifo);
        BufferedImage b = tBF(a);
        ImageIO.write(b, "PNG", new FileOutputStream(System.getProperty("user.dir")+"/src/Assets/Art/Tiles/"+name+".png"));
    }

    public static int[] dTo(int[][] a)
    {
        int[] b = new int[a.length*a.length];
        int i = 0;
        for(int y = 0; y < a.length; y++)
        {
            for (int x = 0; x < a.length; x++)
            {
                b[i]=a[y][x];
                i++;
            }

        }
        return b;
    }

    public static BufferedImage gifa(int[][] pixels){
        BufferedImage newImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        int ctr=0;

        for(int i=0; i<32; i++){
            for(int j=0; j<32; j++){
                newImage.setRGB(i, j, pixels[i][j]);
            }

        }

        //int[] dummy = ImageFcn.getAllPixels(newImage);

        return newImage;

    }

    public static void remBlk(int[][] ifo)
    {
        for(int y = 0; y < ifo.length; y++)
        {
            for(int x = 0; x < ifo.length; x++)
            {
                if(ifo[y][x]==BLACK)
                {
                    ifo[y][x]=CLEAR;
                }
            }
        }
    }

    // overlay a -> b (basically a is on the top)
    public static int[][] cmb(int[][] q, int[][] w)
    {
        int[][] a = q.clone();
        int[][] b = w.clone();
        for(int y = 0; y < a.length; y++)
        {
            for(int x = 0; x < a.length; x++)
            {
                if(a[y][x]!=CLEAR)
                {
                    b[y][x] = a[y][x];
                }
            }
        }
        //Tools.p(w[0][0]+" : "+b[0][0]);
        return b;
    }

    static int[][] rotate(int[][] matrix) {
    int[][] ret = new int[matrix.length][matrix.length];

        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix.length; ++j) {
                ret[i][j] = matrix[matrix.length - j - 1][i];
            }
        }
        return ret;
    }

    public static void resize(int w, int h,String a, String b) throws FileNotFoundException , IOException
    {

        final BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        File img = new File(System.getProperty("user.dir")+"/src/Assets/Art/Tiles/"+a+".png");
        BufferedImage image = ImageIO.read(img);
        Graphics2D g = dst.createGraphics();
        g.drawImage(image, 0, 0, w, h, null);
        g.dispose();
        ImageIO.write(dst, "PNG", new FileOutputStream(System.getProperty("user.dir")+"/src/Assets/Art/Tiles/"+b+".png"));

    }

    public static BufferedImage tBF(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }


    private static int[][] toPix(String a) throws IOException
    {
        File img = new File(System.getProperty("user.dir")+"/src/Assets/Art/Tiles/"+a);
        Tools.p(img.getAbsolutePath());
        BufferedImage image = ImageIO.read(img);
        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }



    public static void crop(int x, int y, int w, int h, String a, String b) throws IOException
    {
        final BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        File img = new File(System.getProperty("user.dir")+"/src/Assets/Art/Tiles/"+a);
        BufferedImage image = ImageIO.read(img);
        BufferedImage iimage = image.getSubimage(x, y, x+w, y+h); //fill in the corners of the desired crop location here
        //BufferedImage copyOfImage = new BufferedImage(iimage.getWidth(), iimage.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage copyOfImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        Graphics g = copyOfImage.createGraphics();
        g.drawImage(iimage, 0, 0, null);
        g.dispose();
        ImageIO.write(copyOfImage, "PNG", new FileOutputStream(System.getProperty("user.dir")+"/src/Assets/Art/Tiles/"+b));


    }
}
