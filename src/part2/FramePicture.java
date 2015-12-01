package part2;
import java.util.ArrayList;

public class FramePicture {
	private int width;
	private int height;
	private int xBias;
	private int yBias;
	private int m;
	private int i;
	private int s;
	private int r;
	private int pixel;
	private int[] colorPanel;//Í¼Æ¬µÄµ÷É«°å
	private ArrayList<Byte> dataList;
	
	public FramePicture(int width,int height,int xBias,int yBias){
		this.width = width;
		this.height = height;
		colorPanel = new int[256];
		dataList = new ArrayList<Byte>();
	}
	
	public void setColorPanel(int[] newPanel){
		this.colorPanel = newPanel;
	}
	
	public void readNewByte(byte newByte){
		dataList.add(newByte);
	}
	
	public ArrayList<Byte> getDataList(){
		return dataList;
	}
	
	public int[] getColorPanel(){
		return colorPanel;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getxBias() {
		return xBias;
	}

	public void setxBias(int xBias) {
		this.xBias = xBias;
	}

	public int getyBias() {
		return yBias;
	}

	public void setyBias(int yBias) {
		this.yBias = yBias;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getS() {
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getPixel() {
		return pixel;
	}

	public void setPixel(int pixel) {
		this.pixel = pixel;
	}
	
}
