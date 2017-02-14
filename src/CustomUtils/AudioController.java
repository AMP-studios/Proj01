package CustomUtils;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.Random;

/**
 * The Audio Controller is used for managing audio in the game and relies on the LWJGL and Slick-Util packages.
 *
 * It can be used by having individual objects each have their own soundcontroller or by creating a public soundcontroller in whichever class has main.
 * The former use is recommended for sound effects as it allows similar object to have sound effects that are different but have the same names.
 * The latter use is recommended for music as it allows global access.
 *
 * @author Pavel Braginskiy
 * @version 1.0
 * @since 2016-11-19
 *
 *
 * */
public class AudioController {
	private java.util.HashMap<String, org.newdawn.slick.openal.Audio> sounds=new java.util.HashMap<>();

	/**
	 * This method adds audio to the ArrayList sounds, and gives them an alias for easier use, as well as initializing it to not playing.
	 * @param fileLocation The string to a location of the file.
	 * @param name The name of the sound, which will be reference by other methods in the SoundController class.
	 * @throws AudioControllerException Thrown if you try to add a sound with an already existing name.
	 * @throws IOException Thrown if the provided file path is invalid
	 */
	public void addSound(String fileLocation, String name) throws AudioControllerException, IOException
	{
		sounds.put(name,AudioLoader.getAudio("WAV",ResourceLoader.getResourceAsStream(fileLocation)));
	}

	/**
	 * Plays a sound effect. Once it starts playing the sound effect, it forgets about it.
	 * It won't show up as "playing", and it can't be stopped. You cannot loop soundeffects.
	 * A sound effect can be playing multiple times at the same time.
	 * Audio files must be .wav's
	 * @param name the name of the sound to be played, which was assigned by the addSound() method.
	 * @throws AudioControllerException if an invalid name is provided
	 */
	public void playSoundEffect(String name, double volume) throws AudioControllerException
	{
		Audio soundEffect;
		soundEffect=sounds.get(name);
		Random r=new Random();
		soundEffect.playAsSoundEffect(r.nextFloat()/5+0.9f,(float)volume,false);
	}

	/**
	 * Plays music, and remembers about it. It will show up as playing and can be stopped.
	 * Only one instance of a sound can be playing as music at a time.
	 * @param name the name of the sound to be played, which was assigned by the addSound() method.
	 * @param loop true if the music should loop, false if it should only play once.
	 * @throws AudioControllerException if the music is already playing or if an invalid name is provided
	 */
	public void playMusic(String name, double volume, boolean loop) throws AudioControllerException
	{
		Audio soundEffect;
		soundEffect=sounds.get(name);
		if((sounds.get(name)).isPlaying())
		{
			throw new AudioControllerException("That music is already playing.");
		}
		soundEffect.playAsMusic(1,(float)volume,loop);
	}

	/**
	 * Stops a currently playing music sound. Cannot stop sound effects.
	 * @param name The name of the music to stop.
	 * @throws AudioControllerException if athe music is not currently playing as music or an invalid name is provided.
	 */
	public void stopSound(String name) throws AudioControllerException
	{
		Audio soundEffect;
		soundEffect=sounds.get(name);
		if(!(sounds.get(name)).isPlaying())
		{
			throw new AudioControllerException("That music is not currently playing, or you tried to stop a sound effect.");
		}
		soundEffect.stop();
	}

	/**
	 * Checks if a music sound is currently playing.
	 * @param name the name of the music being checked
	 * @return true if it is, false if it is not.
	 * @throws AudioControllerException if an invalid name is passed.
	 */
	public boolean isPlaying(String name) throws AudioControllerException
	{
		return (sounds.get(name)).isPlaying();
	}
}
