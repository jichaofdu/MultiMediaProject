package part1;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class LzwDecoder {
	private  final int BITS = 12; 
	private  final int TABLESIZE = 5021;
	private  final int MAXVALUE = (1 << BITS) - 1;
	private  final int MAXCODE = MAXVALUE - 1;
	private int[] oldCode;
	private int[] newChar;
	private List<Integer> string = new ArrayList<Integer>();
	private List<Integer> dictionary = new ArrayList<Integer>();
	private int decodeStackCount = 0;
	private DataInputStream input;
	private DataOutputStream output;
	
	public LzwDecoder(){
		this.oldCode = new int[TABLESIZE];
		this.newChar = new int[TABLESIZE];
	}
	

	//First step: Input first code store in OCODE;
	//Second step: Output translation of OCODE
	//Third step: Decode Loop
	//Loop:
		//First:Input next code store in NOCODE
		//Second: 
			//If NCODE in table
				//STRING = = translation of NCODE
			//If NCODE not in table
				//STRING = translation of OCODE
				//STRING = string + char
		//Third:
			//Output STRING
			//CHAR = the first character in STRING
			//Add enter in table for OCODE + CHAR
			//OCODE = NCODE
		//Forth:
			//If more codes to input ,then return loop.
			//If no more codes to input,then terminate
	//End
	public void decodeProcedure(InputStream is,OutputStream os) throws IOException{
		this.input = new DataInputStream(is);
		this.output = new DataOutputStream(os);
		int next_code = 256; /* This is the next available code to define */
		int nCode;
		int oCode = input.readShort();
		int character = oCode;
		this.output.write(oCode);
		while ((nCode = input.readShort()) != MAXVALUE) {
			string = new ArrayList<Integer>();
			if (nCode >= next_code) {
				setListValue(dictionary, decodeStackCount, character);
				string.add(character);
				decodeString(decodeStackCount + 1, oCode);

			}else{
				decodeString(decodeStackCount, nCode);
			}
			character = string.get(string.size() - 1);
			for (int i = string.size() - 1; i >= 0; i--){
				output.write(string.get(i));
			}
			if (next_code <= MAXCODE){
				oldCode[next_code] = oCode;
				newChar[next_code] = character;
				next_code++;
			}
			oCode = nCode;
		}
		input.close();
		output.close();
	}
	
	private static void setListValue(List<Integer> list, int location, int value){
		if (location >= list.size()){
			list.add(value);
		}else{
			list.set(location, value);
		}
	}

	private void decodeString(int dicIndex, int code){
		decodeStackCount = dicIndex;
		while(code > 255){
			setListValue(dictionary, decodeStackCount,newChar[code]);
			decodeStackCount++;
			string.add(newChar[code]);
			code = oldCode[code];
		}
		setListValue(dictionary, decodeStackCount, code);
		string.add(code);
	}
	
}
