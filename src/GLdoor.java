/**
 * Created by bodyi on 2/22/2017.
 */
public class GLdoor {
    int x1;
    int y1;
    int z1;
    int x2;
    int y2;
    int z2;
    String to = "";
    String from = "";
    char me = (char)1000001;
    char tex1 = (char)304;
    char tex2 = (char)304;
    public GLdoor(char main, char tex1, char tex2, int x1, int x2, int y1, int y2, int z1, int z2, String to, String from)
    {
        this.me = main;
        this.tex1 = tex1;
        this.tex2 = tex2;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z1 = z1;
        this.z2 = z2;
        this.to = to;
        this.from = from;
    }
    public void p()
    {
        Tools.bp(me+","+tex1+","+tex2+","+x1+","+x2+","+y1+","+y2+","+z1+","+z2+","+to+","+from);
    }
}
