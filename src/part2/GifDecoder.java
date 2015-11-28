package part2;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class GifDecoder {
	private int totalColorIndex[][]; 
	private int localColorIndex[][][];
	private ArrayList<Byte>[] list;
	private ArrayList<Integer>[] actualCodeBeforeDecode;
	private DataInputStream data;
	private int dataBlockSetCount;
	
	public GifDecoder(String fileName) throws FileNotFoundException{
		totalColorIndex = new int[256][3];
		localColorIndex = new int[16][256][3];
		File file = new File(fileName);
		FileInputStream is = new FileInputStream(file);
		data = new DataInputStream(is);
		list = new ArrayList[16];	
	}
	
	public void teadTotalFile() throws IOException{
		//��ȡgif�ļ�ͷ
		String gifNoteName = "";
		for(int i = 0;i < 6;i++){
			byte a = data.readByte();
			char c = (char)a;
			gifNoteName += c;
		}
		System.out.println("GIF������" + gifNoteName);
		//��ȡgif���߼���Ļ��ʶ��
		int widthSmall = data.readUnsignedByte();
		int widthBig = data.readUnsignedByte();
		int width =  ((widthBig * 256) + widthSmall);
		System.out.println("�߼���Ļ��ȣ�" + width);
		int heightSmall = data.readUnsignedByte();
		int heightBig = data.readUnsignedByte();
		int height =  ((heightBig * 256) + heightSmall);
		System.out.println("�߼���Ļ�߶ȣ�" + height);
		//��ȡgifȫ����ɫ�б�
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
			totalColorIndex[i][0] = r;
			totalColorIndex[i][1] = g;
			totalColorIndex[i][2] = b;
			//System.out.println("����" + i + ":(" + r + "," + g + "," + b + ")");
		}
		//����ͼ�����ݿ�----------------------------���ֿ����
		boolean flag = true;
		while(flag == true){
			//Step 1������ͷ��ʶ��
			byte read = data.readByte();
			String readString = Integer.toHexString(read);
			//�±߸��ݲ�ͬ��ͷ��ʶ��������Դ�
			if(readString.equals("3b")){
				System.out.println("��ȡ��gif�ļ��ս�����" + readString);
				System.out.println("�������");
				System.out.println("ͼ�����ݼ�������" + dataBlockSetCount);
				for(int i = 0;i < 16;i++){
					System.out.println("��" + i + "����С��" + list[i].size());
				}
				flag = false;
			}else if(readString.equals("21")){
				/*����ȡ�� 0x21 ��ͷ��ʱ�򣬾���Ϊ����չ��*/
				//������չ��-Begin
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
					while(data.readByte() != 0){
						//ѭ��ֱ�������Ľ�β
						//System.out.println("");
					}
				}else if(tag2.equals("01") || tag2.equals("1")){
					System.out.println("��ȡ����չ�飺ͼ���ı�-" + tag2);
					while(data.readByte() != 0){
						//ѭ��ֱ�������Ľ�β
						//System.out.println("");
					}
				}else if(tag2.equals("ff")){
					System.out.println("��ȡ����չ�飺Ӧ�ó���-" + tag2);
					while(data.readByte() != 0){
						//ѭ��ֱ�������Ľ�β
						//System.out.println("");
					}
				}else{
					System.out.println("��ȡ���޷�ʶ�����͵���չ�飺" + tag2);
					while(data.readByte() != 0){
						//ѭ��ֱ�������Ľ�β
						//System.out.println("");
					}
				}

				//������չ��-����
			}else if(readString.equals("2c")){
				/*����ȡ����ͷ��ʶ��Ϊ2Cʱ��Ϊ��ȡ���ļ����ݿ�*/
				System.out.println("��ȡ���ļ����ݿ�-------------------------------");
				//�����ļ����ݿ�-��ʼ
				int xBiasSmall = data.readUnsignedByte();
				int xBiasBig = data.readUnsignedByte();
				int xBias = (xBiasBig * 256 + xBiasSmall);
				System.out.println("X����ƫ������" + xBias);
				int yBiasSmall = data.readUnsignedByte();
				int yBiasBig = data.readUnsignedByte();
				int yBias = (yBiasBig * 256 + yBiasSmall);
				System.out.println("Y����ƫ������" + yBias);
				int imgWidthSmall = data.readUnsignedByte();
				int imgWidthBig = data.readUnsignedByte();
				int imgWidth = (imgWidthBig * 256 + imgWidthSmall);
				System.out.println("ͼ���ȣ�" + imgWidth);
				int imgHeightSmall = data.readUnsignedByte();
				int imgHeightBig = data.readUnsignedByte();
				int imgHeight =  (imgHeightBig * 256 + imgHeightSmall);
				System.out.println("ͼ��߶ȣ�" + imgHeight);
				byte misrpixel = data.readByte();
				System.out.println("m i s r pixelΪ��" + Integer.toBinaryString(misrpixel));
				byte mDataBlock = (byte) ((misrpixel>>7) & 1);
				System.out.println("m i s r pixel�е�mΪ��" + mDataBlock);
				byte iDataBlock = (byte) ((misrpixel>>6) & 1);
				System.out.println("m i s r pixel�е�iΪ��" + iDataBlock);
				byte sDataBlock = (byte) ((misrpixel>>5) & 1);
				System.out.println("m i s r pixel�е�sΪ��" + sDataBlock);
				byte r = (byte) ((misrpixel>>3) & 3);
				System.out.println("m i s r pixel�е�rΪ��" + r);
				byte pixelDataBlock = (byte) ((misrpixel) & 7);
				System.out.println("m i s r pixel�е�pixelΪ��" + pixelDataBlock);
				//��ȡ��ɫ��������Ȼ���ȡ��ɫ�б�
				if(mDataBlock == 1){
					int indexLengthDataBlock = 1 << (pixelDataBlock + 1); 
					localColorIndex[dataBlockSetCount] = new int[indexLengthDataBlock][3];
					for(int index = 0;index < indexLengthDataBlock;index++){
						int red = data.readUnsignedByte();
						int green = data.readUnsignedByte();
						int blue = data.readUnsignedByte();
						localColorIndex[dataBlockSetCount][index][0] = red;
						localColorIndex[dataBlockSetCount][index][1] = green;
						localColorIndex[dataBlockSetCount][index][2] = blue;
						//System.out.println("����" + index + ":(" + red + "," + green + "," + blue + ")");
					}
				}else{
					//���û�оֲ���ɫ����������ɫ����Ϊ0
					localColorIndex[dataBlockSetCount] = new int[0][3];
				}
				//�±߻�����ɫ�б��ͼ������
				int lzwCodeLength = data.readUnsignedByte();
				System.out.println("lzw���볤��Ϊ��" + lzwCodeLength);
				//��ȡһ���������ݿ飬��������ж����ݿ⵽��ĩβ
				boolean dataBlockStatus = true;
				//��ѭ����ÿ��ͼ�����ݽṹ���������ݿ���в���
				int dataBlockCount = 0;
				list[dataBlockSetCount] = new ArrayList<Byte>();
				while(dataBlockStatus == true){
					//��ѭ����ÿ��ͼ�����ݽṹ�����ݿ���в���
					int dataBlockLength = data.readUnsignedByte();
					if(dataBlockLength == 0){
						System.out.println("��ͼ�����ݽṹ�Ķ�ȡ����---------------------------");
						dataBlockStatus = false;
					}else{
						System.out.println("���ݿ��СΪ��" + dataBlockLength + "(" + dataBlockCount + ")");
						for(int index = 0;index < dataBlockLength;index++){
							byte readByte = data.readByte();
							list[dataBlockSetCount].add(readByte);
						}
						dataBlockCount++;
					}
				}
				//�����ļ����ݿ�-����
				dataBlockSetCount++;
			}else{
				System.out.println("��ȡ���ˣ��Ȳ�����չ��Ҳ����LZW���ݿ�:" + readString);
			}
		}
	}
	
	/*����ȡ�������ݷָ�*/
	public void splitCode(){
		actualCodeBeforeDecode = new ArrayList[16];
		String model = "00000000";
		for(int i = 0;i < 16;i++){
			//װ�غõĴ��������
			actualCodeBeforeDecode[i] = new ArrayList<Integer>();
			//��֮ǰ�����ַ���������
			String thisPicData = "";
			int dataLength = list[i].size();
			for(int j = 0;j < dataLength;j++){
				byte byteTemp = list[i].get(j);
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
			//���ͼƬ������ƴ�Ӻ��ˣ���ʼ��ȡ����
			//LogRecord.logRecord(thisPicData, "C:\\Users\\Chao\\Desktop\\out.txt");
			int codeLength = 8 + 1;
			int maxTopBeforeChangeCodeLength = (1 << codeLength) - 1;
			int pointer = thisPicData.length();
			int numOfCodeCount = 0;
			while(pointer > 0){
				pointer -= codeLength;
				String innerString = thisPicData.substring(pointer, pointer + codeLength);
				int inner = Integer.valueOf(innerString,2);
				actualCodeBeforeDecode[i].add(inner);
				numOfCodeCount += 1;
				if(numOfCodeCount == maxTopBeforeChangeCodeLength || codeLength <= 12){
					codeLength++;
					maxTopBeforeChangeCodeLength = (1 << codeLength) - 1;
				}
			}			
		}
	}
	
	
}
