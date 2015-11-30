package dingdan;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MyMain {

	public static void main(String[] args) throws IOException{
		//System.out.println("Part_1 : 请输入测试文件夹名（若不在此Project文件夹下，则为绝对路径）：");
		//Scanner sc=new Scanner(System.in);
		//String fileName=sc.next();
		File file=new File("C:\\Users\\Chao\\Desktop\\out.gif");
		GifToBmp gb=new GifToBmp();
		if(file.isFile()){
			gb.transfer(file);
		} else {
			System.out.println("文件错误");
			System.exit(0);
		}
	}
}
