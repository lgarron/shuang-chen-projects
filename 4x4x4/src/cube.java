package cs.eightstep;

import java.util.*;
import static cs.eightstep.Moves.*;
import static cs.eightstep.cmath.*;

public final class cube {
	int[] ep = new int[24];
	int[] ct = new int[24];
	int cparity = 0;
	int eparity = 0;
	
	private static final int[] cpmv = {1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 
										1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1};
	private static final int[] epmv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
										1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1};
	
	public cube() {
		for (int i=0; i<24; i++) {
			ep[i] = i;
			ct[i] = i / 4;
		}
	}

	public cube(cube c) {
		for (int i=0; i<24; i++) {
			this.ep[i] = c.ep[i];
			this.ct[i] = c.ct[i];
		}
		this.cparity = c.cparity;
		this.eparity = c.eparity;
	}
	
	public cube(long seed) {
		this();
		Random r = new Random(seed);
		for (int i=0; i<23; i++) {
			int t = i + r.nextInt(24-i);
			if (t != i) {
				int m = ep[i];
				ep[i] = ep[t];
				ep[t] = m;
				eparity ^= 1;
			}
		}		
		for (int i=0; i<23; i++) {
			int t = i + r.nextInt(24-i);
			if (t != i) {
				int m = ct[i];
				ct[i] = ct[t];
				ct[t] = m;
			}
		}
		cparity = r.nextInt(2);
	}
	
	public cube(int[] moveseq) {
		this();
		for (int m : moveseq) {
			move(m);
		}
	}
	
	public void set(cube c) {
		for (int i=0; i<24; i++) {
			this.ep[i] = c.ep[i];
			this.ct[i] = c.ct[i];
		}
		this.cparity = c.cparity;
		this.eparity = c.eparity;
	}
	
	public void print() {
		for (int i=0; i<24; i++) {
			System.out.print(ct[i]);
			System.out.print('\t');
		}
		System.out.println();
		for (int i=0; i<24; i++) {
			System.out.print(ep[i]);
			System.out.print('\t');
		}
		System.out.println();
	}
	
	
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


			0	1
			3	2

20	21		8	9		16	17		12	13
23	22		11	10		19	18		15	14

			4	5
			7	6
*/	
	void move(int m) {
		cparity ^= cpmv[m];
		eparity ^= epmv[m];
		int key = m % 3;
		m /= 3;
		switch (m) {
			case 0:	//U
				swap(ct, 0, 1, 2, 3, key);
				swap(ep, 0, 1, 2, 3, key);
				swap(ep, 12, 13, 14, 15, key);				
				break;
			case 1:	//R
				swap(ct, 16, 17, 18, 19, key);
				swap(ep, 11, 15, 10, 19, key);
				swap(ep, 23, 3, 22, 7, key);				
				break;
			case 2:	//F
				swap(ct, 8, 9, 10, 11, key);
				swap(ep, 0, 11, 6, 8, key);
				swap(ep, 12, 23, 18, 20, key);				
				break;
			case 3:	//D
				swap(ct, 4, 5, 6, 7, key);
				swap(ep, 4, 5, 6, 7, key);
				swap(ep, 16, 17, 18, 19, key);				
				break;
			case 4:	//L
				swap(ct, 20, 21, 22, 23, key);
				swap(ep, 1, 20, 5, 21, key);
				swap(ep, 13, 8, 17, 9, key);				
				break;
			case 5:	//B
				swap(ct, 12, 13, 14, 15, key);
				swap(ep, 2, 9, 4, 10, key);
				swap(ep, 14, 21, 16, 22, key);				
				break;
			case 6:	//u
				swap(ct, 0, 1, 2, 3, key);
				swap(ct, 8, 20, 12, 16, key);
				swap(ct, 9, 21, 13, 17, key);
				swap(ep, 0, 1, 2, 3, key);
				swap(ep, 12, 13, 14, 15, key);				
				swap(ep, 9, 22, 11, 20, key);
				break;
			case 7:	//r
				swap(ct, 16, 17, 18, 19, key);
				swap(ct, 1, 15, 5, 9, key);
				swap(ct, 2, 12, 6, 10, key);
				swap(ep, 11, 15, 10, 19, key);
				swap(ep, 23, 3, 22, 7, key);				
				swap(ep, 2, 16, 6, 12, key);
				break;
			case 8:	//f
				swap(ct, 8, 9, 10, 11, key);
				swap(ct, 2, 19, 4, 21, key);
				swap(ct, 3, 16, 5, 22, key);
				swap(ep, 0, 11, 6, 8, key);
				swap(ep, 12, 23, 18, 20, key);				
				swap(ep, 3, 19, 5, 13, key);
				break;
			case 9:	//d
				swap(ct, 4, 5, 6, 7, key);
				swap(ct, 10, 18, 14, 22, key);
				swap(ct, 11, 19, 15, 23, key);
				swap(ep, 4, 5, 6, 7, key);
				swap(ep, 16, 17, 18, 19, key);				
				swap(ep, 8, 23, 10, 21, key);
				break;
			case 10://l
				swap(ct, 20, 21, 22, 23, key);
				swap(ct, 0, 8, 4, 14, key);
				swap(ct, 3, 11, 7, 13, key);
				swap(ep, 1, 20, 5, 21, key);
				swap(ep, 13, 8, 17, 9, key);				
				swap(ep, 14, 0, 18, 4, key);
				break;
			case 11://b
				swap(ct, 12, 13, 14, 15, key);
				swap(ct, 1, 20, 7, 18, key);
				swap(ct, 0, 23, 6, 17, key);
				swap(ep, 2, 9, 4, 10, key);
				swap(ep, 14, 21, 16, 22, key);				
				swap(ep, 7, 15, 1, 17, key);
				break;		
		}
	}	
}
