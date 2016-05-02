package com.louis;

import java.awt.*;
import java.lang.Math;

/**
 * Solves a linear problem using the Two-Phase Simplex Method.
 * 
 * Author: Louis Ashton (louisashton@live.com)
 */
public class LinearProgram {
	public final static int BasicType = 1;
	public final static int NonBasicType = 2;
	public final static int Unbounded = -1;
	public final static int Infeasible = 0;
	public final static int Optimal = 1;
	public final static int Continue = 2;
	public final static int Regular = 0;
	public final static int Artificial = 1;
	public int n;
	public int m;
	public double[] cost;
	public double[] x;
	public double[] pi;
	public double[] BinvAs;
	public Matrix Binv;
	public Matrix B;
	public double[] cB;
	public double objectiveValue = 0;
	public double[][] A;
	public double[] b;
	public int[] basicvars;
	public int s;
	public int r;
	public double minratio;
	public int numNonbasic;
	public int constraintsAdded = 0;
	public int Progress = 0;
	public int numberOfIterations = 0;
	public int numberOfArtificialVariables;
	private int i, j, k, l;
	public double[] reducedCost;
	public int numberOfMinRatioVariables;
	public int[] Nonbasicvars;
	public int[] varType;
	public double[] columnInA;
	int[] getAPerm;
	boolean ArtificialAdded = false;
	double OriginalCost[];

	public LinearProgram(int numvar, int numcol) {
		n = numvar;
		m = numcol;
		cost = new double[numvar + 2 * numcol];
		OriginalCost = new double[numvar + 2 * numcol];
		x = new double[numcol];
		pi = new double[numcol];
		BinvAs = new double[numcol];
		cB = new double[numcol];
		A = new double[numcol][numvar + 3 * numcol];
		b = new double[numcol];
		basicvars = new int[numcol];
		Binv = new Matrix(numcol);
		B = new Matrix(numcol);
		numberOfArtificialVariables = 0;
		reducedCost = new double[numvar + 2 * numcol];
		Nonbasicvars = new int[numvar + 2 * numcol];
		varType = new int[numvar + 2 * numcol];
		columnInA = new double[numcol];
	}

	private class Matrix {
		public double A[][];
		private int size;
		private double[] temporary;

		public Matrix(int size) {
			A = new double[size][size];
			temporary = new double[size];
			this.size = size;
		}

		/*
		 * Gauss-Jordan pivoting.
		 */
		public boolean GJ(double[] x, double[] b) {
			int row, column, k, i;
			int swap;
			double max;
			double scale, temporary;

			// Forward elimination.

			for (column = 0; column < size - 1; column++) {

				/* Finds the maximum element in column column. */

				max = Math.abs(A[column][column]);
				swap = column;
				for (i = column + 1; i < size; i++)
					if (Math.abs(A[i][column]) > max) {
						max = Math.abs(A[i][column]);
						swap = i;
					}

				// Exchanges rows "swap" and "column".

				if (swap != column) {
					temporary = b[swap];
					b[swap] = b[column];
					b[column] = temporary;
					for (i = 0; i < size; i++) {
						temporary = A[swap][i];
						A[swap][i] = A[column][i];
						A[column][i] = temporary;
					}
				}

				// Adds multiples of the pivot row to each row.

				if (A[column][column] != 0)
					for (row = column + 1; row < size; row++) {
						scale = A[row][column] / A[column][column];
						b[row] -= (scale * b[column]);
						for (k = column; k < size; k++) {
							A[row][k] -= (scale * A[column][k]);
						}
					}
			}

			// Backwards substitution

			for (column = size - 1; column >= 0; column--) {

				x[column] = b[column];
				for (row = size - 1; row > column; row--)
					x[column] -= (x[row] * A[column][row]);

				if (A[column][column] != 0)
					x[column] /= A[column][column];
			}
			return true;
		}
	}

	public int iterate() {

		numberOfIterations++;
		this.makeBinv();
		Binv.GJ(pi, cB);
		this.calculateReducedCosts();

		if (!this.testForOptimality()) {
			this.fullfindEV();
		} else {
			objectiveValue = this.calculateObjective();
			return Optimal;
		}
		this.makeB();

		for (int i = 0; i < m; i++)
			columnInA[i] = A[i][Nonbasicvars[s]];

		B.GJ(BinvAs, columnInA);

		if (!this.testUnboundedness()) {
			this.fullfindLV();
			this.GJupdate();

			return Continue;
		} else
			return Unbounded;
	}

	private void makeBinv() {
		for (i = 0; i < m; i++) {
			cB[i] = cost[basicvars[i]];
			for (j = 0; j < m; j++)
				Binv.A[i][j] = A[j][basicvars[i]];
		}
	}

