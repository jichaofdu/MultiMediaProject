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
import javax.imageio.ImageIO;

public class GifDecoder {
	private FramePicture[] frames;//ͼƬһ���ж���֡��ÿһ֡�Ķ���
	private int[] totalColorIndex; //ȫ����ɫ����
	private DataInputStream data;//GIF�ļ�������
	private int dataBlockSetCount;//��ǰ�������˵ڼ���Frame
	private String savePicPath = "C:\\Users\\Chao\\Desktop\\";//��������bmp�ļ��Ĵ洢·��
	private int lzwLength;//LZW����ĳ���
	
	/**
	 * ���ͣ���������Decoder�Ĺ��췽��
	 * @param fileName Ҫ������GIF���ļ���
	 * @throws FileNotFoundException
	 */
	public GifDecoder(String fileName) throws FileNotFoundException{
		frames = new FramePicture[16];
		totalColorIndex = new int[256];
		File file = new File(fileName);
		FileInputStream is = new FileInputStream(file);
		data = new DataInputStream(is);
	}
	
	/**
	 * ���ͣ����������ڶ�ȡ����GIF�ļ��е�����
	 * @throws IOException
	 */
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
			totalColorIndex[i] = (255 << 24) | (r << 16) | (g << 8) | b;
		}
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
				if(mDataBlock == 1){
					int indexLengthDataBlock = 1 << (pixelDataBlock + 1); 
					for(int index = 0;index < indexLengthDataBlock;index++){
						int red = data.readUnsignedByte();
						int green = data.readUnsignedByte();
						int blue = data.readUnsignedByte();
						frames[dataBlockSetCount].getColorPanel()[index] = (255 << 24) | (red << 16) | (green << 8) | blue;
					}
				}else{
					frames[dataBlockSetCount].setColorPanel(totalColorIndex);
				}
				lzwLength = data.readUnsignedByte();
                int bytes = data.readUnsignedByte();
                LzwDecoder dec = new LzwDecoder(lzwLength + 1, bytes, frames[dataBlockSetCount].getColorPanel(),imgWidth,imgHeight, data);
                int[] result = dec.decode();
                saveBmpFile(result,imgWidth,imgHeight);
				dataBlockSetCount++;
			}else{
				System.out.println("��ȡ���ˣ��Ȳ�����չ��Ҳ����LZW���ݿ�:" + readString);
			}
		}
	}
	
	/**
	 * ���ͣ����������ڽ���������BMP�ļ����뵽ָ��·��
	 * @param colorData
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	private void saveBmpFile(int[] colorData,int width,int height) throws IOException{
        File f = new File(savePicPath + dataBlockSetCount + ".bmp");
		DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
		BufferedImage img = new BufferedImage(545, 473, BufferedImage.TYPE_INT_RGB);
	    WritableRaster raster = img.getRaster();
	    raster.setDataElements(0,0,545,473,colorData);
	    ImageIO.write(img, "bmp", output);
	}
	
}
