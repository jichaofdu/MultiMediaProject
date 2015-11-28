package part2;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/**
 * �?��无误
 * @author Chao
 *
 */
public class LogRecord {

	public static void logRecord(String content,String path){
		File file = new File(path);
		BufferedWriter fw;
		try {
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
			fw.append(content);
			fw.newLine();
			fw.flush();
			fw.close();
		} catch (IOException e) {
			System.out.println("[Error] 将文件写入到指定位置出错");
		}
	}
}
