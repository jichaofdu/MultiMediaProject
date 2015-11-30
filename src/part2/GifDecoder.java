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
import javax.imageio.ImageIO;

public class GifDecoder {
	private FramePicture[] frames;
	private RgbColor[] totalColorIndex; 
	private ArrayList<Integer>[] actualCodeBeforeDecode;
	private DataInputStream data;
	private int dataBlockSetCount;
	private String savePicPath = "C:\\Users\\Chao\\Desktop\\";
	private int lzwLength;
	ArrayList<Word> dictionary;
	
	public GifDecoder(String fileName) throws FileNotFoundException{
		frames = new FramePicture[16];
		totalColorIndex = new RgbColor[256];
		File file = new File(fileName);
		FileInputStream is = new FileInputStream(file);
		data = new DataInputStream(is);
		actualCodeBeforeDecode = new ArrayList[16];
		dictionary = new ArrayList<Word>();
	}
	

	
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
			totalColorIndex[i] = new RgbColor(r,g,b);
		}
		//进入图像数据块----------------------------各种块混杂
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
						frames[dataBlockSetCount].getColorPanel()[index] = new RgbColor(red,green,blue);
					}
				}else{
					frames[dataBlockSetCount].setColorPanel(totalColorIndex);
				}
				lzwLength = data.readUnsignedByte();
				boolean dataBlockStatus = true;
				while(dataBlockStatus == true){
					int dataBlockLength = data.readUnsignedByte();
					if(dataBlockLength == 0){
						dataBlockStatus = false;
					}else{
						byte a = data.readByte();
						for(int index = 0;index < dataBlockLength-1;index++){
							byte readByte = data.readByte();
							frames[dataBlockSetCount].readNewByte(readByte);
						}
					}
				}
				dataBlockSetCount++;
			}else{
				System.out.println("读取到了：既不是扩展块也不是LZW数据块:" + readString);
			}
		}
	}
	
	public void parseGif() throws IOException{
		for(int i = 0;i < 16;i++){
			splitCode(i);
		}
		for(int i = 0;i < 16;i++){
			//初始化解码表
			for(int iDict = 0;iDict < (1 << lzwLength) + 2;iDict++){
				Word newWord = new Word();
				newWord.wordElement.add(i);
				dictionary.add(newWord);
			}
			// 解码颜色索引
			ArrayList<Word> resultWordSet = new ArrayList<Word>();
			ArrayList<Integer> localCodeBeforeDecode = actualCodeBeforeDecode[i];
			int readyDecodeLength = localCodeBeforeDecode.size();
			int oldCode = -1;
			int nowCode = -1;
			int CHAR = -1;
			//Input first code, store in OCODE
			int firstCode = localCodeBeforeDecode.get(0);
			oldCode = firstCode;
			//Output translation of oldCode
			Word firstWord = dictionary.get(oldCode);
			resultWordSet.add(firstWord);
			
			for(int index = 1;index < readyDecodeLength;index++){
				Word string = null;
				//Input next code, store in NCODE
				int code = localCodeBeforeDecode.get(i);
				nowCode = code;
				//Is NCODE in table?
				int dicLength = dictionary.size();
				if(nowCode < dicLength){
					//Yes
					//STRING = translation of NCODE/
					string = dictionary.get(nowCode);
					
				}else{
					//No
					//STRING = translation of OCODE
					string = dictionary.get(oldCode);
					//STRING = STRING + CHAR
					string.wordElement.add(CHAR);
				}
				//Output STRING
				resultWordSet.add(string);
				//CHAR = the first character in STRING
				CHAR = string.wordElement.get(0);
				//Add entry in table for OCODE + CHAR
				Word newEntryWord = new Word();
				Word previousSection = dictionary.get(oldCode);
				for(int createEntry = 0;createEntry < previousSection.wordElement.size();createEntry++){
					newEntryWord.wordElement.add(previousSection.wordElement.get(createEntry));
				}
				newEntryWord.wordElement.add(CHAR);
				dictionary.add(newEntryWord);
				//OCODE = NCODE
				oldCode = nowCode;
				//more codes to input?
					//Check in the while loop.
			}
			
			
            // 从索引解析出颜色      
			int resultLength = resultWordSet.size();
			int xiangsuCount = 0;
			RgbColor[] thisPicColorPane = frames[i].getColorPanel();
            for (int resultIndex = 0;resultIndex < resultLength;resultIndex++) {
                Word resultWord = resultWordSet.get(resultIndex);
            	int wordLength = resultWord.wordElement.size(); 
            	for (int intIndex = 0;intIndex < wordLength;intIndex++){
            		RgbColor innerColor = thisPicColorPane[intIndex];
            		int red = innerColor.getRed();
            		int green = innerColor.getGreen();
            		int blue = innerColor.getBlue();
            		frames[i].setPointColor(xiangsuCount, red, green, blue);
                    xiangsuCount++;
                 }
             }

			
		}
		for(int i = 0;i < 16;i++){
			savePicture(i);
		}
	}
	
	/**
	 * 
	 * @param picIndex 当前正在对gif的哪一帧进行存储
	 * @param colorListForPic 图片所有像素的颜色记录
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
	
	
	/*将读取到的数据分割*/
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

			actualCodeBeforeDecode[picIndex].add(inner);
			numOfCodeCount += 1;
			
			if(numOfCodeCount == maxTopBeforeChangeCodeLength && codeLength < 12){
				codeLength++;
				maxTopBeforeChangeCodeLength = (1 << codeLength) - 1;
			}
		}			
	}
	

}
