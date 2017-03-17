import CustomUtils.AudioControllerException;

import java.io.IOException;

/**
 * Created by bodyi on 3/17/2017.
 */
public class ENcow extends GLenemy
{
	public ENcow(String xy) throws AudioControllerException, IOException
	{
		super(Integer.parseInt(xy.split(",")[0]),Integer.parseInt(xy.split(",")[1]),30,1,3000);
		Tools.bp("spawned enemy at "+xy);
		setShootStyle("a1:5:!#f4=e");
		addImage("tl--cow.png");
	}
}
