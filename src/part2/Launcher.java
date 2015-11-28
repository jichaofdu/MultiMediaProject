package part2;
import java.io.IOException;

public class Launcher {
	public static void main(String[] args){
		try {
			GifDecoder gifDecoder = new GifDecoder("C:\\Users\\Chao\\Desktop\\out.gif");
			gifDecoder.teadTotalFile();
			gifDecoder.splitCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
