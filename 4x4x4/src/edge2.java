package cs.eightstep;

import static cs.eightstep.cmath.*;
import static cs.eightstep.Moves.*;
import static cs.eightstep.center2.rlmv;
import static cs.eightstep.center2.rlrot;
import static cs.eightstep.center.symmult;
import static cs.eightstep.center.syminv;

import java.util.*;

/*

					14	2	
				1			15
				13			3
					0	12	
	1	13			0	12			3	15			2	14	
9			20	20			11	11			22	22			9
21			8	8			23	23			10	10			21
	17	5			18	6			19	7			16	4	
					18	6	
				5			19
				17			7
					4	16	


*/

final class edge2 {

	static byte[] eprun = new byte[86048 * 70];
	static int[] sym2raw = new int[86048];
	static int[][] esmv = new int[86048][36];
	static byte[][] trans = new byte[2048][24];
	
	static {
	
		for (int i=0; i<2048; i++) {
			for (int j=0; j<11; j++) {
				trans[i][j] = (byte) ((i>>>j)&1);
				trans[i][j+12] = (byte) (((i>>>j)&1)^1);
			}
			int t = Integer.bitCount(i) & 1;
			trans[i][11] = (byte) t;
			trans[i][23] = (byte) (t^1);
		}	

		edge2 c = new edge2();
		edge2 d = new edge2();
		
		byte[] occ = new byte[1352078/8+1];
		int count = 0;
		
		for (int i=0; i<1352078; i++) {
			if ((occ[i>>>3]&(1<<(i&7))) == 0) {
				sym2raw[count++] = i;
//				occ[i>>>3] |= (1<<(i&7));
				c.set(i);
				for (int j=0; j<16; j++) {
					int idx = c.get();
					occ[idx>>>3] |= (1<<(idx&7));
					c.rot(0);
					if (j%2==1) c.rot(1);
					if (j%8==7) c.rot(2);
				}
			}
		}
		occ = null;
		System.gc();
		
//		read(esmv, 0, 43024, 36, "step2.move0");
//		read(esmv, 43024, 86048, 36, "step2.move1");
		
		
		if (!read(esmv, 0, 86048, 36, "step2.move")) {
			createMoveTable();
			write(esmv, 0, 86048, 36, "step2.move");
		}
/*		
		write(esmv, 0, 43024, 36, "step2.move0");
		write(esmv, 43024, 86048, 36, "step2.move1");
		
*/		

		if (!read(eprun, 0, eprun.length, "step2.prun")) {
			createPrun();
			write(eprun, 0, eprun.length, "step2.prun");
		}
		System.gc();	
	}
	
	static void createMoveTable() {
		edge2 c = new edge2();
		edge2 d = new edge2();
		for (int i=0; i<86048; i++) {
			c.set(sym2raw[i]);
			for (int m=0; m<36; m++) {
				d.set(c.ep);
				d.move(m);
				esmv[i][m] = d.getsym();
			}
			if (i % 10000 == 0) {
				System.out.println(i);
			}
		}	
	}

