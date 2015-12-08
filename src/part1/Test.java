package part1;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {
	public static void main(String[] args){
		File inputFile =  new File("C:\\Users\\Chao\\Desktop\\Lzw_code_procedure2.jpg");
		File outputFile = new File("C:\\Users\\Chao\\Desktop\\Lzw_code_procedure3.jpg");
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			FileOutputStream fos = new FileOutputStream(outputFile);
			LzwDecoder decoder = new LzwDecoder();
			decoder.decodeProcedure(fis, fos);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
