package dingdan;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class De {

	private final int MAX_VALUE=(1<<12)-1 ;//�ֵ����size
	private final int MAX_CODE=MAX_VALUE-1 ;//���Ա�ʾ��code���������������һλ��END�������
   
	List<Short> decodeString = new ArrayList<Short>(); //�洢һ���������ֽ�������ַ���
	int codeSize,localHeight,localWidth;
	int[][] colorTable;
	List<Short> pixelSet = new ArrayList<Short>(); //�洢һ���������ֽ�������ַ���
	int[] result;
	int curBits;
	ArrayList<Byte> br=new ArrayList<Byte>();//��һ֡ͼƬ���������ݿ���Ϣ
	int bufCount=0;
	int bytePoint=0,bitPoint=0;
	
	public De(){
		
	}
	
	public void init(int codeSize,int[][] colorTable,int localHeight,int localWidth){
		this.codeSize=codeSize;
		this.colorTable=colorTable;
		this.localHeight=localHeight;
		this.localWidth=localWidth;
		this.pixelSet=new ArrayList<Short>(localHeight*localWidth);
		this.curBits=codeSize+1;
		this.result = new int[localWidth*localHeight];
		pixelSet.clear();
	}
	
	/**
	 * 
	 * @param input
	 * @param toPath
	 * @param fileName
	 * @throws IOException
	 */
	public int toBmp(DataInputStream input,String toPath,String fileName) throws IOException{
		File f=new File(toPath,fileName);
		DataOutputStream output =new DataOutputStream( new BufferedOutputStream(new FileOutputStream(f))); //���������ļ��������
		
		int b =input.read();
		int k=0,i,count;
		while(b!=33 && b!=44 && b!=59){
			//System.out.println(b+"-------���ݿ��С");
			count=b;
			i=0;
			while(i<count){
				br.add(input.readByte());
				i++;
			}
			k++;
			b =input.read();
		}
		
		System.out.println(k+"---------k");
		
		parseGif(output);
		
		output.close();
		return b;
	}
	
	 private void parseGif(DataOutputStream output) throws IOException {
        
       // BitReader br = new BitReader();
        
        if (br.size() > 0) {
            // out("�Ѷ�ȡ���С(br.length)=" + br.size());
             //out("====Data to hex string.");
             //br.testHexData();
             int iResetCode = 1 << codeSize;        // clear code
             int iCodeSize = codeSize + 1;
             int iFinishCode = iResetCode + 1;        // ��־��һ��ͼ���������Ľ���
             int iIndex = iResetCode + 2;                // ��ǰ������
             
             // ��ʼ�������ֵ�
            List<Word> dict = new ArrayList<Word>(iIndex);
            for (int i = 0; i < iIndex; i++) {
                 Word w = new Word();
                 w.codes.add((short) i);
                 dict.add(w);
             }
           // out("�ֵ���:" + iIndex);
             
             // ������ɫ����
            List<Word> result = new ArrayList<Word>();
            int old = -1;
            int code;
            while ((code = readNext(iCodeSize)) != iFinishCode) {
                //out("iCodeSize=" + iCodeSize + ", code=" + code + ", iResetCode=" + iResetCode);
                if (code == iResetCode) {
                    iCodeSize = codeSize + 1;
                     iIndex = iResetCode + 2;
                     continue;
                }
                if (code < iIndex) { 
                     Word w =  dict.get(code);
                    result.add(w);
                     if (old != -1) {
                         Word nw = new Word();
                        nw.codes.addAll(dict.get(old).codes);
                         nw.codes.add(w.codes.get(0));
                        dict.add(iIndex, nw);
                         iIndex++;
                     }
                 } else { // not found.
                     Word w = dict.get(old);
                     Word nw = new Word();
                     nw.codes.addAll(w.codes);
                     nw.codes.add(w.codes.get(0));
                     dict.add(iIndex, nw);
                     result.add(nw);
                     iIndex++;
                }
                 old = code;
                 if (iIndex >= (1 << iCodeSize)) {
                     iCodeSize++;
                     if (iCodeSize > 12) {
                         //out("=iCodeSize=" + iCodeSize);
                        iCodeSize = 12;
                    }
                 }
                 //showDict(dict);
             }
             
             // ��������������ɫ
            //Color[] colorTable = localColors != null ? localColors : colors;
            
             List<Color> colorList = new ArrayList<Color>();
             for (Word w : result) {
                for (Short s : w.codes) {
                    colorList.add(new Color(colorTable[s][0],colorTable[s][1],colorTable[s][2]));

                 }
             }
             //out("��ɫ��:" + count);
             System.out.println(colorList.size()+"----------size");
             System.out.println(localWidth * localHeight+"----------localWidth * localHeight");
             
            
             
             
             
             
             
             
             
             
             int[] datas = new int[localWidth * localHeight];
             Color tmpC = null;
             for (int i = 0; i < colorList.size(); i++) {
                 tmpC = colorList.get(i);
                 datas[i] = (tmpC.r << 16) | (tmpC.g << 8) | tmpC.b;
             }                
            
             
             
             // ����ͼ��
             BufferedImage img = new BufferedImage(localWidth, localHeight, BufferedImage.TYPE_INT_RGB);
             WritableRaster raster = img.getRaster();
            raster.setDataElements(0, 0, localWidth, localHeight, datas);
           
            ImageIO.write(img, "bmp", output);
         }
        System.out.println(br.size()+"----------brsize");
     }
	
	 private short readNext(int iCodeSize){
		 byte temp=br.get(bytePoint);
		 int[] r=new int[iCodeSize];
		 for(int i=0;i<iCodeSize;i++){
			 if(bitPoint<8){
				 r[i]=(temp>>bitPoint) & 1;//�������ģ���r��0-curBitsλ�ֱ�洢code�Ĵӵ�λ���λ��bit
				 bitPoint++;
			 }else{
				 bytePoint++;
				 temp=br.get(bytePoint);
				 r[i]=temp& 1;
				 bitPoint=1;
			 }
		 }
		 short code=0;
		 for(int i=0;i<iCodeSize;i++){
			 if(r[i]==1){
				 code+=1<<i;
			 } 
		 }
		 return code;
	}
		
	
	 private class Color {
         public int r;
         public int g;
         public int b;
        public Color(int r, int g, int b) {
             this.r = r;
            this.g = g;
           this.b = b;
        }
     }
     
     private class Word {
         public List<Short> codes = new ArrayList<Short>(3);
     }
     
     
}
