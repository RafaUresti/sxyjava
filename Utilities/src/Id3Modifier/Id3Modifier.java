package Id3Modifier;
import java.io.File;
import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;


public class Id3Modifier {
	public static void main(String[] args){
		File sourceFile = new File("D:\\雨夜花，花雨夜.mp3");
		try {
			MP3File mp3 = new MP3File(sourceFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TagException e) {
			e.printStackTrace();
		}
	}
}
