import CustomUtils.AudioControllerException;
import com.sun.corba.se.impl.oa.toa.TOA;

import javax.tools.Tool;
import java.io.IOException;

/**
 * Created by bodyi on 3/14/2017.
 */
public class ENchicken extends GLenemy{
    public ENchicken(String xy) throws AudioControllerException, IOException
    {
        super(Integer.parseInt(xy.split(",")[0]),Integer.parseInt(xy.split(",")[1]),10,1,100);
        Tools.bp("spawned enemy at "+xy);
        addImage("tl--mutantchicken.png");
    }
}
