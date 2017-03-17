import CustomUtils.AudioControllerException;
import CustomUtils.Time;

import java.io.IOException;

/**
 * Created by bodyi on 3/17/2017.
 */
public class ENnuclear_boss extends GLenemy{
	Time timer = new Time();
	boolean buffed = false;
	int difficulty = 50;
	Time timer2 = new Time();
	public ENnuclear_boss(String xy) throws IOException, AudioControllerException {

		super(Integer.parseInt(xy.split(",")[0]),Integer.parseInt(xy.split(",")[1]),50,1,1000);
		Tools.bp("spawned enemy at "+xy);
		addImage("tl--MutantPig.png"); //Temp
		timer.start();
		timer2.start();
	}
	@Override
	protected void act() throws IOException, AudioControllerException {
		super.act();
		if(buffed&&timer2.getTime()<1000)
		{
			speed = 3;
			rate = 100;
			health = 300;
		}
		if(buffed&&timer2.getTime()>1000)
		{

			speed = 1;
			rate = 1000;
			difficulty-=5;
			if(difficulty<=0)
			{
				difficulty=3;
			}
			health = difficulty;
			buffed = false;
			timer.clear();
			timer.start();
		}
		if(!buffed&&timer.getTime()>1000)
		{
			timer2.clear();
			timer2.start();
			buffed = true;
		}
	}
}
