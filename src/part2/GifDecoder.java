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
		//读取gif文件头
		String gifNoteName = "";
		for(int i = 0;i < 6;i++){
			byte a = data.readByte();
			char c = (char)a;
			gifNoteName += c;
		}
		System.out.println("GIF署名：" + gifNoteName);
		//读取gif的逻辑屏幕标识符
		int widthSmall = data.readUnsignedByte();
		int widthBig = data.readUnsignedByte();
		int width =  ((widthBig * 256) + widthSmall);
		System.out.println("逻辑屏幕宽度：" + width);
		int heightSmall = data.readUnsignedByte();
		int heightBig = data.readUnsignedByte();
		int height =  ((heightBig * 256) + heightSmall);
		System.out.println("逻辑屏幕高度：" + height);
		//读取gif全局颜色列表
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
			totalColorIndex[i][0] = r;
			totalColorIndex[i][1] = g;
			totalColorIndex[i][2] = b;
			//System.out.println("索引" + i + ":(" + r + "," + g + "," + b + ")");
		}
		//进入图像数据块----------------------------各种块混杂
		boolean flag = true;
		while(flag == true){
			//Step 1：检验头标识符
			byte read = data.readByte();
			String readString = Integer.toHexString(read);
			//下边根据不同的头标识符来区别对待
			if(readString.equals("3b")){
				System.out.println("读取到gif文件终结器：" + readString);
				System.out.println("程序结束");
				System.out.println("图像数据集数量：" + dataBlockSetCount);
				for(int i = 0;i < 16;i++){
					System.out.println("第" + i + "组块大小：" + list[i].size());
				}
				flag = false;
			}else if(readString.equals("21")){
				/*当读取到 0x21 开头的时候，就认为是扩展块*/
				//操作扩展块-Begin
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
					while(data.readByte() != 0){
						//循环直到这个块的结尾
						//System.out.println("");
					}
				}else if(tag2.equals("01") || tag2.equals("1")){
					System.out.println("读取到扩展块：图形文本-" + tag2);
					while(data.readByte() != 0){
						//循环直到这个块的结尾
						//System.out.println("");
					}
				}else if(tag2.equals("ff")){
					System.out.println("读取到扩展块：应用程序-" + tag2);
					while(data.readByte() != 0){
						//循环直到这个块的结尾
						//System.out.println("");
					}
				}else{
					System.out.println("读取到无法识别类型的扩展块：" + tag2);
					while(data.readByte() != 0){
						//循环直到这个块的结尾
						//System.out.println("");
					}
				}

				//操作扩展块-结束
			}else if(readString.equals("2c")){
				/*当读取到开头标识符为2C时认为读取到文件数据块*/
				System.out.println("读取到文件数据块-------------------------------");
				//操作文件数据块-开始
				int xBiasSmall = data.readUnsignedByte();
				int xBiasBig = data.readUnsignedByte();
				int xBias = (xBiasBig * 256 + xBiasSmall);
				System.out.println("X方向偏移量：" + xBias);
				int yBiasSmall = data.readUnsignedByte();
				int yBiasBig = data.readUnsignedByte();
				int yBias = (yBiasBig * 256 + yBiasSmall);
				System.out.println("Y方向偏移量：" + yBias);
				int imgWidthSmall = data.readUnsignedByte();
				int imgWidthBig = data.readUnsignedByte();
				int imgWidth = (imgWidthBig * 256 + imgWidthSmall);
				System.out.println("图像宽度：" + imgWidth);
				int imgHeightSmall = data.readUnsignedByte();
				int imgHeightBig = data.readUnsignedByte();
				int imgHeight =  (imgHeightBig * 256 + imgHeightSmall);
				System.out.println("图像高度：" + imgHeight);
				byte misrpixel = data.readByte();
				System.out.println("m i s r pixel为：" + Integer.toBinaryString(misrpixel));
				byte mDataBlock = (byte) ((misrpixel>>7) & 1);
				System.out.println("m i s r pixel中的m为：" + mDataBlock);
				byte iDataBlock = (byte) ((misrpixel>>6) & 1);
				System.out.println("m i s r pixel中的i为：" + iDataBlock);
				byte sDataBlock = (byte) ((misrpixel>>5) & 1);
				System.out.println("m i s r pixel中的s为：" + sDataBlock);
				byte r = (byte) ((misrpixel>>3) & 3);
				System.out.println("m i s r pixel中的r为：" + r);
				byte pixelDataBlock = (byte) ((misrpixel) & 7);
				System.out.println("m i s r pixel中的pixel为：" + pixelDataBlock);
				//获取颜色索引长度然后读取颜色列表
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
						//System.out.println("索引" + index + ":(" + red + "," + green + "," + blue + ")");
					}
				}else{
					//如果没有局部颜色就设置其颜色数量为0
					localColorIndex[dataBlockSetCount] = new int[0][3];
				}
				//下边基于颜色列表的图像数据
				int lzwCodeLength = data.readUnsignedByte();
				System.out.println("lzw编码长度为：" + lzwCodeLength);
				//读取一个个的数据块，但是如何判定数据库到达末尾
				boolean dataBlockStatus = true;
				//外循环对每个图像数据结构的总体数据块进行操纵
				int dataBlockCount = 0;
				list[dataBlockSetCount] = new ArrayList<Byte>();
				while(dataBlockStatus == true){
					//里循环对每个图像数据结构的数据块进行操作
					int dataBlockLength = data.readUnsignedByte();
					if(dataBlockLength == 0){
						System.out.println("该图像数据结构的读取结束---------------------------");
						dataBlockStatus = false;
					}else{
						System.out.println("数据块大小为：" + dataBlockLength + "(" + dataBlockCount + ")");
						for(int index = 0;index < dataBlockLength;index++){
							byte readByte = data.readByte();
							list[dataBlockSetCount].add(readByte);
						}
						dataBlockCount++;
					}
				}
				//操作文件数据块-结束
				dataBlockSetCount++;
			}else{
				System.out.println("读取到了：既不是扩展块也不是LZW数据块:" + readString);
			}
		}
	}
	
	/*将读取到的数据分割*/
	public void splitCode(){
		actualCodeBeforeDecode = new ArrayList[16];
		String model = "00000000";
		for(int i = 0;i < 16;i++){
			//装截好的代码的容器
			actualCodeBeforeDecode[i] = new ArrayList<Integer>();
			//将之前所有字符反向连接
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
			//这个图片的内容拼接好了，开始截取数据
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
