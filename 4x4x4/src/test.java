package cs.eightstep;

import static cs.eightstep.Moves.*;
import static cs.eightstep.cmath.*;
import static cs.eightstep.center2.rlmv;
import static cs.eightstep.center2.ctmv;
import static cs.eightstep.center2.ctprun;
import static cs.eightstep.center2.rlrot;
import static cs.eightstep.edge2.esmv;
import static cs.eightstep.center.symmult;
import static cs.eightstep.center.ctsmv;
import static cs.eightstep.center.csprun;
import static cs.eightstep.center.symmove;
import static cs.eightstep.edge2.eprun;

import java.io.*;
import java.util.*;

public final class test implements Runnable {
	static int[] count = new int[1];

	int[] move1 = new int[15];
	int[] move2 = new int[20];
	int[] move3 = new int[20];
	int totallength = 999;
	int length1 = 0;
	int length2 = 0;
	int maxlength2;	
	boolean add1 = false;
	int in2 = 0;
	int[][] edges = new int[15][];
	int[][] syms = new int[15][];
	public cube c;
	cube c1 = new cube();
	cube c2 = new cube();
	center2 ct2 = new center2();
	center4 ct4 = new center4();
	edge12 e12 = new edge12();
	edge12[] tempe = new edge12[20];

	int valid1 = 0;
	public String solution = "";
	public long endtime;

	public test() {
		edges[0] = new int[1024];
		syms[0] = new int[1024];
		for (int i=0; i<20; i++) {
			tempe[i] = new edge12();
		}
	}
	
	public static String tostr(int[] moves) {
		StringBuilder s = new StringBuilder();
		for (int m : moves) {
			s.append(move2str[m]);
		}
		
		s.append(String.format(" (%d moves)", moves.length));
		return s.toString();
	}
	
	public static int[] tomove(String s) {
		s = s.replaceAll("\\s", "");
		int[] arr = new int[s.length()];
		int j = 0;
		for (int i=0, length=s.length(); i<length; i++) {
			int axis = -1;
			switch (s.charAt(i)) {
				case 'U':	axis = 0;	break;
				case 'R':	axis = 1;	break;
				case 'F':	axis = 2;	break;
				case 'D':	axis = 3;	break;
				case 'L':	axis = 4;	break;
				case 'B':	axis = 5;	break;
				case 'u':	axis = 6;	break;
				case 'r':	axis = 7;	break;
				case 'f':	axis = 8;	break;
				case 'd':	axis = 9;	break;
				case 'l':	axis = 10;	break;
				case 'b':	axis = 11;	break;
				default:	continue;
			}
			axis *= 3;
			if (++i<length) {
				switch (s.charAt(i)) {
					case '2':	axis++;		break;
					case '\'':	axis+=2;	break;
					default:	--i;
				}
			}
			arr[j++] = axis;
		}
		
		int[] ret = new int[j];
		while (--j>=0) {
			ret[j] = arr[j];
		}
		return ret;
	}
	
	static class gogogo extends Thread {
		private test me = null;
		gogogo(test me) {
			this.me = me;
		}
		public void run() {
			for (int t=0; t<10000000; t++) {
				StringBuilder sb = new StringBuilder();
				long time = System.nanoTime();
				int[] moveseq = new int[40];
				Random r = new Random();
				int lm = 36;
				for (int i=0; i<40; i++) {
					int m = r.nextInt(27);
					while (ckmv[lm][m]) {
						m = r.nextInt(27);
					}
					moveseq[i] = m;
					lm = m;
					sb.append(move2str[moveseq[i]]);
					sb.append(' ');
				}
				sb.append('\n');
//				me.calc(moveseq);
				me.c = new cube(r.nextLong());
				Thread ca = new Thread(me);
				me.endtime = System.currentTimeMillis() + 200000;
				ca.start();
				try {
					ca.join();
				} catch (Exception e) {}
				sb.append(me.solution);
//				sb.append(String.format("%d   ", me.totallength));
				sb.append(System.nanoTime() - time);
				synchronized (test.count) {
					System.out.print(test.count[0]++);
					System.out.print('\t');
					System.out.println(sb.toString());
				}
			}
		}
	}

	public static void main(String[] args) {
		load();
		test first = new test();
//		test second = new test();
		new gogogo(first).start();
//		new gogogo(second).start();
	}	
	
	static {
		load();
	}
	
	public String solve(String scramble) {
		int[] moveseq = tomove(scramble);
		return solve(moveseq);
	}
	
