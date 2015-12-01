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
	private FramePicture[] frames;//图片一共有多少帧，每一帧的对象
	private int[] totalColorIndex; //全局颜色索引
	private DataInputStream data;//GIF文件数据流
	private int dataBlockSetCount;//当前解析到了第几个Frame
	private String savePicPath = "C:\\Users\\Chao\\Desktop\\";//解析出的bmp文件的存储路径
	private int lzwLength;//LZW编码的长度
	
	/**
	 * 解释：本方法是Decoder的构造方法
	 * @param fileName 要解析的GIF的文件名
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
	 * 解释：本方法用于读取整个GIF文件中的数据
	 * @throws IOException
	 */
	public void teadTotalFile() throws IOException{
		String gifNoteName = "";
		for(int i = 0;i < 6;i++){
			byte a = data.readByte();
			char c = (char)a;
			gifNoteName += c;
		}
		System.out.println("GIF署名：" + gifNoteName);
		int widthSmall = data.readUnsignedByte();
		int widthBig = data.readUnsignedByte();
		int width =  ((widthBig * 256) + widthSmall);
		System.out.println("逻辑屏幕宽度：" + width);
		int heightSmall = data.readUnsignedByte();
		int heightBig = data.readUnsignedByte();
		int height =  ((heightBig * 256) + heightSmall);
		System.out.println("逻辑屏幕高度：" + height);
		byte mcrspixel = data.readByte();
		String mcrspixelString = Integer.toBinaryString(mcrspixel);
		System.out.println("m cr s pixel为：" + mcrspixelString.substring(24, 32));
		byte m = (byte) (mcrspixel>>7 & 1);
		System.out.println("m cr s pixel 中m的bit为:" + m);
		byte cr = (byte)((mcrspixel & 112) >> 4);
		System.out.println("m cr s pixel 中cr的bit为：" + cr);
		byte s = (byte)((mcrspixel & 8) >> 3);
		System.out.println("m cr s pixel 中s的bit为：" + s);
		byte pixel = (byte)(mcrspixel & 7);
		System.out.println("m cr s pixel 中pixel的bit为：" + pixel);
		byte backgroundColor = data.readByte();
		System.out.println("背景色在全局颜色列表中的索引：" + backgroundColor);
		byte widthHeight = data.readByte();
		System.out.println("宽高比：" + widthHeight);
		//开始读取全局颜色列表：
		int indexLength = 1 << (pixel + 1); 
		for(int i = 0;i < indexLength;i++){
			int r = data.readUnsignedByte();
			int g = data.readUnsignedByte();
			int b = data.readUnsignedByte();
			totalColorIndex[i] = (255 << 24) | (r << 16) | (g << 8) | b;
		}
		boolean flag = true;
		while(flag == true){
			//Step 1：检验头标识符
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
					System.out.println("读取到扩展块：图形控制-" + tag2);
					data.skip(6);
				}else if(tag2.equals("fe")){
					System.out.println("读取到扩展块：注释-" + tag2);
					while(data.readByte() != 0){}
				}else if(tag2.equals("01") || tag2.equals("1")){
					System.out.println("读取到扩展块：图形文本-" + tag2);
					while(data.readByte() != 0){}
				}else if(tag2.equals("ff")){
					System.out.println("读取到扩展块：应用程序-" + tag2);
					while(data.readByte() != 0){}
				}else{
					System.out.println("读取到无法识别类型的扩展块：" + tag2);
					while(data.readByte() != 0){}
				}
			}else if(readString.equals("2c")){
				System.out.println("读取到文件数据块-------------------------------");
				int xBiasSmall = data.readUnsignedByte();
				int xBiasBig = data.readUnsignedByte();
				int xBias = (xBiasBig * 256 + xBiasSmall);
				System.out.println("X偏移量" + xBias);
				int yBiasSmall = data.readUnsignedByte();
				int yBiasBig = data.readUnsignedByte();
				int yBias = (yBiasBig * 256 + yBiasSmall);
				System.out.println("Y偏移量" + yBias);
				int imgWidthSmall = data.readUnsignedByte();
				int imgWidthBig = data.readUnsignedByte();
				int imgWidth = (imgWidthBig * 256 + imgWidthSmall);
				System.out.println("图片宽度" + imgWidth);
				int imgHeightSmall = data.readUnsignedByte();
				int imgHeightBig = data.readUnsignedByte();
				int imgHeight =  (imgHeightBig * 256 + imgHeightSmall);
				System.out.println("图片高度" + imgHeight);
				frames[dataBlockSetCount] = new FramePicture(imgWidth,imgHeight,xBias,yBias);
				byte misrpixel = data.readByte();
				System.out.println("m i s r pixel为：" + Integer.toBinaryString(misrpixel));
				byte mDataBlock = (byte) ((misrpixel>>7) & 1);
				System.out.println("M是：" + mDataBlock);
				frames[dataBlockSetCount].setM(mDataBlock);
				byte iDataBlock = (byte) ((misrpixel>>6) & 1);
				System.out.println("I是：" + iDataBlock);
				frames[dataBlockSetCount].setI(iDataBlock);
				byte sDataBlock = (byte) ((misrpixel>>5) & 1);
				System.out.println("S是：" + sDataBlock);
				frames[dataBlockSetCount].setS(sDataBlock);
				byte rDataBlock = (byte) ((misrpixel>>3) & 3);
				System.out.println("R是：" + rDataBlock);
				frames[dataBlockSetCount].setR(rDataBlock);
				byte pixelDataBlock = (byte) ((misrpixel) & 7);
				System.out.println("pixel是：" + pixelDataBlock);
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
				System.out.println("读取到了：既不是扩展块也不是LZW数据块:" + readString);
			}
		}
	}
	
	/**
	 * 解释：本方法用于将解析出的BMP文件存入到指定路径
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
