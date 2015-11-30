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
	int tableSize;//��ʼLZW���λ��
	int hasLocalColor=0;
	String toPath="C:\\Users\\Chao\\Desktop\\";
	
	//LZWDecoder decoder=new LZWDecoder();
	De decoder=new De();
	
	public void transfer(File f) throws IOException{
		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(f))); //���������ļ���
		int b;
		int count=0;
		///////////////////////////////////////////////
		while (count<6 && (b =(byte)input.read()) != -1) {
			System.out.println((char)b+"---------�ļ�ͷ");
			count++;
		}
		///////////////////////////////////////////////////
		count=0;
		b =input.read();
		width=b;
		b = input.read();
		width+=b<<8;
		width=width&0xffff;
		System.out.println(width+"---------�߼���Ļ���");
		
		b =input.read();
		height=b;
		b =input.read();
		height+=b<<8;
		height=height&0xffff;
		System.out.println(height+"---------�߼���Ļ�߶�");
		
		b =input.read();
		m=b>>7 & 1;
		System.out.println(m+"----�Ƿ���ȫ����ɫ��");
		
		cr=b>>4 & 7;
		System.out.println(cr+"----cr");
		
		colorDeep=cr+1;
		System.out.println(colorDeep+"----��ɫ���");
		
		s=b>>3 & 1;
		System.out.println(s+"----s");
		
		pixel=b & 7;
		System.out.println(pixel+"----ȫ����ɫ�б��С");
		global=(int) (3*Math.pow(2,pixel+1));
		System.out.println(global+"----ȫ����ɫ�б���ռ�ֽ�");
		
		
		b =input.read();
		System.out.println(b+"----����ɫ");
		b =input.read();
		System.out.println(b+"----���ؿ�߱�");
		//////////////////////////////////////////////////////////
		getGlobalColorList(input);
		
		/////////////////////////////////////////////////////////////
		b=input.read();
		while(true){
			if (b==33){
				System.out.println("-----������չ��0x21-----");
				dealExpandBlock(input);
				b=input.read();
			} else if (b==44){
				System.out.println(imageNum+++"/////////////////////////");
				System.out.println(b+"------����ͼ���ʶ��0x2c");
				
				readImageIdentifier(input);//��ͼ���ʶ�������оֲ���ɫ��ͬʱ������
				
				b =input.read();
				tableSize=b;
				System.out.println(tableSize+"-------LZW�����ʼ���λ��");
				
				
				if(hasLocalColor==1){
					decoder.init(tableSize,localColor,localHeight,localWidth);
				} else {
					decoder.init(tableSize,globalColor,localHeight,localWidth);
				}
				b=decoder.toBmp(input,toPath,imageNum+".bmp");
			//	System.exit(0);

				System.out.println("////////////////////////////////////");
			} else if(b==59){
				System.out.println(b+"------�����ļ�������0x3b");
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
			//System.out.printf("globalTable:����%d��rgbֵ:(%d,%d,%d)\n",count,R,G,B);
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
			//System.out.printf("localTable:����%d��rgbֵ:(%d,%d,%d)\n",count,R,G,B);
			count++;
		}
	}
	
	public void readImageIdentifier(DataInputStream input) throws IOException{
		int b =input.read();
		localX=b;
		b = input.read();
		localX+=b<<8;
		System.out.println(localX+"---------X����ƫ����");
		
		b =input.read();
		localY=b;
		b = input.read();
		localY+=b<<8;
		System.out.println(localY+"---------Y����ƫ����");
		
		b =input.read();
		localWidth=b;
		b = input.read();
		localWidth+=b<<8;
		//width=width&0xffff;
		System.out.println(localWidth+"---------ͼ����");
		
		b =input.read();
		localHeight=b;
		b =input.read();
		localHeight+=b<<8;
		//height=height&0xffff;
		System.out.println(height+"---------ͼ��߶�");
		
		b =input.read();
		System.out.println(Integer.toBinaryString(b)+"-------");
		hasLocalColor=b>>7 & 1;
		System.out.println(hasLocalColor+"---------�Ƿ��оֲ���ɫ�б�");
		int jiaozhi=b>>6 & 1;
		System.out.println(jiaozhi+"---------��֯��־");
		int fenlei=b>>5 & 1;
		System.out.println(fenlei+"---------�����־");
		int baoliu=b>>3 & 3;
		System.out.println(baoliu+"---------����λ0");
		int jubu=b & 7;
		localSize=(int) Math.pow(2, jubu+1);
		System.out.println(jubu+"---------�ֲ���ɫ�б��С");
		
		if(hasLocalColor==1){
			getLocalColorList(input); 
		}  
	}
	
	public void dealExpandBlock(DataInputStream input) throws IOException{
		int b=input.read();
		if(b==255){
			System.out.println("----------����Ӧ�ó�����չ��0xff-------");
		} else if(b==249){
			System.out.println("----------����ͼ�ο�����չ��0xf9-------");
		} else if(b==254){
			System.out.println("----------����ע����չ��0xfe----------");
		}
		while(b!=0){
			b=input.read();
		}
	}
	
	
	
	
}
