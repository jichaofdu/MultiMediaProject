package part2;
import java.io.DataInputStream;
import java.io.IOException;

public class LzwDecoder {
    DataInputStream input;
    int[][] dictionary;
    int dictionaryLength;
    int bitsPerCode;
    int bitPosition;
    int size;
    int bitsPerCodeOriginal;
    int count;
    int bytesNum;
    int STOPcode;
    int CLEARcode;
    int[] colorIndexTable;
    int valueNow;

    public LzwDecoder(int bitsPerCode,int bytesnum,int[] table,int width,int height,DataInputStream in) throws IOException{
        this.input = in;
        this.bitsPerCode = bitsPerCodeOriginal = bitsPerCode;
        this.colorIndexTable = table;
        this.bytesNum = bytesnum;
        this.dictionaryLength = table.length + 2;
        this.valueNow = -1;
        this.bitPosition = count = 0;
        this. STOPcode = table.length + 1;
        this.CLEARcode = table.length;
        this.size = width * height;
        this.dictionary = new int[1 << 12][];
        for (int i = 0; i < table.length; i++){
            int[] temp = {i};
            this.dictionary[i] = temp;
        }
        this.valueNow = this.input.readUnsignedByte();
        this.count++;
    }
    
    /**
     * ��ͼƬ���н���
     * @return
     * @throws IOException
     */
    public int[] decode() throws IOException{
        int index = 0;
        int[] picColorData = new int[size];
        int nCode = CLEARcode;
        while (true){
            int oldCode = nCode;
            nCode = nextCode();
           
            if(nCode == STOPcode){ //�����ȡ��ֹͣλ������
                break;
            }else if(nCode == CLEARcode){//��ȡ�����λ
                resetDict();
            }else if(oldCode == CLEARcode){
                for(int i = 0; i < dictionary[nCode].length; i++){
                	picColorData[index++] = colorIndexTable[dictionary[nCode][i]];
                }
            }else{
                if(nCode < dictionaryLength){//������ֵ��д���
                    int[] temp = new int[dictionary[oldCode].length + 1];
                    for (int i = 0; i < dictionary[nCode].length; i++) {
                    	picColorData[index++] = colorIndexTable[dictionary[nCode][i]];
                    }
                    System.arraycopy(dictionary[oldCode], 0, temp, 0, dictionary[oldCode].length);
                    temp[temp.length - 1] = dictionary[nCode][0];
                    dictionary[dictionaryLength++] = temp;
                }else{//������ֵ��в�����
                    int[] temp = new int[dictionary[oldCode].length + 1];
                    System.arraycopy(dictionary[oldCode], 0, temp, 0, dictionary[oldCode].length);
                    temp[temp.length - 1] = dictionary[oldCode][0];
                    for(int i = 0; i < temp.length; i++){
                    	picColorData[index++] = colorIndexTable[temp[i]];
                    }
                    dictionary[dictionaryLength++] = temp;
                }
                if(dictionaryLength >= (1 << bitsPerCode) && bitsPerCode < 12){//�ַ���������
                    bitsPerCode++;
                }
            }
        }
        return picColorData;
    }
    
    /**
     * ���ͣ����������н�ȡ��һ��Code
     * @return ���ؽ�ȡ��Code
     * @throws IOException
     */
    private int nextCode() throws IOException {
        int fillbits = 0;
        int value = 0;
        while(fillbits < bitsPerCode){
            int nextbits = valueNow; 
            int bitsfromhere = 8 - bitPosition;
            if(bitsfromhere > (bitsPerCode - fillbits)){
                bitsfromhere = bitsPerCode - fillbits;
                nextbits &= (0xff >>> 8 - bitPosition - bitsfromhere);
            }else{
            	nextbits >>>= bitPosition;
            }
            value |= nextbits << fillbits;
            fillbits += bitsfromhere;
            bitPosition += bitsfromhere;
            if(bitPosition >= 8){
                bitPosition = 0;
                if (count >= bytesNum){
                	bytesNum = input.readUnsignedByte();
                    count = 0;
                }
                valueNow = input.readUnsignedByte();
                count++;
            }
        }
        return value;
    }
    
    /**
     * ���ͣ�������CLEAR�ַ�֮����Ҫ�����ֵ�
     */
    private void resetDict(){
        dictionaryLength = colorIndexTable.length + 2;
        bitsPerCode = bitsPerCodeOriginal;
    }

}
