package part2;
import java.io.IOException;

public class Launcher {
	public static void main(String[] args){
		try {
			//在此处修改测试文件的路径
			GifDecoder gifDecoder = new GifDecoder("C:\\Users\\Chao\\Desktop\\out.gif");
			gifDecoder.teadTotalFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
