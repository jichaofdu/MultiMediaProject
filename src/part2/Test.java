package part2;

import java.util.ArrayList;

public class Test {
	public static void main(String[] args){
		byte a = 8;
		byte b = 2;
		byte c = 8;
		byte d = 0;
		ArrayList<Byte> tempByteList = new ArrayList<Byte>();
		tempByteList.add(a);
		tempByteList.add(b);
		tempByteList.add(c);
		tempByteList.add(d);
		
		String model = "00000000";
		String thisPicData = "";
		
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
				System.out.println(inner);
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
