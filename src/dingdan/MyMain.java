package dingdan;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MyMain {

	public static void main(String[] args) throws IOException{
		//System.out.println("Part_1 : ����������ļ������������ڴ�Project�ļ����£���Ϊ����·������");
		//Scanner sc=new Scanner(System.in);
		//String fileName=sc.next();
		File file=new File("C:\\Users\\Chao\\Desktop\\out.gif");
		GifToBmp gb=new GifToBmp();
		if(file.isFile()){
			gb.transfer(file);
		} else {
			System.out.println("�ļ�����");
			System.exit(0);
		}
	}
}