	public String solve(int[] moveseq) {
		calc(moveseq);
		return solution;	
	}
	
	static void load() {
		edge12 e12 = new edge12();
		edge2 e = new edge2();
		center c = new center();
		center2 c2 = new center2();
		center4 c4 = new center4();
		cube cu = new cube();
	}
	
	public void calc(long seed) {
	    c = new cube(seed);
	    run();
	}
	
	public void calc(int[] moveseq) {
		c = new cube(moveseq);
		run();
	}
	
	public void run() {
		solution = "";
		int ud = new center(c, 0).getsym();
		int fb = new center(c, 1).getsym();
		int rl = new center(c, 2).getsym();
		edge2 et = new edge2();
		for (int i=0; i<1024; i++) {
			edges[0][i] = et.getsymidx(c.ep, i);
			syms[0][i] = edges[0][i] & 0x0f;
			edges[0][i] >>>= 4;
		}
		
		totallength = 28;
		in2 = 0;
		valid1 = 0;
		try {
			for (length1=0; length1<100; length1++) {
				if (search1(rl>>>6, rl&0x3f, length1, 36, 0) 
					|| search1(ud>>>6, ud&0x3f, length1, 36, 0)
					|| search1(fb>>>6, fb&0x3f, length1, 36, 0))
						break;
			}
		} catch (RuntimeException e) {
		}
	}
	
	public void calc(cube s) {
		c = s;
		run();
	}

	public final boolean search1(int ct, int sym, int maxl, int lm, int depth) {
		if (ct==0) {
			if (maxl != 0) {
				return false;
			}
			return init2(sym, lm);
		} else {
			if (csprun[ct] > maxl) {
				return false;
			}
			for (int m=0; m<27; m++) {
				if (ckmv[lm][m]) {
					continue;
				}
				int ctx = ctsmv[ct][symmove[sym][m]];
				int symx = symmult[sym][ctx&0x03f];
				ctx>>>=6;
				move1[depth] = m;
				valid1 = Math.min(valid1, depth);
				if (search1(ctx, symx, maxl-1, m, depth+1)) {
					return true;
				}
			}
		}
		return false;
	}		
	
	final boolean init2(int sym, int lm) {
		if (++in2>= 100000 || ((System.currentTimeMillis()>endtime) && !solution.equals(""))) {
			return true;
		}
		
		if (in2 % 1000 == 0)
			System.out.print(String.format("%d\r", in2/1000));


//		System.out.print("\r=");

		c1.set(c);
		for (int i=0; i<length1; i++) {
			c1.move(move1[i]);
		}

//		System.out.print("\r0");

		switch (center.finish[sym]) {
			case 0 :
				c1.move(fx1);
				c1.move(bx3);
				move1[length1] = fx1;
				move1[length1+1] = bx3;
				add1 = true;
				sym = 21;
				break;
			case 12869 :
				c1.move(ux1);
				c1.move(dx3);
				move1[length1] = ux1;
				move1[length1+1] = dx3;
				add1 = true;
				sym = 36;
				break;
			case 735470 : 
				add1 = false;
		}
		ct2.set(c1);
		int s2ct = ct2.getct();
		int s2rl = ct2.getrl();
		int ctp = ctprun[s2ct*70+s2rl];
		maxlength2 = Math.min(totallength-11-length1, 9);

		if (ctp >= maxlength2) {
//		System.out.print("\r-");
			return false;
		}

//		System.out.print("\r1");

		int l = add1 ? length1 + 2 : length1;
		for (int i=valid1; i<l; i++) {
			if (edges[i+1] == null) {
				edges[i+1] = new int[1024];
				syms[i+1] = new int[1024];
			}
		
//			int[] ed = edges[i+1];
//			int[] syo = syms[i];
			int m = move1[i];
			for (int j=0; j<1024; j++) {
				edges[i+1][j] = esmv[edges[i][j]][symmove[syms[i][j]][m]];
				syms[i+1][j] = symmult[syms[i][j]][edges[i+1][j]&0x0f];
				edges[i+1][j] >>>= 4;
			}
		}
		valid1 = length1;

//		System.out.print("\r2");

		for (length2=ctp; length2<maxlength2; length2++) {
			for (int k=0; k<1024; k++) {
				if (search2(edges[l][k], s2ct, s2rl, syms[l][k], length2, 28, 0)) {
					return false;
				}
			}
		}

//		System.out.print("\r-");

		return false;
	}
	
