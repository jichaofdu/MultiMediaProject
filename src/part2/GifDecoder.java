package part2;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class GifDecoder {
	private FramePicture[] frames;
	private RgbColor[] totalColorIndex; 
	private ArrayList<Integer>[] actualCodeBeforeDecode;
	private DataInputStream data;
	private int dataBlockSetCount;
	private String savePicPath = "C:\\Users\\Chao\\Desktop\\";
	List<Word> dict;
	
	public GifDecoder(String fileName) throws FileNotFoundException{
		frames = new FramePicture[16];
		totalColorIndex = new RgbColor[256];
		File file = new File(fileName);
		FileInputStream is = new FileInputStream(file);
		data = new DataInputStream(is);
		actualCodeBeforeDecode = new ArrayList[16];
	}
	

	
	public void teadTotalFile() throws IOException{
		String gifNoteName = "";
		for(int i = 0;i < 6;i++){
			byte a = data.readByte();
			char c = (char)a;
			gifNoteName += c;
		}
		System.out.println("GIF������" + gifNoteName);
		int widthSmall = data.readUnsignedByte();
		int widthBig = data.readUnsignedByte();
		int width =  ((widthBig * 256) + widthSmall);
		System.out.println("�߼���Ļ��ȣ�" + width);
		int heightSmall = data.readUnsignedByte();
		int heightBig = data.readUnsignedByte();
		int height =  ((heightBig * 256) + heightSmall);
		System.out.println("�߼���Ļ�߶ȣ�" + height);
		byte mcrspixel = data.readByte();
		String mcrspixelString = Integer.toBinaryString(mcrspixel);
		System.out.println("m cr s pixelΪ��" + mcrspixelString.substring(24, 32));
		byte m = (byte) (mcrspixel>>7 & 1);
		System.out.println("m cr s pixel ��m��bitΪ:" + m);
		byte cr = (byte)((mcrspixel & 112) >> 4);
		System.out.println("m cr s pixel ��cr��bitΪ��" + cr);
		byte s = (byte)((mcrspixel & 8) >> 3);
		System.out.println("m cr s pixel ��s��bitΪ��" + s);
		byte pixel = (byte)(mcrspixel & 7);
		System.out.println("m cr s pixel ��pixel��bitΪ��" + pixel);
		byte backgroundColor = data.readByte();
		System.out.println("����ɫ��ȫ����ɫ�б��е�������" + backgroundColor);
		byte widthHeight = data.readByte();
		System.out.println("��߱ȣ�" + widthHeight);
		//��ʼ��ȡȫ����ɫ�б�
		int indexLength = 1 << (pixel + 1); 
		for(int i = 0;i < indexLength;i++){
			int r = data.readUnsignedByte();
			int g = data.readUnsignedByte();
			int b = data.readUnsignedByte();
			totalColorIndex[i] = new RgbColor(r,g,b);
		}
		//����ͼ�����ݿ�----------------------------���ֿ����
		boolean flag = true;
		while(flag == true){
			//Step 1������ͷ��ʶ��
			byte read = data.readByte();
			String readString = Integer.toHexString(read);
			if(readString.equals("3b")){
				flag = false;
			}else if(readString.equals("21")){
				byte secondTag = data.readByte();
				String tag2 = Integer.toHexString(secondTag);
				if(secondTag != 1){
					tag2 = tag2.substring(tag2.length() - 2, tag2.length());
				}
				if(tag2.equals("f9")){
					System.out.println("��ȡ����չ�飺ͼ�ο���-" + tag2);
					data.skip(6);
				}else if(tag2.equals("fe")){
					System.out.println("��ȡ����չ�飺ע��-" + tag2);
					while(data.readByte() != 0){}
				}else if(tag2.equals("01") || tag2.equals("1")){
					System.out.println("��ȡ����չ�飺ͼ���ı�-" + tag2);
					while(data.readByte() != 0){}
				}else if(tag2.equals("ff")){
					System.out.println("��ȡ����չ�飺Ӧ�ó���-" + tag2);
					while(data.readByte() != 0){}
				}else{
					System.out.println("��ȡ���޷�ʶ�����͵���չ�飺" + tag2);
					while(data.readByte() != 0){}
				}
			}else if(readString.equals("2c")){
				System.out.println("��ȡ���ļ����ݿ�-------------------------------");
				int xBiasSmall = data.readUnsignedByte();
				int xBiasBig = data.readUnsignedByte();
				int xBias = (xBiasBig * 256 + xBiasSmall);
				System.out.println("Xƫ����" + xBias);
				int yBiasSmall = data.readUnsignedByte();
				int yBiasBig = data.readUnsignedByte();
				int yBias = (yBiasBig * 256 + yBiasSmall);
				System.out.println("Yƫ����" + yBias);
				int imgWidthSmall = data.readUnsignedByte();
				int imgWidthBig = data.readUnsignedByte();
				int imgWidth = (imgWidthBig * 256 + imgWidthSmall);
				System.out.println("ͼƬ���" + imgWidth);
				int imgHeightSmall = data.readUnsignedByte();
				int imgHeightBig = data.readUnsignedByte();
				int imgHeight =  (imgHeightBig * 256 + imgHeightSmall);
				System.out.println("ͼƬ�߶�" + imgHeight);
				frames[dataBlockSetCount] = new FramePicture(imgWidth,imgHeight,xBias,yBias);
				byte misrpixel = data.readByte();
				System.out.println("m i s r pixelΪ��" + Integer.toBinaryString(misrpixel));
				byte mDataBlock = (byte) ((misrpixel>>7) & 1);
				System.out.println("M�ǣ�" + mDataBlock);
				frames[dataBlockSetCount].setM(mDataBlock);
				byte iDataBlock = (byte) ((misrpixel>>6) & 1);
				System.out.println("I�ǣ�" + iDataBlock);
				frames[dataBlockSetCount].setI(iDataBlock);
				byte sDataBlock = (byte) ((misrpixel>>5) & 1);
				System.out.println("S�ǣ�" + sDataBlock);
				frames[dataBlockSetCount].setS(sDataBlock);
				byte rDataBlock = (byte) ((misrpixel>>3) & 3);
				System.out.println("R�ǣ�" + rDataBlock);
				frames[dataBlockSetCount].setR(rDataBlock);
				byte pixelDataBlock = (byte) ((misrpixel) & 7);
				System.out.println("pixel�ǣ�" + pixelDataBlock);
				frames[dataBlockSetCount].setPixel(pixelDataBlock);
				int lzwLength = data.readUnsignedByte();
				if(mDataBlock == 1){
					int indexLengthDataBlock = 1 << (pixelDataBlock + 1); 
					for(int index = 0;index < indexLengthDataBlock;index++){
						int red = data.readUnsignedByte();
						int green = data.readUnsignedByte();
						int blue = data.readUnsignedByte();
						frames[dataBlockSetCount].getColorPanel()[index] = new RgbColor(red,green,blue);
					}
				}else{
					frames[dataBlockSetCount].setColorPanel(totalColorIndex);
				}
				boolean dataBlockStatus = true;
				while(dataBlockStatus == true){
					int dataBlockLength = data.readUnsignedByte();
					if(dataBlockLength == 0){
						dataBlockStatus = false;
					}else{
						for(int index = 0;index < dataBlockLength;index++){
							byte readByte = data.readByte();
							frames[dataBlockSetCount].readNewByte(readByte);
						}
					}
				}
				dataBlockSetCount++;
			}else{
				System.out.println("��ȡ���ˣ��Ȳ�����չ��Ҳ����LZW���ݿ�:" + readString);
			}
		}
	}
	
	public void parseGif() throws IOException{
		for(int i = 0;i < 16;i++){
			splitCode(i);
		}
		for(int i = 0;i < 16;i++){
			//��ʼ�������
			int iResetCode = 1 << 8;        		// clear code
	        int iCodeSize = 8 + 1;
	        int iFinishCode = iResetCode + 1;       // ��־��һ��ͼ���������Ľ���
	        int iIndex = iResetCode + 2;            // ��ǰ������
	        dict = new ArrayList<Word>(iIndex);
	        for(int j = 0; j < iIndex; j++) {
	            Word w = new Word();
	            w.codes.add((short) j);
	            dict.add(w);
	        }
           // ������ɫ����
           List<Word> result = new ArrayList<Word>();
           int old = -1;
           int code;
           int length = actualCodeBeforeDecode[i].size();
           for(int j = 0;j < length;j++){
        	   code = actualCodeBeforeDecode[i].get(j);
               if(code == iResetCode){
            	   iCodeSize = 8 + 1;
                   iIndex = iResetCode + 2;
                   continue;
               }else if(code < iIndex){ 
            	   Word w =  dict.get(code);
                   result.add(w);
                   if(old != -1){
                	   Word nw = new Word();
                       nw.codes.addAll(dict.get(old).codes);
                       nw.codes.add(w.codes.get(0));
                       dict.add(iIndex, nw);
                       iIndex++;
                   }
                }else{ // not found.
                	Word w = dict.get(old);
                    Word nw = new Word();
                    nw.codes.addAll(w.codes);
                    nw.codes.add(w.codes.get(0));
                    dict.add(iIndex, nw);
                    result.add(nw);
                    iIndex++;
                }
                old = code;
                if(iIndex >= (1 << iCodeSize)){
                    iCodeSize++;
                    if (iCodeSize > 12) {
                    	iCodeSize = 12;
                   }
                }
           }
  
            // ��������������ɫ        
            RgbColor[] colorPanel = frames[i].getColorPanel();
            int countNum = 0;
            for (Word w : result) {
               for (Short s : w.codes) {
            	   int redFinal = colorPanel[s].getRed();
            	   int greenFinal = colorPanel[s].getGreen();
            	   int blueFinal = colorPanel[s].getBlue();
            	   frames[i].getPicColorList()[0] = new RgbColor(redFinal,greenFinal,blueFinal);
                }
            }

			
			
			
			
			
			
			
			
			
			
		}
		for(int i = 0;i < 16;i++){
			savePicture(i);
		}
	}
	
	/**
	 * 
	 * @param picIndex ��ǰ���ڶ�gif����һ֡���д洢
	 * @param colorListForPic ͼƬ�������ص���ɫ��¼
	 * @throws IOException
	 */
	public void savePicture(int picIndex) throws IOException{
		File f = new File(savePicPath + picIndex + ".bmp");
		DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
		int[] datas = new int[545 * 473];
		RgbColor[] colorListForPic = frames[picIndex].getPicColorList();
		int length = colorListForPic.length;
        for (int i = 0; i < length; i++) {
        	RgbColor tmpC = colorListForPic[i];
            datas[i] = (tmpC.getRed() << 16) | (tmpC.getGreen() << 8) | tmpC.getBlue();
        }                
        BufferedImage img = new BufferedImage(545, 473, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = img.getRaster();
        raster.setDataElements(0,0,545,473,datas);
        ImageIO.write(img, "bmp", output);
	}
	
	
	/*����ȡ�������ݷָ�*/
	public void splitCode(int picIndex){
		String model = "00000000";
		actualCodeBeforeDecode[picIndex] = new ArrayList<Integer>();
		String thisPicData = "";
		ArrayList<Byte> tempByteList = frames[picIndex].getDataList();
		int dataLength = tempByteList.size();
		for(int j = 0;j < dataLength;j++){
			byte byteTemp = tempByteList.get(j);
			String byteStringTemp = Integer.toBinaryString(byteTemp);
			String readyToAdd = null;
			if(byteStringTemp.length() > 8){
				readyToAdd = byteStringTemp.substring(byteStringTemp.length() - 8, byteStringTemp.length());
			}else if(byteStringTemp.length() == 8){
				readyToAdd = byteStringTemp;
			}else{
				readyToAdd = model.substring(0, 8 - byteStringTemp.length()) + byteStringTemp;
			}
			thisPicData = readyToAdd + thisPicData;				
		}
		int codeLength = 8 + 1;
		int maxTopBeforeChangeCodeLength = (1 << codeLength) - 1;
		int pointer = thisPicData.length();
		int numOfCodeCount = 0;
		while(pointer - codeLength > 0){
			pointer -= codeLength;
			String innerString = thisPicData.substring(pointer, pointer + codeLength);
			int inner = Integer.valueOf(innerString,2);
			if(inner != 1 << 8 + 1){
				actualCodeBeforeDecode[picIndex].add(inner);
				numOfCodeCount += 1;
			}else{
				break;
			}
			if(numOfCodeCount == maxTopBeforeChangeCodeLength && codeLength < 12){
				codeLength++;
				maxTopBeforeChangeCodeLength = (1 << codeLength) - 1;
			}
		}			
	}
	

}