	static void createPrun() {
		edge2 c = new edge2();
		char[] SymState = new char[86048];
        for (int i=0; i<86048; i++) {
        	c.set(sym2raw[i]);
        	for (int j=0; j<16; j++) {
        		if (Arrays.binarySearch(sym2raw, c.get()) >= 0)
					SymState[i] |= (1 << j);
				c.rot(0);
				if (j%2==1) c.rot(1);
				if (j%8==7) c.rot(2);
			}
        }

		Arrays.fill(eprun, (byte)-1);
		
		int[][] ok = {{ux2}, {fx2}, {ux2, Lx1, Lx1}, {fx2, Lx1, Lx1}, {ux2, Lx1, Lx1, fx2}};
		
		for (int[] seq : ok) {
			int rl = 0;
			for (int m : seq) {
				rl = rlmv[rl][std2move[m]];
			}
			eprun[rl] = 0;
		}
		eprun[0] = 0;
		int depth = 0;
		int done = 6;
		while (done != 86048 * 70) {
			if (depth <= 6) {
				for (int i=0; i<86048 * 70; i++) {
					if (eprun[i]==depth) {
						int rl = i % 70;
						int edge = i / 70;
						for (int m=0; m<23; m++) {
							int edgex = esmv[edge][move2std[m]];
							int sym = edgex & 0x0f;
							edgex >>>= 4;
							int rlx = rlrot[rlmv[rl][m]][sym];
							int idx = edgex * 70 + rlx;
					
							if (eprun[idx] == -1) {
								eprun[idx] = (byte)(depth+1);
								done++;
								int syms = SymState[edgex];
								if (syms != 1) {
									for (int j=1; j<16; j++) {
										syms >>>= 1;
										if ((syms & 1) == 1) {
											idx = edgex * 70 + rlrot[rlx][j];
											if (eprun[idx]==-1) {
												eprun[idx] = (byte)(depth+1);
												done++;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				for (int i=0; i<86048 * 70; i++) {
					if (eprun[i] == -1) {
						int rl = i % 70;
						int edge = i / 70;
						for (int m=0; m<23; m++) {
							int edgex = esmv[edge][move2std[m]];
							int sym = edgex & 0x0f;
							edgex >>>= 4;
							int rlx = rlrot[rlmv[rl][m]][sym];
							int idx = edgex * 70 + rlx;
					
							if (eprun[idx] == depth) {
								eprun[i] = (byte)(depth+1);
								done++;
								break;
							}
						}
					}
				}
			}
			depth++;
			System.out.println(String.format("%2d%10d", depth, done));
		}
	}

	int[] ep = new int[24];

	void rot(int r) {
		switch (r) {
			case 0:
				move(ux2);
				move(dx2);
				break;
			case 1:
				move(rx1);
				move(lx3);
				break;
			case 2:
				swap(ep, 1, 13, 15, 3, 1);
				swap(ep, 20, 8, 11, 23, 1);
				swap(ep, 5, 17, 19, 7, 1);
				swap(ep, 9, 21, 22, 10, 1);
				swap(ep, 14, 0, 2, 12, 1);
				swap(ep, 18, 4, 6, 16, 1);
				break;
		}
	
	}

	void move(int m) {
		int key = m % 3;
		m /= 3;
		switch (m) {
			case 0:	//U
				swap(ep, 0, 1, 2, 3, key);
				swap(ep, 12, 13, 14, 15, key);				
				break;
			case 1:	//R
				swap(ep, 11, 15, 10, 19, key);
				swap(ep, 23, 3, 22, 7, key);				
				break;
			case 2:	//F
				swap(ep, 0, 11, 6, 8, key);
				swap(ep, 12, 23, 18, 20, key);				
				break;
			case 3:	//D
				swap(ep, 4, 5, 6, 7, key);
				swap(ep, 16, 17, 18, 19, key);				
				break;
			case 4:	//L
				swap(ep, 1, 20, 5, 21, key);
				swap(ep, 13, 8, 17, 9, key);				
				break;
			case 5:	//B
				swap(ep, 2, 9, 4, 10, key);
				swap(ep, 14, 21, 16, 22, key);				
				break;
			case 6:	//u
				swap(ep, 0, 1, 2, 3, key);
				swap(ep, 12, 13, 14, 15, key);				
				swap(ep, 9, 22, 11, 20, key);
				break;
			case 7:	//r
				swap(ep, 11, 15, 10, 19, key);
				swap(ep, 23, 3, 22, 7, key);				
				swap(ep, 2, 16, 6, 12, key);
				break;
			case 8:	//f
				swap(ep, 0, 11, 6, 8, key);
				swap(ep, 12, 23, 18, 20, key);				
				swap(ep, 3, 19, 5, 13, key);
				break;
			case 9:	//d
				swap(ep, 4, 5, 6, 7, key);
				swap(ep, 16, 17, 18, 19, key);				
				swap(ep, 8, 23, 10, 21, key);
				break;
			case 10://l
				swap(ep, 1, 20, 5, 21, key);
				swap(ep, 13, 8, 17, 9, key);				
				swap(ep, 14, 0, 18, 4, key);
				break;
			case 11://b
				swap(ep, 2, 9, 4, 10, key);
				swap(ep, 14, 21, 16, 22, key);				
				swap(ep, 7, 15, 1, 17, key);
				break;		
		}
	}
	
	int get() {
		int idx = 0;
		int r = 12;
		for (int i=22; i>=0; i--) {
			if (ep[i] != ep[23]) {
				idx += Cnk[i][r--];
			}
		}
		return idx;
	}
	
	int getsym() {
		for (int j=0; j<16; j++) {
			int cord = raw2sym(get());
			if (cord != -1)
				return cord * 16 + j;
			rot(0);
			if (j%2==1) rot(1);
			if (j%8==7) rot(2);
		}
		System.out.print('e');
		return -1;
	}

	void set(int idx) {
		int r = 12;
		ep[23] = 0;
		for (int i=22; i>=0; i--) {
			if (idx >= Cnk[i][r]) {
				idx -= Cnk[i][r--];
				ep[i] = 1;
			} else {
				ep[i] = 0;
			}
		}
	}	

	void set(int[] ep) {
		for (int i=0; i<24; i++) {
			this.ep[i] = ep[i];
		}
	}
	
	static int raw2sym(int n) {
		int m = Arrays.binarySearch(sym2raw, n);
		return (m>=0 ? m : -1);
	}
	
	static int getidx(int[] ep, boolean[] trans) {
		boolean temp = trans[ep[23]];
		int idx = 0;
		int r = 12;
		for (int i=22; i>=0; i--) {
			if (trans[ep[i]] != temp) {
				idx += Cnk[i][r--];
			}
		}
		return idx;	
	}
	
	int getsymidx(int[] ep, int idx) {
		for (int i=0; i<24; i++) {
			this.ep[i] = trans[idx][ep[i]];
		}
		return getsym();
	}
}
