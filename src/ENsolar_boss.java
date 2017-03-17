import CustomUtils.AudioControllerException;
import CustomUtils.Time;
import org.lwjgl.opengl.Display;
import sun.security.provider.Sun;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bodyi on 3/16/2017.
 */
public class ENsolar_boss extends GLenemy{
	private Time timer = new Time();

	private Time timer2 = new Time();
	private int movSpdSave = 0;
	private Time timer3 = new Time();
	private boolean striking = false;
	private int volleyTime= 5000;
	private int volleyDelay = 500;
	private int volleyRestTime = 10000;
	private Time dropDelay = new Time();
	private ArrayList<Sunstrike> strikes = new ArrayList<>();
	public ENsolar_boss(String xy) throws IOException, AudioControllerException {

		super(Integer.parseInt(xy.split(",")[0]),Integer.parseInt(xy.split(",")[1]),30,2,200);
		Tools.bp("spawned enemy at "+xy);
		addImage("tl--MutantPig.png"); //Temp
		timer.start();
		timer2.start();
		timer3.start();
	}
	@Override
	protected void act() throws IOException, AudioControllerException {
		super.act();
		ArrayList<Sunstrike> trash = new ArrayList<>();
		for(Sunstrike s : strikes)
		{
			s.update();
			if(s.finished)
			{
				trash.add(s);
			}
		}
		for(Sunstrike s : trash)
		{
			strikes.remove(s);
		}
		if(striking)
		{
			speed=0;
		}else {
			speed=movSpdSave;
		}
		if(striking&&timer2.getTime()<volleyTime)
		{
			if(timer3.getTime()>volleyDelay)
			{
				int x1 = (int)LibTest.PLAYER.x;
				int y1 = (int)LibTest.PLAYER.y;
				int x2 = LibTest.rRn(x1,x1);
				x=x2-16;
				int y2 = LibTest.rRn(y1,y1);
				y=y2-16;
				Sunstrike s = new Sunstrike(x2,y2,1000, 100,LibTest.rRn(10,10));
				s.addFrame("SolarBeam1.png");
				s.addFrame("SolarBeam2.png");
				s.addFrame("SolarBeam3.png");
				strikes.add(s);
				timer3.clear();
				timer3.start();
			}
		}
		if(striking&&timer2.getTime()>volleyTime)
		{
			striking = false;
			timer.clear();
			timer.start();
			timer2.clear();
			timer2.stop();
		}
		if (!striking&&timer.getTime()>volleyRestTime) {
			striking = true;
			movSpdSave = (int)speed;
			timer2.start();
		}
	}
}
