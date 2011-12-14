package cs.eightstep;
import java.io.*;

final class cmath {
	static int[][] Cnk = new int[25][25];
	
	static {
		for (int i=0; i<25; i++) {
			Cnk[i][i] = 1;
			Cnk[i][0] = 1;
		}
		for (int i=1; i<25; i++) {
			for (int j=1; j<=i; j++) {
				Cnk[i][j] = Cnk[i-1][j] + Cnk[i-1][j-1];
			}
		}
	}
	
	final static OutputStream getOutput(String filename) throws IOException {
		return new BufferedOutputStream(new FileOutputStream(filename));
	}
	
	final static InputStream getInput(String filename) throws IOException {
		return new BufferedInputStream(new FileInputStream(filename));
	}
	
	final static boolean write(byte[] data, int idx, int length, String filename) {
		try {
			OutputStream os = getOutput(filename);
			os.write(data, idx, length);
			os.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	final static boolean read(byte[] data, int idx, int length, String filename) {
		try {
			InputStream is = getInput(filename);
			is.read(data, idx, length);
			is.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}		
	}

	final static boolean read(int[][] data, int l, int r, int width, String filename) {
		try {
			InputStream is = getInput(filename);
			byte[] buf = new byte[width * 3];
			for (int i=l; i<r; i++) {
				is.read(buf);
				for (int j=0; j<width; j++) {
					data[i][j] = (buf[j*3])&0xff | (buf[j*3+1]<<8)&0xff00 | (buf[j*3+2]<<16)&0xff0000;
				}
			}
			is.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	final static boolean write(int[][] data, int l, int r, int width, String filename) {
		try {
			OutputStream os = getOutput(filename);
			byte[] buf = new byte[width * 3];
			for (int i=l; i<r; i++) {
				int idx = 0;
				for (int j=0; j<width; j++) {
					buf[idx++] = (byte)(data[i][j] & 0xff);
					buf[idx++] = (byte)((data[i][j]>>>8) & 0xff);
					buf[idx++] = (byte)((data[i][j]>>>16) & 0xff);
				}
				os.write(buf);
			}
			os.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	final static int bitCount(int i) {
		i = i - ((i >>> 1) & 0x55555555);
		i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
		return (i + (i >>> 8) + (i >>> 4)) & 0x0f;
	}

	public static void swap(int[] arr, int a, int b, int c, int d, int key) {
		int temp;
		switch (key) {
			case 0:
				temp = arr[d];
				arr[d] = arr[c];
				arr[c] = arr[b];
				arr[b] = arr[a];
				arr[a] = temp;
				return;
			case 1:
				temp = arr[a];
				arr[a] = arr[c];
				arr[c] = temp;
				temp = arr[b];
				arr[b] = arr[d];
				arr[d] = temp;
				return;
			case 2:
				temp = arr[a];
				arr[a] = arr[b];
				arr[b] = arr[c];
				arr[c] = arr[d];
				arr[d] = temp;
				return;
		}
	}
}