	public void calculateReducedCosts() {
		for (i = 0; i < numNonbasic; i++) {
			for (j = 0; j < m; j++)
				columnInA[j] = A[j][Nonbasicvars[i]];
			reducedCost[i] = cost[Nonbasicvars[i]] - this.dotProduct(pi, columnInA, m);
		}
	}

	public boolean testForOptimality() {
		boolean isOptimal = true;
		for (int i = 0; i < numNonbasic; i++)
			if (reducedCost[i] < 0) {
				isOptimal = false;
				return isOptimal;
			}
		return isOptimal;
	}

	public void fullfindEV() {
		int minimumIndex = 0;
		double minValue = 100000;
		for (i = 0; i < numNonbasic; i++)
			if (reducedCost[i] < 0 && reducedCost[i] < minValue) {
				minimumIndex = i;
				minValue = reducedCost[i];
			}
		s = minimumIndex;
	}

	public double calculateObjective() {
		double z = 0;
		for (int i = 0; i < m; i++)
			z += (x[i] * cost[basicvars[i]]);
		return z;
	}

	private void makeB() {
		for (i = 0; i < m; i++)
			for (j = 0; j < m; j++)
				B.A[i][j] = A[i][basicvars[j]];
	}

	public void fullfindLV() {
		double Ratio;
		int minimumIndex = -1;
		numberOfMinRatioVariables = 0;

		for (i = 0; i < m; i++) {
			if (BinvAs[i] > 0) {
				Ratio = x[i] / BinvAs[i];
				if (numberOfMinRatioVariables == 0) {
					minratio = Ratio;
					minimumIndex = i;
					numberOfMinRatioVariables = 1;
				} else if (Ratio < minratio) {
					minratio = Ratio;
					minimumIndex = i;
					numberOfMinRatioVariables = 1;
				} else if (Ratio == minratio)
					numberOfMinRatioVariables++;
			}
		}
		r = minimumIndex;
	}

	public void GJupdate() {
		int temporary;

		for (i = 0; i < m; i++)
			x[i] -= (minratio * BinvAs[i]);
		x[r] = minratio;

		if (varType[basicvars[r]] == Artificial)
			numberOfArtificialVariables--;
		if (varType[Nonbasicvars[s]] == Artificial)
			numberOfArtificialVariables++;

		temporary = basicvars[r];
		basicvars[r] = Nonbasicvars[s];
		Nonbasicvars[s] = temporary;

		this.makeBinv();
	}

	public boolean testUnboundedness() {
		boolean isUnbounded = true;
		/* If BinvAs > 0 the problem is unbounded. */
		for (i = 0; i < m; i++)
			if (BinvAs[i] > 0) {
				isUnbounded = false;
				break;
			}
		return isUnbounded;
	}

	public void chooseCosts(double[] coefficient) {
		for (i = 0; i < n; i++)
			cost[i] = coefficient[i];
	}

	public void addConstraint(double[] coefficients, double rhs) {
		for (i = 0; i < n; i++) {
			A[constraintsAdded][i] = coefficients[i];
		}
		x[constraintsAdded] = rhs;
		b[constraintsAdded] = rhs;
		constraintsAdded++;
	}

	public boolean preparation(int numberOfVariables, int numberOfConstraints) {
		int lastColumn, NextNonBasic;
		int[] ConstraintVariable = new int[numberOfConstraints];

		lastColumn = numberOfVariables;

		for (i = 0; i < lastColumn; i++)
			Nonbasicvars[i] = i;

		NextNonBasic = lastColumn;

		// Creates the basis with artificial variables, if required.

		for (i = 0; i < numberOfConstraints; i++) {
			if (b[i] >= 0) {
				A[i][lastColumn] = 1;
				x[i] = b[i];
			} else { /* b[i] < 0 */
				A[i][lastColumn] = -1;
				x[i] = -b[i];
			}

			varType[lastColumn] = Artificial;
			basicvars[i] = lastColumn;
			numberOfArtificialVariables++;
			lastColumn++;
			ArtificialAdded = true;
		}

		numNonbasic = lastColumn - m;
		n = lastColumn;

		if (numberOfArtificialVariables > 0)
			this.createPhaseOne();

		return true;
	}

	public void createPhaseOne() {
		double phaseCosts[] = new double[n];

		for (int i = 0; i < n; i++) {
			OriginalCost[i] = cost[i];
			if (Artificial == varType[i])
				phaseCosts[i] = 1;
			else
				phaseCosts[i] = 0;
		}
		this.chooseCosts(phaseCosts);
	}

	public double dotProduct(double[] row, double[] column, int size) {
		double result = 0;
		for (int i = 0; i < size; i++)
			result += row[i] * column[i];
		return result;
	}

