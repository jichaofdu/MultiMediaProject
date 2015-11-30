package dingdan;
import java.io.*;

public class GifToBmp {
	int imageNum=0;
	int height,width,m,cr,s,pixel,global;
	int  localHeight,localWidth,localX,localY;
	int colorDeep;
	int globalColor[][]=new int[256][3];
	int localColor[][]=new int[256][3];
	int localSize;
	int tableSize;//初始LZW码表位数
	int hasLocalColor=0;
	String toPath="C:\\Users\\Chao\\Desktop\\";
	
	//LZWDecoder decoder=new LZWDecoder();
	De decoder=new De();
	
	public void transfer(File f) throws IOException{
		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(f))); //创建输入文件流
		int b;
		int count=0;
		///////////////////////////////////////////////
		while (count<6 && (b =(byte)input.read()) != -1) {
			System.out.println((char)b+"---------文件头");
			count++;
		}
		///////////////////////////////////////////////////
		count=0;
		b =input.read();
		width=b;
		b = input.read();
		width+=b<<8;
		width=width&0xffff;
		System.out.println(width+"---------逻辑屏幕宽度");
		
		b =input.read();
		height=b;
		b =input.read();
		height+=b<<8;
		height=height&0xffff;
		System.out.println(height+"---------逻辑屏幕高度");
		
		b =input.read();
		m=b>>7 & 1;
		System.out.println(m+"----是否有全局颜色表");
		
		cr=b>>4 & 7;
		System.out.println(cr+"----cr");
		
		colorDeep=cr+1;
		System.out.println(colorDeep+"----颜色深度");
		
		s=b>>3 & 1;
		System.out.println(s+"----s");
		
		pixel=b & 7;
		System.out.println(pixel+"----全局颜色列表大小");
		global=(int) (3*Math.pow(2,pixel+1));
		System.out.println(global+"----全局颜色列表所占字节");
		
		
		b =input.read();
		System.out.println(b+"----背景色");
		b =input.read();
		System.out.println(b+"----像素宽高比");
		//////////////////////////////////////////////////////////
		getGlobalColorList(input);
		
		/////////////////////////////////////////////////////////////
		b=input.read();
		while(true){
			if (b==33){
				System.out.println("-----遇到扩展块0x21-----");
				dealExpandBlock(input);
				b=input.read();
			} else if (b==44){
				System.out.println(imageNum+++"/////////////////////////");
				System.out.println(b+"------遇到图像标识符0x2c");
				
				readImageIdentifier(input);//度图像标识符，若有局部颜色表，同时创建。
				
				b =input.read();
				tableSize=b;
				System.out.println(tableSize+"-------LZW编码初始码表位数");
				
				
				if(hasLocalColor==1){
					decoder.init(tableSize,localColor,localHeight,localWidth);
				} else {
					decoder.init(tableSize,globalColor,localHeight,localWidth);
				}
				b=decoder.toBmp(input,toPath,imageNum+".bmp");
			//	System.exit(0);

				System.out.println("////////////////////////////////////");
			} else if(b==59){
				System.out.println(b+"------遇到文件结束符0x3b");
				break;
			} else {
				b =input.read();
			}
			
		}
		///////////////////////////////////////////
		input.close();
	}
	
	
	public void getGlobalColorList(DataInputStream input) throws IOException{
		int count=0;
		int rgb=(int) Math.pow(2,pixel+1);
		int R,G,B;
		while (m==1 && count<rgb) {
			R =input.read();
			G =input.read();
			B =input.read();
			globalColor[count][0]=R;
			globalColor[count][1]=G;
			globalColor[count][2]=B;
			//System.out.printf("globalTable:索引%d的rgb值:(%d,%d,%d)\n",count,R,G,B);
			count++;
		}
	}
	
	public void getLocalColorList(DataInputStream input) throws IOException{
		int count=0;
//		int rgb=localSize;
//		System.out.println(rgb+"--rgb");
		int R,G,B;
		while (hasLocalColor==1 && count<localSize) {
			R =input.read();
			G =input.read();
			B =input.read();
			localColor[count][0]=R;
			localColor[count][1]=G;
			localColor[count][2]=B;
			//System.out.printf("localTable:索引%d的rgb值:(%d,%d,%d)\n",count,R,G,B);
			count++;
		}
	}
	
	public void readImageIdentifier(DataInputStream input) throws IOException{
		int b =input.read();
		localX=b;
		b = input.read();
		localX+=b<<8;
		System.out.println(localX+"---------X方向偏移量");
		
		b =input.read();
		localY=b;
		b = input.read();
		localY+=b<<8;
		System.out.println(localY+"---------Y方向偏移量");
		
		b =input.read();
		localWidth=b;
		b = input.read();
		localWidth+=b<<8;
		//width=width&0xffff;
		System.out.println(localWidth+"---------图像宽度");
		
		b =input.read();
		localHeight=b;
		b =input.read();
		localHeight+=b<<8;
		//height=height&0xffff;
		System.out.println(height+"---------图像高度");
		
		b =input.read();
		System.out.println(Integer.toBinaryString(b)+"-------");
		hasLocalColor=b>>7 & 1;
		System.out.println(hasLocalColor+"---------是否有局部颜色列表");
		int jiaozhi=b>>6 & 1;
		System.out.println(jiaozhi+"---------交织标志");
		int fenlei=b>>5 & 1;
		System.out.println(fenlei+"---------分类标志");
		int baoliu=b>>3 & 3;
		System.out.println(baoliu+"---------保留位0");
		int jubu=b & 7;
		localSize=(int) Math.pow(2, jubu+1);
		System.out.println(jubu+"---------局部颜色列表大小");
		
		if(hasLocalColor==1){
			getLocalColorList(input); 
		}  
	}
	
	public void dealExpandBlock(DataInputStream input) throws IOException{
		int b=input.read();
		if(b==255){
			System.out.println("----------遇到应用程序扩展块0xff-------");
		} else if(b==249){
			System.out.println("----------遇到图形控制扩展块0xf9-------");
		} else if(b==254){
			System.out.println("----------遇到注释扩展块0xfe----------");
		}
		while(b!=0){
			b=input.read();
		}
	}
	
	
	
	
}
