import CustomUtils.Time;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import sun.security.provider.Sun;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bodyi on 3/16/2017.
 */
public class Sunstrike {
	int delay = 1;
	int x = 0;
	int y = 0;
	int curFrame = 0;
	Time timer = new Time();
	int range = 10;
	int damage = 10;
	boolean finished = false;
	Time frameDelay = new Time();
	Time timer2 = new Time();
	int duration = 300;
	Texture marker;
	boolean strike = false;
	Texture texture;
	int frameSpeed = 10;
	ArrayList<Texture> lazerFrames = new ArrayList<>();
	public Sunstrike(int x, int y, int delay, int range, int damage) throws IOException
	{
		this.damage = damage;
		this.x = x;
		this.y = y;
		this.range = range;
		this.delay = delay;
		timer.start();
		frameDelay.start();
		String pth="Assets\\Art\\Tiles\\";
		marker = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+"dangerMarker1.png"));
	}

	public void addFrame(String name) throws IOException
	{
		String pth="Assets\\Art\\Tiles\\";
		lazerFrames.add(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(pth+name)));
	}

	public double distTo(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt(Math.pow(Math.abs(x1-x2),2)+Math.pow(Math.abs(y1-y2),2));
	}

	public void update()
	{
		if(frameDelay.getTime()>frameSpeed)
		{
			if(lazerFrames.size()>1)
			{
				curFrame++;
				curFrame%=lazerFrames.size();
			}
		}
		Color.white.bind();
		texture = marker;
		texture.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f((int) x-32, (int) y);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f((int) x-32 + texture.getTextureWidth(), (int) y-32);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f((int) x-32 + texture.getTextureWidth(), (int) y-32 + texture.getTextureHeight());
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f((int) x-32, (int) y-32 + texture.getTextureHeight());
		GL11.glEnd();


		if(strike&&timer2.getTime()<duration)
		{
			Color.white.bind();
			Texture texture = lazerFrames.get(curFrame);
			texture.bind();
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f((int) x-390, (int) y-600);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f((int) x-390 + texture.getTextureWidth(), (int) y-600);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f((int) x-390 + texture.getTextureWidth(), (int) y-600 + texture.getTextureHeight());
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f((int) x-390, (int) y-600 + texture.getTextureHeight());
			GL11.glEnd();
		}
		if(strike&&timer2.getTime()>duration)
		{
			finished = true;
		}
		if(!strike&&timer.getTime()>delay)
		{
			if(distTo(x,y,(int)LibTest.PLAYER.x,(int)LibTest.PLAYER.y)<range)
			{
				LibTest.PLAYER.health-=damage;
				LibTest.PLAYER.Hpbar.subtract(damage);
			}
			strike = true;
			timer2.clear();
			timer2.start();
		}
	}
}