	public void showInfo() {
		for (int j = 0; j < n; j++)
			System.out.println("cost[" + j + "]:" + cost[j]);
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				System.out.println("A[" + i + "][" + j + "]:" + A[i][j]);

		System.out.println("r:" + r);

		System.out.println("s:" + s);

		for (int i = 0; i < m; i++)
			System.out.println("cB[" + i + "]:" + cB[i]);

		System.out.println("minratio:" + minratio);

		for (int i = 0; i < m; i++)
			System.out.println("pi[" + i + "]:" + pi[i]);

		for (int i = 0; i < m; i++)
			System.out.println("BinvAs[" + i + "]:" + BinvAs[i]);

		for (int i = 0; i < m; i++)
			System.out.println("basicvars[" + i + "]:" + basicvars[i]);

		for (int i = 0; i < numNonbasic; i++)
			System.out.println("Nonbasicvars[" + i + "]:" + Nonbasicvars[i]);

		for (int i = 0; i < m; i++)
			System.out.println("x[" + i + "]:" + x[i]);

		for (int i = 0; i < numNonbasic; i++)
			System.out.println("reducedCost[" + i + "]:" + reducedCost[i]);

		System.out.println("objectiveValue:" + objectiveValue);

		for (int i = 0; i < n; i++)
			System.out.println("OriginalCost[" + i + "]:" + OriginalCost[i]);

	}

	public void eliminateArtificialVariables() {
		int i;
		int LastBasic = 0;
		int LastNonBasic = 0;
		double[] temporaryXvalues = new double[n];
		int artificialCount = 0;
		int ArtificialsInBasis;
		int[] BasisType = new int[n];

		for (i = 0; i < numNonbasic; i++)
			BasisType[Nonbasicvars[i]] = NonBasicType;

		for (i = 0; i < m; i++) {
			BasisType[basicvars[i]] = BasicType;
			temporaryXvalues[basicvars[i]] = x[i];
		}

		/*
		 * Moves the real basic variables to the beginning of the matrix.
		 * Artificial variables are shifted to the right. basisAugmentation will
		 * eliminate them.
		 */

		for (i = 0; i < n; i++)
			if (varType[i] != Artificial) {
				switch (BasisType[i]) {
				case BasicType:
					basicvars[LastBasic] = i;
					x[LastBasic] = temporaryXvalues[i];
					LastBasic++;
					break;
				case NonBasicType:
					Nonbasicvars[LastNonBasic] = i;
					LastNonBasic++;
					break;
				default:
				}
			} else
				artificialCount++;

		ArtificialsInBasis = 0;

		for (i = 0; i < n; i++)
			if (varType[i] == Artificial) {
				switch (BasisType[i]) {
				case BasicType:
					ArtificialsInBasis++;
					basicvars[LastBasic] = i;
					x[LastBasic] = temporaryXvalues[i];
					LastBasic++;
					break;
				case NonBasicType:
					Nonbasicvars[LastNonBasic] = i;
					LastNonBasic++;
					break;
				default:
				}
			}

		if (ArtificialsInBasis > 0) {
			basisAugmentation(m - ArtificialsInBasis);

			/* Reconstructing the index, Nonbasicvars, basicvars, and x. */

			for (i = 0; i < m; i++) {
				basicvars[i] = getAPerm[i];
			}

			for (i = 0; i < n; i++)
				BasisType[i] = NonBasicType;

			for (i = 0; i < m; i++)
				BasisType[basicvars[i]] = BasicType;

			LastBasic = 0;
			LastNonBasic = 0;

			for (i = 0; i < n; i++)
				switch (BasisType[i]) {
				case BasicType:
					if (varType[i] == Artificial) {
					}
					basicvars[LastBasic] = i;
					x[LastBasic] = temporaryXvalues[i];
					LastBasic++;
					break;
				case NonBasicType:
					Nonbasicvars[LastNonBasic] = i;
					LastNonBasic++;
					break;
				default:
				}
		}

		// Move to phase 2
		ArtificialAdded = false;

		this.chooseCosts(OriginalCost);

		numNonbasic -= artificialCount;
		n -= artificialCount;

		Progress = 0;

	}

