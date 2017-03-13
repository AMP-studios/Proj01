import java.awt.image.PixelGrabber;
import java.util.Objects;
import java.util.Random;

public class Tools {
	private static boolean printing = true;

	private static boolean isGreyscaleImage(PixelGrabber pg) {
		return pg.getPixels() instanceof byte[];
	}

	public static void p(Object lp)
	{
		if(printing)
		{
			System.out.println(lp);
		}
	}

	public static void p(Object lp,int color)
	{
			System.out.println("\033["+color+"m"+lp);
			System.out.print("\033[0m");
	}

	public static void bp(Object lp)
	{
			System.out.println(lp);
	}

	public static int random(int top, int bot)
	{
		//Tools.bp("query for random between "+ top +" and "+ bot+".");
		if(bot > top)
		{
			int temp = top;
			top = bot;
			bot = temp;
		}
		//Tools.bp(top-bot);
		return new Random().nextInt(top-bot) + bot;
	}

	public static void enablePrinting()
	{
		printing=true;
	}

	public static void disablePrinting()
	{
		printing=false;
	}
	static String toLet(String a) {
		if(a.matches("[a-zA-Z0-9]"))
		{
			return "Assets\\Font\\"+a.toUpperCase()+".png";
		}
		switch (a) {
			case "`": return "Assets\\Font\\accent.png";
			case "&": return "Assets\\Font\\and.png";
			case "*": return "Assets\\Font\\ast.png";
			case "@": return "Assets\\Font\\at.png";
			case "\\":return "Assets\\Font\\bslash.png";
			case "^": return "Assets\\Font\\carrot.png";
			case ":": return "Assets\\Font\\col.png";
			case ",": return "Assets\\Font\\comma.png";
			case "$": return "Assets\\Font\\doll.png";
			case ".": return "Assets\\Font\\dot.png";
			case "\"":return "Assets\\Font\\dquot.png";
			case "=": return "Assets\\Font\\eq.png";
			case "!": return "Assets\\Font\\exclaim.png";
			case "/": return "Assets\\Font\\fslash.png";
			case ">": return "Assets\\Font\\greater.png";
			case "<": return "Assets\\Font\\less.png";
			case "-": return "Assets\\Font\\minus.png";
			case "#": return "Assets\\Font\\num.png";
			case "%": return "Assets\\Font\\percent.png";
			case "|": return "Assets\\Font\\pipe.png";
			case "+": return "Assets\\Font\\plus.png";
			case "?": return "Assets\\Font\\question.png";
			case "'": return "Assets\\Font\\quot.png";
			case "(": return "Assets\\Font\\quote0.png";
			case ")": return "Assets\\Font\\quote1.png";
			case "[": return "Assets\\Font\\quote2.png";
			case "]": return "Assets\\Font\\quote3.png";
			case "{": return "Assets\\Font\\quote4.png";
			case "}": return "Assets\\Font\\quote5.png";
			case ";": return "Assets\\Font\\semicol.png";
			case " ": return "Assets\\Font\\space.png";
			case "~": return "Assets\\Font\\tilda.png";
			case "_": return "Assets\\Font\\under.png";
			default:  return "Assets\\Font\\err.png";
		}
	}

}
