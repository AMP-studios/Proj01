import CustomUtils.AudioControllerException;

import java.io.IOException;

/**
 * Created by Matthew on 3/15/2017.
 */
public class ENpig extends GLenemy
{
    public ENpig(String xy) throws AudioControllerException, IOException
    {
        super(Integer.parseInt(xy.split(",")[0]),Integer.parseInt(xy.split(",")[1]),20,1,500);
        Tools.bp("spawned enemy at "+xy);
        addImage("tl--MutantPig.png");
        setShootStyle("a1:1:!#f4=e");
    }
}
