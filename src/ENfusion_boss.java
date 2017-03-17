import CustomUtils.AudioControllerException;
import CustomUtils.Time;

import java.io.IOException;

@SuppressWarnings("unused")
public class ENfusion_boss extends GLenemy{
	private Time timer = new Time();
	public ENfusion_boss(String xy) throws IOException, AudioControllerException {

		super(Integer.parseInt(xy.split(",")[0]),Integer.parseInt(xy.split(",")[1]),40,1,1000);
		Tools.bp("spawned enemy at "+xy);
		addImage("boass1_1.png"); //Temp
		addImage("boass1_0.png");
		setShootStyle("a1:1:!#f4=e");
		timer.start();
	}
	@Override
	protected void act() throws IOException, AudioControllerException {
		if(health<0)
		{
			LibTest.loadMapFromPreload("l2r1");
		}
		super.act();
		if (timer.getTime()>700) {
			LibTest.addEnemy(new ENFusionBossSpawn(String.format("%d,%d", (int) super.x, (int) super.y)));
			timer.clear();
			timer.start();
		}
	}

}
