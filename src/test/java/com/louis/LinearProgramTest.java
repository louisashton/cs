package com.louis;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.louis.LinearProgram;

/**
 * Tests for {@link LinearProgram}.
 * 
 * @author Louis Ashton  (louisashton@live.com)
 */
public class LinearProgramTest {
	private static final double TOL = 1E-6;

	public Output fullrsm(int m, int n, double[] c, double[][] a, double[] b){
		LinearProgram lp = new LinearProgram(n,m);
		lp.chooseCosts(c);
		for (int i = 0; i < m ; i++){
			lp.addConstraint(a[i], b[i]);
		}
		lp.preparation(n,m);
		int SolveStatus = lp.Continue;
		while (SolveStatus == lp.Continue){
			SolveStatus = lp.iterate();
		}
		if (lp.ArtificialAdded == true && SolveStatus != lp.Unbounded) {
			if (lp.calculateObjective() <= 0) {
				lp.eliminateArtificialVariables();
				SolveStatus = lp.Continue;
				while (SolveStatus == lp.Continue){
					SolveStatus = lp.iterate();
				}
			} else {
				SolveStatus = lp.Infeasible;
			}
		}
		lp.showInfo();
		int result = SolveStatus;
		double z = lp.calculateObjective();
		double[] x = lp.x;
		double[] pi = lp.pi;
		return new Output(result,z,x,pi);
	}
	
	private class Output {
		int result;
		double z;
		double[] x;
		double[] pi;
		private Output(int result, double z, double[] x, double[] pi){
			this.result = result;
			this.z = z;
			this.x = x;
			this.pi = pi;
		}
	}

	@Test
	public void testOne() {
		int n = 2;
		int m = 2;
		double[] c = {1,1};
		double[] b = {9,2};
		double[][] a = {{1,0},{0,1}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(1,output.result);
		assertEquals((double)11,output.z, TOL);
	}

	@Test
	public void testTwo() {
		int n = 6;
		int m = 3;
		double[] c = {1,2,3,0,0,0};
		double[] b = {4,0,6};
		double[][] a = {{1,-1, 1, 1, 0, 0}, {1, 1, 0, 0, -1, 0}, {0, 0, 1,  0, 0, 1}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(1,output.result);
		assertEquals((double)0,output.z, TOL);
	}

	@Test
	public void testThree() {
		int n = 5;
		int m = 2;
		double[] c = {-3,-2,1,0,0};
		double[] b = {6,3};
		double[][] a = {{2,1,1,1,0},{0,1,-1,0,1}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(1,output.result);
		assertEquals((double)-10.5,output.z, TOL);
	}

	@Test
	public void testFour() {
		int n = 6;
		int m = 3;
		double[] c = {-10,-12,-12,0,0,0};
		double[] b = {20,20,20};
		double[][] a = {{1, 2, 2, 1, 0, 0},{2, 1, 2, 0, 1, 0},{2, 2, 1, 0, 0, 1}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(1,output.result);
		assertEquals((double)-136,output.z, TOL);
	}
	@Test
	public void testFive() {
		int n = 7;
		int m = 3;
		double[] c = {-19,-13,-12,-17,0,0,0};
		double[] b = {225,117,420};
		double[][] a = {{3, 2, 1, 2, 1, 0, 0},{1, 1, 1, 1, 0, 1, 0},{4, 3, 3, 4, 0, 0, 1}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(1,output.result);
		assertEquals((double)-1827,output.z, TOL);
	}

	@Test
	public void testSix() {
		int n = 5;
		int m = 3;
		double[] c = {1,2,3,0,0};
		double[] b = {4,0,6};
		double[][] a = {{1, -1, 1, 1, 0},{1, 1, 0, 0, -1},{0, 0, 1, 0,  0}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(1,output.result);
		assertEquals((double)22,output.z, TOL);
	}

	@Test
	public void testSeven() {
		int n = 5;
		int m = 3;
		double[] c = {2,3,2,0,0};
		double[] b = {4,5,7};
		double[][] a = {{1,-2,-1,0,0},{0,3,4,1,0},{0,6,1,0,1}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(1,output.result);
		assertEquals((double)8,output.z, TOL);
	}
	
	@Test
	public void testEight() {
		int n = 6;
		int m = 3;
		double[] c = {-3,-2,-4,0,0,0};
		double[] b = {4,5,7};
		double[][] a = {{1,1,2,1,0,0},{2,0,3,0,1,0},{2,1,3,0,0,1}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(1,output.result);
		assertEquals((double)-10.5,output.z, TOL);
	}
	
	@Test
	public void testNine() {
		int n = 3;
		int m = 2;
		double[] c = {-2,-3,-4};
		double[] b = {10,15};
		double[][] a = {{3,2,1},{2,5,3}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(1,output.result);
		assertEquals((double)-130/7,output.z, TOL);
	}
	@Test
	public void testTen() {
		int n = 5;
		int m = 3;
		double[] c = {2,3,0,0,0};
		double[] b = {10,4,0};
		double[][] a = {{1,0,0,0,0},{0,1,0,0,0},{0,0,1,1,1}};

		Output output = fullrsm(m,n,c,a,b);
		assertEquals(1,output.result);
		assertEquals((double)32,output.z, TOL);
	}
	@Test
	public void testEleven() {
		int n = 4;
		int m = 2;
		double[] c = {-1,-1,0,0};
		double[] b = {1,2};
		double[][] a = {{1, -1, -1, 0},{1, 1, 0, -1}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(-1,output.result);
	}

	@Test
	public void testTwelve() {
		int n = 3;
		int m = 2;
		double[] c = {-2,-3,-4};
		double[] b = {-1,-1};
		double[][] a = {{3,2,1},{2,5,3}};
		Output output = fullrsm(m,n,c,a,b);
		assertEquals(0,output.result);
	}
}
