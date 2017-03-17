import CustomUtils.AudioControllerException;

import java.io.IOException;

/**
 * Created by bodyi on 3/17/2017.
 */
public class ENFusionBossSpawn extends GLenemy{
	public ENFusionBossSpawn(String xy) throws AudioControllerException, IOException
	{
		super(Integer.parseInt(xy.split(",")[0]),Integer.parseInt(xy.split(",")[1]),1,4,700);
		Tools.bp("spawned enemy at "+xy);
		addImage("boss_spawn1.png");
		setShootStyle("a1:1:!#f4=e");
	}
}
