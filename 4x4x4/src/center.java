package cs.eightstep;

import static cs.eightstep.cmath.*;
import static cs.eightstep.Moves.*;
import java.util.*;
/*
			0	1
			3	2

20	21		8	9		16	17		12	13
23	22		11	10		19	18		15	14

			4	5
			7	6
*/

final class center {
	
	static int[][] ctsmv = new int[15582][36];
	static int[][] ctsmvx = new int[538365][36];
	static int[] sym2raw = new int[15582];
	static byte[] csprun = new byte[15582];
	static byte[] csprunx = new byte[538365*2];
	
	static int[][] symmult = new int[48][48];
	static int[][] symmove = new int[48][36];
	static int[] syminv = new int[48];
	static int[] finish = new int[48];
	
	static int[] sym2rawx = new int[538365];
	
	static {
		initSym();
		center c = new center();
		c.set(0);
		for (int i=0; i<48; i++) {
			finish[syminv[i]] = c.get();
			c.rot(0);
			if (i%2==1) c.rot(1);
			if (i%8==7) c.rot(2);
			if (i%16==15) c.rot(3);
		}
		
		byte[] occ = new byte[735471/8+1];
		int count = 0;
		for (int i=0; i<735471; i++) {
			if ((occ[i>>>3]&(1<<(i&7))) == 0) {
				sym2raw[count++] = i;
//				occ[i] = true;
				c.set(i);
				for (int j=0; j<48; j++) {
					int idx = c.get();
					occ[idx>>>3] |= (1<<(idx&7));
					c.rot(0);
					if (j%2==1) c.rot(1);
					if (j%8==7) c.rot(2);
					if (j%16==15) c.rot(3);
				}
			}
		}
		occ = null;
		System.gc();
		
		if (!read(ctsmv, 0, 15582, 36, "center.move")) {
			createMoveTable();
			write(ctsmv, 0, 15582, 36, "center.move");
		}		
		
		Arrays.fill(csprun, (byte)-1);
		csprun[0] = 0;
		int depth = 0;
		int done = 1;
		while (done != 15582) {
			if (depth < 5)
				for (int i=0; i<15582; i++) {
					if (csprun[i]==depth) {
						for (int m=0; m<27; m++) {
							if (csprun[ctsmv[i][m]>>>6] == -1) {
								csprun[ctsmv[i][m]>>>6] = (byte)(depth+1);
								++done;
							}
						}
					}
				}
			else
				for (int i=0; i<15582; i++) {
					if (csprun[i]==-1) {
						for (int m=0; m<27; m++) {
							if (csprun[ctsmv[i][m]>>>6] == depth) {
								csprun[i] = (byte)(depth+1);
								++done;
								break;
							}
						}
					}
				}
			
			depth++;
			System.out.println(String.format("%2d%10d", depth, done));
		}
	}

	static void createMoveTable() {
		center c = new center();
		center d = new center();
		for (int i=0; i<15582; i++) {
			d.set(sym2raw[i]);
			for (int m=0; m<36; m++) {
				c.set(d);
				c.move(m);
				ctsmv[i][m] = c.getsym();
			}
		}
	}
	
