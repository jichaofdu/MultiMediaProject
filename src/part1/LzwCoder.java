package part1;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LzwCoder {
	private final int BITS = 12;
	private final int TABLE_SIZE = 5021;
	private final int HASHING_SHIFT = 4;
	private final int MAX_VALUE = (1 << BITS) - 1; ;
	private final int MAX_CODE = MAX_VALUE - 1;
	private final int EOF = -1;
	private DataInputStream input;
	private DataOutputStream output;
	private short[] code_value;
	private short[] prefix_code;
	private short[] append_character;
	
	public LzwCoder(){
		this.code_value = new short[TABLE_SIZE];
		this.prefix_code = new short[TABLE_SIZE];
		this.append_character = new short[TABLE_SIZE];
		for (short i = 0; i < TABLE_SIZE; i++){
			this.code_value[i] = -1;
		}
	}
	

	//First step: Input first byte store in STRING
	//Second step: Code Loop
	//Loop:
		//First:Input next byte, store in CHAR
		//Second: 
			//If STRING + CHAR in table 
				//STRING = STRING + CHAR
			//If STRING + CHAR not in table
				//output the code for STRING
				//add entry  in table for STRING + CHAR
				//STRING = CHAR
		//Third:
			//If and bytes to input ,then return loop.
			//If no more bytes to input,then terminate
		//Forth:
			//Output the code for STRING
	//End
	public void codeProcedure(InputStream is, OutputStream os) throws IOException{
		input = new DataInputStream(is);
		output = new DataOutputStream(os);
		short next_code = 0;
		short character = 0;
		short string_code = 0;
		short index = 0;
		next_code = 256;

		for (short i = 0; i < TABLE_SIZE; i++)
			code_value[i] = -1;

		string_code = (short) input.read();

		while ((character = (short) input.read()) != EOF) {
			index = find_match(string_code, character);

			if (code_value[index] != -1) {
				string_code = code_value[index];
			} else {
				if (next_code <= MAX_CODE) {
					code_value[index] = next_code++;
					prefix_code[index] = string_code;
					append_character[index] = character;
				}

				output.writeShort(string_code);
				string_code = character;
			}
		}

		output.writeShort(string_code);
		output.writeShort((short) MAX_VALUE);
		output.writeShort((short) 0);
		output.close();
		input.close();
	}

	private short find_match(short hash_prefix, short hash_character) {
		int index = 0;
		int offset = 0;
		index = (hash_character << HASHING_SHIFT) ^ hash_prefix;
		if (index == 0)
			offset = 1;
		else
			offset = TABLE_SIZE - index;
		while (true) {
			if (code_value[index] == -1){
				return (short) index;
			}
			if (prefix_code[index] == hash_prefix&& append_character[index] == hash_character){
				return (short) index;
			}
			index -= offset;
			if (index < 0){
				index += TABLE_SIZE;
			}
		}
	}

}
