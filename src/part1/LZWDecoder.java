package part1;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class LZWDecoder {
	private  final int BITS = 12; 
	private  final int TABLE_SIZE = 5021;;
	private  final int MAX_VALUE = (1 << BITS) - 1;
	private  final int MAX_CODE = MAX_VALUE - 1;
	private DataInputStream input;
	private DataOutputStream output;
	private short[] prefixCode ;
	private short[] append_character;
	private List<Short> string = new ArrayList<Short>();
	private List<Short> decodeStack = new ArrayList<Short>();
	private int decodeStackCount = 0;
	
	public LZWDecoder(){
		this.prefixCode = new short[TABLE_SIZE];
		this.append_character = new short[TABLE_SIZE];
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
		short next_code = 256; /* This is the next available code to define */
		short new_code;
		short old_code = input.readShort();
		short character = old_code;
		System.out.println("Expanding...");
		this.output.write(old_code);
		while ((new_code = input.readShort()) != MAX_VALUE) {
			string = new ArrayList<Short>();
			if (new_code >= next_code) {
				setListValue(decodeStack, decodeStackCount, character);
				string.add(character);
				decodeString(decodeStackCount + 1, old_code);

			} else {
				decodeString(decodeStackCount, new_code);
			}
			character = string.get(string.size() - 1);
			for (int i = string.size() - 1; i >= 0; i--) {
				output.write(string.get(i));
			}
			if (next_code <= MAX_CODE) {
				prefixCode[next_code] = old_code;
				append_character[next_code] = character;
				next_code++;
			}
			old_code = new_code;
		}
		input.close();
		output.close();
	}
	
	private static void setListValue(List<Short> list, int location, Short value) {
		if (location >= list.size()) {
			list.add(value);
		} else {
			list.set(location, value);
		}
	}

	private void decodeString(int decode_stack_address, short code) {
		int i = 0;
		decodeStackCount = decode_stack_address;
		while(code > 255){
			setListValue(decodeStack, decodeStackCount,append_character[code]);
			decodeStackCount++;
			string.add(append_character[code]);
			code = prefixCode[code];
			if (i++ >= MAX_CODE){
				System.out.println("Fatal error during code expansion.");
				System.exit(-3);
			}
		}
		setListValue(decodeStack, decodeStackCount, code);
		string.add(code);
	}
	
}