	public final boolean search2(int edge, int ct, int rl, int sym, int maxl, int lm, int depth) {
		int prun1 = eprun[edge * 70 + rlrot[rl][sym]];
		if (prun1==0 && ct==0) {
			if (maxl != 0) {
				return false;
			}
			init3();
		} else if (prun1 <= maxl && ctprun[ct * 70 + rl] <= maxl) {
			int[] cto = ctmv[ct];
			int[] rlo = rlmv[rl];
			int[] eo = esmv[edge];
			int[] svo = symmove[sym];
			int[] suo = symmult[sym];
		
			for (int m=0; m<23; m++) {
				if (ckmv2[lm][m]) {
					continue;
				}
				int mt = move2std[m];
				int edgex = eo[svo[mt]];
				int symx = suo[edgex&0x0f];
				edgex >>>= 4;
//				int ctx = ctmv[ct][m];
//				int rlx = rlmv[rl][m];
				move2[depth] = mt;
				if (search2(edgex, cto[m], rlo[m], symx, maxl-1, m, depth+1)) {
					return true;
				}
			}
		}
		return false;
	}	

	final boolean init3() {
//		System.out.print("\r3");
		c2.set(c1);
		for (int i=0; i<length2; i++) {
			c2.move(move2[i]);
		}
		int eparity = e12.set(c2);
		ct4.set(c2, eparity);
		int ct = ct4.getct();
		int edge = e12.get();
		int length = -1;
		int maxlen3 = totallength-length1-length2;
		int prun = e12.getprun(edge);
		for (int i=prun; i<maxlen3; i++) {
			if (search4(edge, ct, prun, i, 20, 0)) {
				length = i;
				break;
			}
		}
		if (length != -1) {
			StringBuffer str = new StringBuffer();
			
			str.append("Step 1 : ");
			for (int i=0; i<length1; i++) {
				str.append(move2str[move1[i]]);
				str.append(' ');
			}
			if (add1) {
				str.append('[');
				str.append(move2str[move1[length1]]);
				str.append(' ');
				str.append(move2str[move1[length1+1]]);
				str.append(']');
			}
			str.append(String.format(" (%d moves) \n", length1));
			str.append("Step 2 : ");
			for (int i=0; i<length2; i++) {
				str.append(move2str[move2[i]]);
				str.append(' ');
			}
			str.append(String.format(" (%d moves) \n", length2));
			str.append("Step 3 : ");
			for (int i=0; i<length; i++) {
				str.append(move2str[move3std[move3[i]]]);
				str.append(' ');
			}			
			str.append(String.format(" (%d moves) \n", length));
			str.append(String.format("Total  : %d moves\n", length1 + length2 + length));
			synchronized (solution) {
				solution = str.toString();
			}
		
			totallength = length1 + length2 + length;
			maxlength2 = Math.min(totallength-11-length1, 9);
/*			System.out.print(in2);
			System.out.print('\t');
			System.out.print(String.format("%d\t%d\t%d\t%d\t", totallength, length1, length2, length));

			for (int i=0; i<length1; i++) {
				System.out.print(move2str[move1[i]]);
				System.out.print(' ');
			}
			System.out.print('.');
			if (add1) {
				System.out.print(move2str[move1[length1]]);
				System.out.print(' ');
				System.out.print(move2str[move1[length1+1]]);
				System.out.print('.');			
			}
			for (int i=0; i<length2; i++) {
				System.out.print(move2str[move2[i]]);
				System.out.print(' ');
			}
			System.out.print(".");
			for (int i=0; i<length; i++) {
				System.out.print(String.format("%s ", move2str[move3std[move3[i]]]));		
			}
			System.out.println();
//			if (totallength <= 26)
//				throw new RuntimeException();
*/			return true;
		}
//		System.out.print("\r2");
		return false;
	}
	
	public final boolean search4(int edge, int ct, int prun, int maxl, int lm, int depth) {
		if (maxl == 0) {
			if (edge==0 && ct==0) {
				return true;
			}
		} else {
			int prunx = edge12.getprun(edge, prun);
		
			if (prunx > maxl || center4.prun[ct] > maxl) {
				return false;
			}
			
			tempe[depth].set(edge);
		
			for (int m=0; m<17; m++) {
				if (ckmv3[lm][m]) {
					continue;
				}
				
				move3[depth] = m;
				int ctx = center4.ctmove[ct][m];
				int edgex = edge12.getmv(tempe[depth].edge, m);

				if (search4(edgex, ctx, prunx, maxl-1, m, depth+1)) {
					return true;
				}
			}
		}
		return false;
	}
}
