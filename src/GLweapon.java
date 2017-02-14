import CustomUtils.Time;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.*;
public class GLweapon {
	/**
	 * Pattern rules:
	 * numbers: "1-9" show how many bullets come out followed by a ':' and the damage ending in another ':' 1:10:
	 * symbols: "-" extends the life of the bullet by 0.3 of a second
	 * symbols: "." extends the life of the bullet by 0.1 of a second
	 * symbols: "=" extends the life of the bullet by 1 second
	 * letters: "a" start sequence (followed by num bullets allways)
	 * letters: "e" ends the sequence
	 * letters: "f,b,l,r" add/subtract horizontal or vertical speed
	 * letters: "S" to remove all velocity
	 * patterns: after the a1:10: you specify the pattern ending it with an '#'
	 * patterns: ")" spreads all bullets out
	 * patterns: "!" straight line
	 * patterns: "^" makes a + of bullets  spreading out
	 * patterns: "@" bullets spin around point
	 * patterns: "~" to create a sine wave, comma to add period and amplitude (in pixels) (:~10,1)
		 *
	 * examples: a2:3:!#f10=-.e
	 *	  creates 2 bullets side by side and fires them at a speed of 10 pixels a second for 1.4 seconds
	 * examples: a5:1:)#f3--S=f10==-e
	 */
	boolean paused = false;
	private String pattS = "";
	char[] patt;
	int curP = 0;
	char cur;
	int wait = 0;
	boolean active;
	String step = "init";
	String pattern = "";
	String fcg = "up";
	int amt = 0, dmg = 0;
	int x = 0,y = 0;
	private String pth = "Assets\\Art\\Tiles\\";
	private String exe = "";
	private String cmd = "";
	ArrayList<GLbullet> bullets = new ArrayList<>();


	public GLweapon(String rule)
	{
		pattS = rule;
		curP = 0;
		cmd = pattS.split("#")[0];
		exe = pattS.split("#")[1];
		patt = cmd.toCharArray();
		cur = patt[curP];
	}

	private void upPtt() throws IOException
	{
		if(wait < 1 && !step.equals("EOS"))
		{
			if(step.equals("create"))
			{
				String[] rr = curveSp(1,1,0);
				if(pattern.equals("spread"))
				{
					rr = curveSp(45,amt, 1);
				}
				else if (pattern.equals("radial"))
				{
					rr = curveSp(360,amt,1);
				}
				for(int i = 0; i < amt; i++)
				{
					if(pattern.equals("straight"))
					{
						GLbullet b = new GLbullet(x,y,exe);
						switch(fcg) {
							case "up":
								b.dX=0;
								b.dY=-1;
								break;
							case "down":
								b.dX=0;
								b.dY=1;
								break;
							case "left":
								b.dY=0;
								b.dX=-1;
								break;
							case "right":
								b.dY=0;
								b.dX=+1;
								break;
							default:
								b.dX=0;
								b.dY=0;
								break;
						}
						bullets.add(b);
					}
					if(pattern.equals("spread"))
					{
						for(String aRr : rr) {
							GLbullet b=new GLbullet(x, y,exe);
							String[] q=aRr.split(",");
							b.dX=Double.parseDouble(q[0]);
							b.dY=Double.parseDouble(q[1]);
							bullets.add(b);
						}
					}
					if(pattern.equals("radial"))
					{
						for(String aRr : rr) {
							GLbullet b=new GLbullet(x, y,exe);
							String[] q=aRr.split(",");
							b.dX=Double.parseDouble(q[0]);
							b.dY=Double.parseDouble(q[1]);
							bullets.add(b);
						}
					}
					//System.out.println(bullets+" patt "+pattern + " : " + rr + " : "+a);
				}
				step = "finished";
			}
			if(step.equals("pat"))
			{
				curP++;
				cur = patt[curP];
				switch((cur+"")) {
					case "!":
						pattern="straight";
						break;
					case ")":
						pattern="spread";
						break;
					case "^":
						pattern="radial";
						break;
				}
				step = "create";
			}
			if(step.equals("dmg"))
			{
				curP++;
				cur = patt[curP];
				dmg = Integer.parseInt(cur+"");
				step = "pat";
				out.println("> damage "+dmg);
			}
			if(step.equals("num"))
			{
				try
				{
					int a = (Integer.parseInt(""+cur));
					if(!(patt[curP+1]+"").equals(":"))
					{
						curP++;
						if(patt.length > curP)
						{
							cur = patt[curP];
						}
						else
						{
							step = "EOS";
						}
						int b = (Integer.parseInt(""+cur));
						amt = a*10 + b;
					}
					else
					{
						amt = a;
					}
					step = "dmg";
					out.println("> number "+amt);
				}
				catch (Exception ignored)
				{

				}
			}
			if((""+cur).equals("a"))
			{
				active = true;
				step = "num";
				Tools.p(">init");
			}
			curP++;
			if(patt.length > curP)
			{
				cur = patt[curP];
			}
			else
			{
				step = "EOS";
			}
		}

	}

	private String[] curveSp(int angle, int points, double speed)
	{
		double sub = angle/points;
		double top = angle/2;
		String[] re = new String[points];
		for(int i =0; i < points; i++)
		{
			double[] rs = diff(top,speed);
			String wr = rs[0]+","+rs[1];
			re[i] = wr;
			top -= sub;
		}
		return re;

	}

	public double[] diff(double ang, double speed)
	{

		double dx = Math.sin(ang)*speed;
		double dy = Math.cos(ang)*speed;
		return new double[]{dx,dy};
	}
	public void render() throws IOException
	{
		if(!paused)
		{
			upPtt();
		}
		ArrayList<GLbullet> toRemove =new ArrayList<>();
		for(GLbullet b : bullets)
		{
			b.render();
			if(!paused)
			{
				b.update();
			}
			if(!b.active)
			{
				toRemove.add(b);
			}
		}
		for(GLbullet b : toRemove)
		{
			bullets.remove(b);
		}
	}

}
