package cs.eightstep;
import java.util.*;
import java.io.*;
import static cs.eightstep.cmath.write;
import static cs.eightstep.cmath.read;
import static cs.eightstep.Moves.*;

final class edge12 {

	int[] edge = new int[12];
	int[] temp;

	static byte[] prun;
	static byte[] prunP = new byte[12*11*10*9*8*7*6*5*4*3/5];
	
	final static int[][] edgex = {	{ 3, 0, 1, 2, 4, 5, 6, 7, 8, 9,10,11},
									{ 2, 3, 0, 1, 4, 5, 6, 7, 8, 9,10,11},
									{ 1, 2, 3, 0, 4, 5, 6, 7, 8, 9,10,11},
									{ 0, 1, 2, 7, 4, 5, 6, 3, 8, 9,11,10},
									{ 8, 1, 2, 3, 4, 5,11, 7, 6, 9,10, 0},
									{ 6, 1, 2, 3, 4, 5, 0, 7,11, 9,10, 8},
									{11, 1, 2, 3, 4, 5, 8, 7, 0, 9,10, 6},
									{ 0, 1, 2, 3, 7, 4, 5, 6, 8, 9,10,11},
									{ 0, 1, 2, 3, 6, 7, 4, 5, 8, 9,10,11},
									{ 0, 1, 2, 3, 5, 6, 7, 4, 8, 9,10,11},
									{ 0, 5, 2, 3, 4, 1, 6, 7, 9, 8,10,11},
									{ 0, 1,10, 3, 9, 5, 6, 7, 8, 2, 4,11},
									{ 0, 1, 4, 3, 2, 5, 6, 7, 8,10, 9,11},
									{ 0, 1, 9, 3,10, 5, 6, 7, 8, 4, 2,11},
									{ 2, 3, 0, 1, 4, 5, 6, 7, 8,11,10, 9},
									{ 0, 1, 6, 7, 4, 5, 2, 3, 8, 9,11,10},
									{ 6, 1, 2, 5, 4, 3, 0, 7,11, 9,10, 8},
									{ 0, 1, 2, 3, 6, 7, 4, 5,10, 9, 8,11},
									{ 4, 5, 2, 3, 0, 1, 6, 7, 9, 8,10,11},
									{ 0, 7, 4, 3, 2, 5, 6, 1, 8,10, 9,11}};
							
	final static int[][] edgeox = {	{ 1, 2, 3, 0, 4, 5, 6, 7, 8, 9,10,11},
									{ 2, 3, 0, 1, 4, 5, 6, 7, 8, 9,10,11},
									{ 3, 0, 1, 2, 4, 5, 6, 7, 8, 9,10,11},
									{ 0, 1, 2, 7, 4, 5, 6, 3, 8, 9,11,10},
									{11, 1, 2, 3, 4, 5, 8, 7, 0, 9,10, 6},
									{ 6, 1, 2, 3, 4, 5, 0, 7,11, 9,10, 8},
									{ 8, 1, 2, 3, 4, 5,11, 7, 6, 9,10, 0},
									{ 0, 1, 2, 3, 5, 6, 7, 4, 8, 9,10,11},
									{ 0, 1, 2, 3, 6, 7, 4, 5, 8, 9,10,11},
									{ 0, 1, 2, 3, 7, 4, 5, 6, 8, 9,10,11},
									{ 0, 5, 2, 3, 4, 1, 6, 7, 9, 8,10,11},
									{ 0, 1, 9, 3,10, 5, 6, 7, 8, 4, 2,11},
									{ 0, 1, 4, 3, 2, 5, 6, 7, 8,10, 9,11},
									{ 0, 1,10, 3, 9, 5, 6, 7, 8, 2, 4,11},
									{ 2, 3, 0, 1, 4, 5, 6, 7,10, 9, 8,11},
									{ 4, 1, 2, 7, 0, 5, 6, 3, 8, 9,11,10},
									{ 6, 7, 2, 3, 4, 5, 0, 1,11, 9,10, 8},
									{ 0, 1, 2, 3, 6, 7, 4, 5, 8,11,10, 9},
									{ 0, 5, 6, 3, 4, 1, 2, 7, 9, 8,10,11},
									{ 0, 1, 4, 5, 2, 3, 6, 7, 8,10, 9,11}};
	
	private static int[] ptb = new int[16 * 4];
	private static byte[] GetPacked = new byte[243*8];
	private static int[] fact = {19958400, 1814400, 181440, 20160, 2520, 360, 60, 12, 3, 1};
	
	static {
	
		for (int i=0; i<243; i++) {
			for (int j=0; j<5; j++) {
				int l = i;
				for (int k=1; k<=j; k++)
					l /= 3;
				GetPacked[i*8+j] = (byte)(l % 3);
			}
		}
		for (int i=0; i<16; i++) {
			for (int j=0; j<3; j++) {
				ptb[i*4+j] = i + (j - i + 18 + 1) % 3 - 1;
			}
		}
		
/*		
		final int fx = 9580032;
		read(prunP, 0, fx, "edge12.prun0");
		read(prunP, fx, fx, "edge12.prun1");
		read(prunP, fx*2, fx, "edge12.prun2");
		read(prunP, fx*3, fx, "edge12.prun3");
		read(prunP, fx*4, fx, "edge12.prun4");
*/



		if (!read(prunP, 0, prunP.length, "edge12.prunP")) {
			createPrun();
			write(prunP, 0, prunP.length, "edge12.prunP");
		}

//		write(prunP, 0, fx, "edge12.prun0");
//		write(prunP, fx, fx, "edge12.prun1");
//		write(prunP, fx*2, fx, "edge12.prun2");
//		write(prunP, fx*3, fx, "edge12.prun3");
//		write(prunP, fx*4, fx, "edge12.prun4");
		
	}
	
