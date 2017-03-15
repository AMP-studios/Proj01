import org.lwjgl.opengl.Display;

import java.io.IOException;

/**
 * Created by bodyi on 11/21/2016.
 *
 */
public class GLtext {

	int x;
	int y;
	int cX;
	int cY;
	public GLimage[] letters = new GLimage[666];
	int curLet = 0;
	int size;
	String tag = "";
	String contents;

	public GLtext(String s, int x, int y, int size) throws IOException
	{
		this.contents = s;
		this.size = size;
		this.x = x;
		this.y = y;
		cX = x;
		cY = y;
		write();
	}
	private void write() throws IOException
	{
		String[] br = contents.split("");
		for(String aBr : br) {
			createLetter(Tools.toLet(""+aBr), cX, cY, this.size);
		}
	}

	public void createLetter(String path, int x, int y,int size) throws IOException
	{
		GLimage tex = new GLimage(path,x,y,true);
		letters[curLet] = tex;
		curLet+=1;
		if(cX>= Display.getWidth()- (letters[0].width+letters[0].enl)*2)
		{
			cX = this.x;
			cY += (letters[0].height+letters[0].enl)+(letters[0].height+letters[0].enl)/10;
		}
		//Tools.p("cl > "+path);
		cX += (letters[0].width+letters[0].enl)+(letters[0].width+letters[0].enl)/10;

	}


	public void render() throws IOException
	{
		cX = x;
		cY = y;
		letters = new GLimage[666];
		curLet = 0;
		write();
		for(GLimage letter : letters) {
			if(letter!=null) {
				letter.render();
			}
		}
	}
}