	//Augments a partial basis to give us the columns required for a full implmentation, by eliminating artifical variables.
	public int basisAugmentation(int sizeOfBasis) {
		int i, j, k, highI, lowI, temporaryI;
		double[][] basicNormal;
		int rows, cols;
		double normalEigen, eigenOne, eigenTwo, temporaryD;
		double[] vectorT;

		rows = m;
		cols = n;

		if (rows > cols) {
			return 1;
		}
		if (sizeOfBasis > rows) {
			return 1;
		}
		if (sizeOfBasis < 0 || rows <= 0 || cols <= 0) {
			return 1;
		}
		for (i = 0; i < sizeOfBasis; i++) {
			if (basicvars[i] < 0 || basicvars[i] >= cols) {
				return 1;
			}
		}
		if (sizeOfBasis == rows) {
			return 0;
		}

		getAPerm = new int[cols];
		basicNormal = new double[rows][cols];

		/*
		 * Transfer columns of A to basicNormal. The current basis is in the
		 * first sizeOfBasis locations. The remaining columns are in the last
		 * cols-sizeOfBasis locations. getAPerm keeps track of the permutations.
		 */

		lowI = 0;
		highI = cols - 1;
		for (i = 0; i < cols; i++) {
			if (inbasis(i, sizeOfBasis, basicvars)) {
				for (j = 0; j < rows; j++)
					basicNormal[j][lowI] = A[j][i];
				getAPerm[lowI] = i;
				lowI++;
			} else {
				for (j = 0; j < rows; j++)
					basicNormal[j][highI] = A[j][i];
				getAPerm[highI] = i;
				highI--;
			}
		}
		vectorT = new double[rows];
		// Does sizeOfBasis stages of QR factorization.
		for (i = 0; i < sizeOfBasis; i++) {
			// Finds the norm of the column.
			normalEigen = 0;
			for (j = i; j < rows; j++)
				normalEigen += basicNormal[j][i] * basicNormal[j][i];
			normalEigen = (double) Math.sqrt(normalEigen);

			for (j = i; j < rows; j++)
				vectorT[j] = basicNormal[j][i];

			if (vectorT[i] < 0) {
				basicNormal[i][i] = normalEigen;
				vectorT[i] -= normalEigen;
			} else {
				basicNormal[i][i] = -normalEigen;
				vectorT[i] += normalEigen;
			}

			// Zeroes the column.
			for (j = i + 1; j < rows; j++)
				basicNormal[j][i] = 0;

			eigenOne = 0;
			for (j = i; j < rows; j++)
				eigenOne += vectorT[j] * vectorT[j];

			eigenOne = 2 / eigenOne;

			for (k = i + 1; k < cols; k++) {
				eigenTwo = 0;
				for (j = i; j < rows; j++)
					eigenTwo += vectorT[j] * basicNormal[j][k];
				eigenTwo *= eigenOne;
				for (j = i; j < rows; j++)
					basicNormal[j][k] -= eigenTwo * vectorT[j];
			}
		}

		/*
		 * Finds m-sizeOfBasis columns by choosing the largest pivot in each
		 * row.
		 */

		for (i = sizeOfBasis; i < rows; i++) {

			// Finds the column with largest pivot in this row.
			eigenOne = Math.abs(basicNormal[i][i]);
			highI = i;
			for (j = i + 1; j < cols; j++) {
				if (Math.abs(basicNormal[i][j]) > eigenOne) {
					eigenOne = Math.abs(basicNormal[i][j]);
					highI = j;
				}
			}
			// Swaps the column highI with this column.
			temporaryI = getAPerm[i];
			getAPerm[i] = getAPerm[highI];
			getAPerm[highI] = temporaryI;

			for (j = 0; j < rows; j++) {
				temporaryD = basicNormal[j][i];
				basicNormal[j][i] = basicNormal[j][highI];
				basicNormal[j][highI] = temporaryD;
			}

			// Same again if we're not at stage "rows" yet.

			if (i < rows - 1) {

				normalEigen = 0;
				for (j = i; j < rows; j++)
					normalEigen += basicNormal[j][i] * basicNormal[j][i];

				normalEigen = (double) Math.sqrt(normalEigen);

				for (j = i; j < rows; j++)
					vectorT[j] = basicNormal[j][i];

				if (vectorT[i] < 0) {
					basicNormal[i][i] = normalEigen;
					vectorT[i] -= normalEigen;
				} else {
					basicNormal[i][i] = -normalEigen;
					vectorT[i] += normalEigen;
				}

				for (j = i + 1; j < rows; j++)
					basicNormal[j][i] = 0;

				eigenOne = 0;
				for (j = i; j < rows; j++)
					eigenOne += vectorT[j] * vectorT[j];
				eigenOne = 2 / eigenOne;

				for (k = i + 1; k < cols; k++) {
					eigenTwo = 0;
					for (j = i; j < rows; j++)
						eigenTwo += vectorT[j] * basicNormal[j][k];
					eigenTwo *= eigenOne;
					for (j = i; j < rows; j++)
						basicNormal[j][k] -= eigenTwo * vectorT[j];
				}
			}
		}

		/*
		 * The columns required for a full basis are elements sizeOfBasis to rows-1 of getAPerm.
		 */

		return 0;
	}

	boolean inbasis(int i, int sizeOfBasis, int[] basis) {
		int j;
		for (j = 0; j < sizeOfBasis; j++) {
			if (basis[j] == i)
				return true;
		}
		return false;
	}
}