	public static void main(String[] args) {
		center c = new center();
		byte[] occ = new byte[25741485/8+1];
		int count = 0;
		for (int i=0; i<25741485; i++) {
			if ((occ[i>>>3]&(1<<(i&7))) == 0) {
				sym2rawx[count++] = i;
				c.setx(i);
				for (int j=0; j<48; j++) {
					int idx = c.getx();
					occ[idx>>>3] |= (1<<(idx&7));
					c.rot(0);
					if (j%2==1) c.rot(1);
					if (j%8==7) c.rot(2);
					if (j%16==15) c.rot(3);
				}
			}
		}
		System.out.println(count);
		occ = null;
		System.gc();
		
		center d = new center();
		for (int i=0; i<538365; i++) {
			d.setx(sym2rawx[i]);
			for (int m=0; m<36; m++) {
				c.set(d);
				c.move(m);
				ctsmvx[i][m] = c.getsymx();
			}
			if (i % 10000 == 0) {
				System.out.println(i);
			}
		}

		Arrays.fill(csprunx, (byte)-1);
		csprunx[0] = 0;
		int t = ctsmvx[0][fx2];
		int symt = t & 0x3f;
		csprunx[t>>>6] = 0;
		t = ctsmvx[t>>>6][symmove[symt][Ux2]];
		symt = symmult[symt][t&0x3f];
		csprunx[t>>>6] = 0;
		t = ctsmvx[t>>>6][symmove[symt][rx2]];
		csprunx[t>>>6] = 0;
		
		int depth = 0;
		int done = 4;
		int[] pmv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 
						1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1};
		while (done != 538365 * 2) {
			if (depth < 6)
				for (int i=0; i<538365 * 2; i++) {
					if (csprunx[i]==depth) {
						for (int m=0; m<27; m++) {
							int idx = ((ctsmvx[i>>>1][m]>>>6)<<1) + ((i&1)^pmv[m]);
							if (csprunx[idx] == -1) {
								csprunx[idx] = (byte)(depth+1);
								++done;
							}
						}
					}
				}
			else
				for (int i=0; i<538365 * 2; i++) {
					if (csprunx[i]==-1) {
						for (int m=0; m<27; m++) {
							int idx = ((ctsmvx[i>>>1][m]>>>6)<<1) + ((i&1)^pmv[m]);
							if (csprunx[idx] == depth) {
								csprunx[i] = (byte)(depth+1);
								++done;
								break;
							}
						}
					}
				}
			
			depth++;
			System.out.println(String.format("%2d%10d", depth, done));
		}

	}

	int[] ct = new int[24];
	int[] temp = new int[8];
	
	center() {
		for (int i=0; i<8; i++) {
			ct[i] = 1;
		}
		for (int i=8; i<24; i++) {
			ct[i] = 0;
		}		
	}
	
	center(int[] ct) {
		for (int i=0; i<24; i++) {
			this.ct[i] = ct[i];
		}
	}
	
	center(cube c, int urf) {
		for (int i=0; i<24; i++) {
			this.ct[i] = (c.ct[i]/2 == urf) ? 1 : 0;
		}
	}
	
	void move(int m) {
		int key = m % 3;
		m /= 3;
		switch (m) {
			case 0:	//U
				swap(ct, 0, 1, 2, 3, key);			
				break;
			case 1:	//R
				swap(ct, 16, 17, 18, 19, key);			
				break;
			case 2:	//F
				swap(ct, 8, 9, 10, 11, key);			
				break;
			case 3:	//D
				swap(ct, 4, 5, 6, 7, key);			
				break;
			case 4:	//L
				swap(ct, 20, 21, 22, 23, key);			
				break;
			case 5:	//B
				swap(ct, 12, 13, 14, 15, key);			
				break;
			case 6:	//u
				swap(ct, 0, 1, 2, 3, key);
				swap(ct, 8, 20, 12, 16, key);
				swap(ct, 9, 21, 13, 17, key);
				break;
			case 7:	//r
				swap(ct, 16, 17, 18, 19, key);
				swap(ct, 1, 15, 5, 9, key);
				swap(ct, 2, 12, 6, 10, key);
				break;
			case 8:	//f
				swap(ct, 8, 9, 10, 11, key);
				swap(ct, 2, 19, 4, 21, key);
				swap(ct, 3, 16, 5, 22, key);
				break;
			case 9:	//d
				swap(ct, 4, 5, 6, 7, key);
				swap(ct, 10, 18, 14, 22, key);
				swap(ct, 11, 19, 15, 23, key);
				break;
			case 10://l
				swap(ct, 20, 21, 22, 23, key);
				swap(ct, 0, 8, 4, 14, key);
				swap(ct, 3, 11, 7, 13, key);
				break;
			case 11://b
				swap(ct, 12, 13, 14, 15, key);
				swap(ct, 1, 20, 7, 18, key);
				swap(ct, 0, 23, 6, 17, key);
				break;		
		}
	}		
	
	void set(int idx) {
		int r = 8;
		for (int i=23; i>=0; i--) {
			ct[i] = 0;
			if (idx >= Cnk[i][r]) {
				idx -= Cnk[i][r--];
				ct[i] = 1;
			}
		}
	}
	
	int get() {	
		int idx = 0;
		int r = 8;
		for (int i=23; i>=0; i--) {
			if (ct[i] == 1) {
				idx += Cnk[i][r--];
			}
		}
		return idx;
	}
	
	void setx(int idx) {
		int idx8 = idx % 35;
		idx /= 35;
		int r = 4;
		temp[7] = 1;
		for (int i=6; i>=0; i--) {
			if (idx8 >= Cnk[i][r]) {
				idx8 -= Cnk[i][r--];
				temp[i] = 2;
			} else {
				temp[i] = 1;
			}
		}
		
		r = 8;
		for (int i=23; i>=0; i--) {
			ct[i] = 0;
			if (idx >= Cnk[i][r]) {
				idx -= Cnk[i][r--];
				ct[i] = temp[r];
			}
		}		
	}
	
	int getx() {
		int idx = 0;
		int r = 8;
		for (int i=23; i>=0; i--) {
			if (ct[i] != 0) {
				idx += Cnk[i][r--];
				temp[r] = ct[i];
			}
		}
		
		idx *= 35;
		r = 4;
		for (int i=6; i>=0; i--) {
			if (temp[i] != temp[7]) {
				idx += Cnk[i][r--];
			}
		}
		return idx;
	}
	
	int getsym() {
		for (int j=0; j<48; j++) {
			int cord = raw2sym(get());
			if (cord != -1)
				return cord * 64 + j;
			rot(0);
			if (j%2==1) rot(1);
			if (j%8==7) rot(2);
			if (j%16==15) rot(3);
		}
		System.out.print('e');
		return -1;
	}
	
	int getsymx() {
		for (int j=0; j<48; j++) {
			int cord = raw2symx(getx());
			if (cord != -1)
				return cord * 64 + j;
			rot(0);
			if (j%2==1) rot(1);
			if (j%8==7) rot(2);
			if (j%16==15) rot(3);
		}
		System.out.print('e');
		return -1;	
	}
	
	static int raw2sym(int n) {
		int m = Arrays.binarySearch(sym2raw, n);
		return (m>=0 ? m : -1);
	}	
	static int raw2symx(int n) {
		int m = Arrays.binarySearch(sym2rawx, n);
		return (m>=0 ? m : -1);
	}	
	
	void set(center c) {
		for (int i=0; i<24; i++) {
			this.ct[i] = c.ct[i];
		}
	}
	
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
				swap(ct, 0, 3, 1, 2, 1);
				swap(ct, 8, 11, 9, 10, 1);
				swap(ct, 4, 7, 5, 6, 1);
				swap(ct, 12, 15, 13, 14, 1);
				swap(ct, 16, 19, 21, 22, 1);
				swap(ct, 17, 18, 20, 23, 1);
				break;
			case 3:
				move(ux1);
				move(dx3);
				move(fx1);
				move(bx3);
				break;			
		}	
	}
	
	void rotate(int r) {
		for (int j=0; j<r; j++) {
			rot(0);
			if (j%2==1) rot(1);
			if (j%8==7) rot(2);
			if (j%16==15) rot(3);
		}
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof center) {
			center c = (center)obj;
			for (int i=0; i<24; i++) {
				if (ct[i] != c.ct[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	static void initSym() {
		center c = new center();
		for (int i=0; i<24; i++) {
			c.ct[i] = i;
		}
		center d = new center();
		for (int i=0; i<24; i++) {
			d.ct[i] = i;
		}
		center e = new center();
		for (int i=0; i<24; i++) {
			e.ct[i] = i;
		}
		center f = new center();
		for (int i=0; i<24; i++) {
			f.ct[i] = i;
		}
		
		for (int i=0; i<48; i++) {
			for (int j=0; j<48; j++) {
				for (int k=0; k<48; k++) {
					if (c.equals(d)) {
						symmult[i][j] = k;
						if (k==0) {
							syminv[i] = j;
						}
					}
					d.rot(0);
					if (k%2==1) d.rot(1);
					if (k%8==7) d.rot(2);				
					if (k%16==15) d.rot(3);
				}
				c.rot(0);
				if (j%2==1) c.rot(1);
				if (j%8==7) c.rot(2);
				if (j%16==15) c.rot(3);
			}
			c.rot(0);
			if (i%2==1) c.rot(1);
			if (i%8==7) c.rot(2);
			if (i%16==15) c.rot(3);
		}
		
		for (int i=0; i<48; i++) {
			c.set(e);
			c.rotate(syminv[i]);
			for (int j=0; j<36; j++) {
				d.set(c);
				d.move(j);
				d.rotate(i);
				for (int k=0; k<36; k++) {
					f.set(e);
					f.move(k);
					if (f.equals(d)) {
						symmove[i][j] = k;
						break;
					}
				}
			}
		}		
	}	
}
