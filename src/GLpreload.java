import javax.tools.Tool;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by bodyi on 2/24/2017.
 */
public class GLpreload {


    public String music = "";
    private  String name = "";

    public  double id = Math.random();

    public  ArrayList<GLtile> tiles = new ArrayList<>();

    private  ArrayList<String> names = new ArrayList<>();

    private  GLtile[][][] grid = new GLtile[3][640/32][800/32];

    public ArrayList<String> existingTiles = new ArrayList<>();

    public ArrayList<String> newTiles = new ArrayList<>();

    private PrintWriter in;
    private String[][] colDraw = new String[640/32][800/32];
    private String path = "src\\Assets\\Art\\Tiles\\";

    public ArrayList<Object> enemies = new ArrayList<>();

    private  GLtile findTile(String tag)
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
    private  GLtile findTile(char tag)
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

    private void addEnemy(String name, int x, int y) throws ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException,InvocationTargetException
    {
        Object object = Class.forName(name).getConstructor(String.class).newInstance(""+x+","+y);
        enemies.add(object);
    }

    private void collSquash()
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
                    else if(!colDraw[y][x].equals("@")&&!colDraw[y][x].equals("#"))
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
        addColl();
    }

    private void addColl()
    {
        int y = 0;
        int x = 0;
        for(String[] a : colDraw)
        {
            for(String b : a)
            {
                grid[1][y][x].tp=colDraw[y][x].toCharArray()[0];
                x++;
            }
            y++;
            x=0;

        }
    }

    private void createTile(String img, int x, int y, char use, char type) throws IOException
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
    private boolean isExisting(String name)
    {
        int at = findExisting(name);
        if(at>0&&existingTiles.get(at).split(",")[0].equals(name))
        {
            return true;
        }
        return false;
    }

    private int findExisting(String name)
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

    private void loadExisting() throws IOException , ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException,InvocationTargetException
    {
        Tools.bp(enemies);
        //addEnemy("ENchicken",10,10);
        Tools.bp(enemies);
        String path = "src\\Assets\\Art\\Tiles\\";
        Scanner in = new Scanner(new FileReader(path+"tiles.txt"));
        while(in.hasNextLine())
        {
            existingTiles.add(in.nextLine());
        }
    }
    public String  getName()
    {
        return name;
    }

    public GLtile[][][] getGrid()
    {
        return grid;
    }


    public GLpreload(String name) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException,IllegalAccessException,InvocationTargetException

    {
        //in = new PrintWriter(path+"tiles.txt","UTF-8");
        loadExisting();
        Tools.enablePrinting();
        Tools.p("preloading "+name);
        Tools.disablePrinting();
        this.name = name;
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

        Scanner in4 = new Scanner(new FileReader(path+name+".ene"));

        int y = 0;
        int x = 0;
        while(in4.hasNextLine())
        {
            String cur = in4.nextLine();
            String[] br = cur.split(",");
            for(int i = 0; i < br.length-1; i++)
            {
                if(!br[i].equals("none"))
                {
                    String cl = br[i].substring(0,br[i].indexOf('.'));
                    addEnemy(cl,x*32,y*32);
                }
                x++;
            }
            y++;
            x=0;
        }
        y=0;
        Scanner in2;
        q = 0;
        w = 0;
        z = 0;
        Tools.p("loading tiles from save");
        for(GLtile[][] gr : grid)
        {
            in = new Scanner(new FileReader(path+name+"_"+z+".map"));
            if(z==0)
            {
                music = in.nextLine();
                Tools.bp("cur music "+music);
            }
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
                    if(sp2[0]=='@')
                    {
                        b = new GLtile("tl--invis.png",w*32,q*32,sp[w],sp2[w]);
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
        collSquash();
    }
}
