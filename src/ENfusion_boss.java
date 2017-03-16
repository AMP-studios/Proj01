import CustomUtils.AudioControllerException;
import CustomUtils.Time;

import java.io.IOException;

@SuppressWarnings("unused")
public class ENfusion_boss extends GLenemy{
	private Time timer;
	public ENfusion_boss(String xy) throws IOException, AudioControllerException {

		super(Integer.parseInt(xy.split(",")[0]),Integer.parseInt(xy.split(",")[1]),300,2,20);
		Tools.bp("spawned enemy at "+xy);
		addImage("tl--MutantPig.png"); //Temp
		timer.start();
	}

}