	static void createPrun() {
		prun = new byte[12*11*10*9*8*7*6*5*4*3/4];
		edge12 e = new edge12();
		edge12 f = new edge12();
		Arrays.fill(prun, (byte)-1);
		setpruning2(0, 0);
		int depth = 0;
		int done = 1;
		while (done != 12*11*10*9*8*7*6*5*4*3) {
			if (depth <= 10) {
				for (int i=0; i<12*11*10*9*8*7*6*5*4*3; i++) {
					if (getpruning2(i)==depth%3) {
						e.set(i);
						for (int m=0; m<20; m++) {
							int idx = f.getmv(e.edge, m);
							if (getpruning2(idx) == 0x03) {
								setpruning2(idx, (depth+1)%3);
								done++;
								if ((done & 0x0fffff) == 0) {
									System.out.print(String.format("%9d\r", done));
								}								
							}
						}
					}
				}
			} else {
				for (int i=0; i<12*11*10*9*8*7*6*5*4*3; i++) {
					if (getpruning2(i)==0x03) {
						e.set(i);
						for (int m=0; m<20; m++) {
							int idx = f.getmv(e.edge, m);
							if (getpruning2(idx)==depth%3) {
								setpruning2(i, (depth+1)%3);
								done++;
								if ((done & 0x0fffff) == 0) {
									System.out.print(String.format("%9d\r", done));
								}
								break;
							}
						}
					}
				}
			}
			depth++;
			System.out.println(String.format("%2d%10d", depth, done));
		}
		for (int i=0; i<12*11*10*9*8*7*6*5*4*3/5; i++) {
			int n = 1;
			int value = 0;
			for (int j=0; j<4; j++) {
				value += n * getpruning2(4*i+j);
				n *= 3;
			}
			value += n * getpruning2(12*11*10*9*8*7*6*5*4*3/5*4+i);
			prunP[i] = (byte)value;
		}
		prun = null;
		System.gc();
	}
	
	final static void setpruning2(int index, int value) {
		prun[index >>> 2] ^= (3 ^ value) << ((index & 0x03) << 1);
	}
	
	final static int getpruning2(int index) {
		return ((prun[index >>> 2] >>> (((index & 0x03) << 1))) & 3);
	}
	
	final static int getpruningP(int index) {
		if (index < 12*11*10*9*8*7*6*5*4*3/5*4) {
			int data = prunP[index >>> 2]&0x0ff;
			return GetPacked[(data<<3) | (index & 3)];
		} else {
			int data = prunP[index-12*11*10*9*8*7*6*5*4*3/5*4]&0x0ff;
			return GetPacked[(data<<3) | 4];
		}
	}
	
	final static int getprun(int edge, int prun) {
		return ptb[(prun << 2) | getpruningP(edge)];
	}
	
	final int getprun(int edge) {
		int depth = 0;
		int depm3 = getpruningP(edge);
		while (edge!=0) {
			if (depm3 == 0)
				depm3 = 2;
			else
				depm3--;
			set(edge);
			for (int m=0; m<20; m++) {
				int edgex = getmv(this.edge, m);
				if (getpruningP(edgex)==depm3) {
					depth++;
					edge = edgex;
					break;
				}
			}
		}
		return depth;
	}

	int set(cube c) {
		if (temp == null) {
			temp = new int[12];
		}
		for (int i=0; i<12; i++) {
			temp[i] = i;
			edge[i] = c.ep[i+12]%12;
		}
		int parity = 0;
		for (int i=0; i<12; i++) {
			while (edge[i] != i) {
				int t = edge[i];
				edge[i] = edge[t];
				edge[t] = t;
				int s = temp[i];
				temp[i] = temp[t];
				temp[t] = s;
				parity ^= 1;
			}
		}
		for (int i=0; i<12; i++) {
			edge[i] = temp[c.ep[i]%12];
		}
		return parity;
	}

	static int getmv(int[] ep, int mv) {
		int[] movo = edgeox[mv];
		int[] mov = edgex[mv];
		int idx = 0;
		int m = (1 << movo[ep[mov[11]]]) | (1 << movo[ep[mov[10]]]);
		for (int i=9; i>=0; --i) {
			int t = 1 << movo[ep[mov[i]]];
			idx += cmath.bitCount(m & (t - 1)) * fact[i];
			m |= t;
		}
		return idx;			
	}
	
	final int get() {
		int m = (1 << edge[11]) | (1 << edge[10]);		
		int idx = 0;
		for (int i=9; i>=0; --i) {
			int t = 1 << edge[i];
			idx += cmath.bitCount(m & (t - 1)) * fact[i];
			m |= t;
		}
		return idx;
	}	

	final void set(int idx) {
		int s = 0;
		edge[11] = 1;
		edge[10] = 0;
		for (int i=9; i>=0; i--) {
			edge[i] = idx % (12-i);
			s ^= edge[i];
			idx /= (12-i);
			for (int j=i+1; j<12; j++) {
				if (edge[j] >= edge[i])
					edge[j]++;
			}
		}
		if ((s & 1) != 0) {
			int temp = edge[11];
			edge[11] = edge[10];
			edge[10] = temp;
		}
	}
}
