package com.louis;

import java.awt.*;
import java.lang.Math;

/**
 * A Two-Phase Simplex method solver.
 * 
 * The Two-Phase Revised Simplex method solves a system of linear equations, A *
 * x = b. An equation in standard form is inputted from contorlling external
 * code. Variable names were specified in the assignment.
 * 
 * Author: Louis Ashton (louisashton@live.com)
 */
public class LinearProgram {
	// Type of variable.
	public final static int BasicType = 1;
	public final static int NonBasicType = 2;
	// Type of solution.
	public final static int Unbounded = -1;
	public final static int Infeasible = 0;
	public final static int Optimal = 1;
	public final static int Continue = 2;
	// Another classification of variables.
	public final static int Regular = 0;
	public final static int Artificial = 1;

	// The number of variables, as specified in the brief.
	public int n;
	// The number of constraints, as specified in the brief.
	public int m;
	// The objective function, to be minimised.
	public double[] cost;
	// The solution vector.
	public double[] x;
	// The vector of duals.
	public double[] pi;
	// The leaving variable criterion.
	public double[] BinvAs;
	// The inverse of the basic constraint coefficients.
	public Matrix Binv;
	// The basic constraint coefficients.
	public Matrix B;
	// The basic objective coefficients (basic costs).
	public double[] cB;
	public double objectiveValue = 0;
	// The constraint coefficients.
	public double[][] A;
	// The rhs of the constraints.
	public double[] b;
	// The basic variables.
	public int[] basicvars;
	// The entering variables.
	public int s;
	// The leaving variable.
	public int r;
	// The minimum ratio of the basic variables.
	public double minratio;
	// The number of non-basic variables.
	public int numNonbasic;
	// The number of constraints added to-date.
	public int constraintsAdded = 0;
	// The progress of one iteration.
	public int Progress = 0;
	public int numberOfIterations = 0;
	public int numberOfArtificialVariables;
	private int i, j, k, l;
	// The reduced costs of the non-basic variables.
	public double[] reducedCost;
	// The number of basic variables which posses the minimum ratio.
	public int numberOfMinRatioVariables;
	public int[] Nonbasicvars;
	public int[] varType;
	public double[] nonBasicCosts;
	// A permutation of vaiables that forms a full basis.
	int[] getAPerm;
	boolean ArtificialAdded = false;
	// The costs originally inputted.
	double OriginalCost[];

	/*
	 * Constructs a linear program.
	 * 
	 * @param numvar is the number of variables.
	 * 
	 * @param numcon is the number of constraints.
	 */
	public LinearProgram(int numvar, int numcon) {
		n = numvar;
		m = numcon;
		cost = new double[numvar + 2 * numcon];
		OriginalCost = new double[numvar + 2 * numcon];
		x = new double[numcon];
		pi = new double[numcon];
		BinvAs = new double[numcon];
		cB = new double[numcon];
		A = new double[numcon][numvar + 3 * numcon];
		b = new double[numcon];
		basicvars = new int[numcon];
		Binv = new Matrix(numcon);
		B = new Matrix(numcon);
		numberOfArtificialVariables = 0;
		reducedCost = new double[numvar + 2 * numcon];
		Nonbasicvars = new int[numvar + 2 * numcon];
		varType = new int[numvar + 2 * numcon];
		nonBasicCosts = new double[numcon];
	}

	/*
	 * A matrix over which Gauss-Jordanian elimination can be performed.
	 */
	private class Matrix {
		public double A[][];
		private int size;
		private double[] temporary;

		/*
		 * Constructs a square matrix of the given size.
		 * 
		 * @param size is both the height and length of the array.
		 */
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

				// Finds the maximum element in the given column.
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

			// Backwards substitution.
			for (column = size - 1; column >= 0; column--) {

				x[column] = b[column];
				for (row = size - 1; row > column; row--) {
					x[column] -= (x[row] * A[column][row]);
				}

				if (A[column][column] != 0) {
					x[column] /= A[column][column];
				}
			}
			return true;
		}
	}

	/*
	 * One iteration of the method.
	 * 
	 * @return the status of the program.
	 */
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

		for (int i = 0; i < m; i++) {
			nonBasicCosts[i] = A[i][Nonbasicvars[s]];
		}
		B.GJ(BinvAs, nonBasicCosts);

		if (!this.testUnboundedness()) {
			this.fullfindLV();
			this.gaussJordanianUpdate();

			return Continue;
		} else {
			return Unbounded;
		}
	}

	/*
	 * Updates the inverse for the constraint coefficients of the basic
	 * variables.
	 */
	private void makeBinv() {
		for (i = 0; i < m; i++) {
			cB[i] = cost[basicvars[i]];
			for (j = 0; j < m; j++) {
				Binv.A[i][j] = A[j][basicvars[i]];
			}
		}
	}

	/*
	 * Calculates the reduced cost of each non-basic variable.
	 */
	public void calculateReducedCosts() {
		for (i = 0; i < numNonbasic; i++) {
			for (j = 0; j < m; j++) {
				nonBasicCosts[j] = A[j][Nonbasicvars[i]];
			}
			reducedCost[i] = cost[Nonbasicvars[i]] - this.dotProduct(pi, nonBasicCosts, m);
		}
	}

	/*
	 * The solution is optimal if all reduced costs are positive.
	 * 
	 * @return whether the solution is optimal.
	 */
	public boolean testForOptimality() {
		boolean isOptimal = true;
		for (int i = 0; i < numNonbasic; i++)
			if (reducedCost[i] < 0) {
				isOptimal = false;
				return isOptimal;
			}
		return isOptimal;
	}

	/*
	 * The entering variable minimises the reduced costs of entering.
	 */
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

	/*
	 * The total cost is equal to the coefficients of the basic variables
	 * multiplied by their values.
	 * 
	 * @return the value of the objective.
	 */
	public double calculateObjective() {
		double z = 0;
		for (int i = 0; i < m; i++) {
			z += (x[i] * cost[basicvars[i]]);
		}
		return z;
	}

	/*
	 * Updates the constraint coefficients of the basic variables.
	 */
	private void makeB() {
		for (i = 0; i < m; i++)
			for (j = 0; j < m; j++) {
				B.A[i][j] = A[i][basicvars[j]];
			}
	}

	/*
	 * The leaving variables minimises all positive shadow prices.
	 */
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
				} else if (Ratio == minratio) {
					numberOfMinRatioVariables++;
				}
			}
		}
		r = minimumIndex;
	}

	/*
	 * Updates the inverse and solution vector through Gauss-Jordan pivoting.
	 */
	public void gaussJordanianUpdate() {
		int temporary;

		for (i = 0; i < m; i++) {
			x[i] -= (minratio * BinvAs[i]);
		}
		x[r] = minratio;

		if (varType[basicvars[r]] == Artificial) {
			numberOfArtificialVariables--;
		}
		if (varType[Nonbasicvars[s]] == Artificial) {
			numberOfArtificialVariables++;
		}

		temporary = basicvars[r];
		basicvars[r] = Nonbasicvars[s];
		Nonbasicvars[s] = temporary;

		this.makeBinv();
	}

	/*
	 * The problem is unbounded if the leaving ratios are negative.
	 * 
	 * @return true if the problem is unbounded.
	 */
	public boolean testUnboundedness() {
		boolean isUnbounded = true;
		/* If BinvAs > 0 the problem is unbounded. */
		for (i = 0; i < m; i++) {
			if (BinvAs[i] > 0) {
				isUnbounded = false;
				break;
			}
		}
		return isUnbounded;
	}

	/*
	 * Updates the objective coefficients.
	 * 
	 * @param coefficient is the costs to be added.
	 */
	public void chooseCosts(double[] coefficient) {
		for (i = 0; i < n; i++) {
			cost[i] = coefficient[i];
		}
	}

	/*
	 * Adds a constraint to the program.
	 * 
	 * @param coefficients are the values of the lhs for each variable.
	 * 
	 * @param rhs is the right hand side constant of the equality.
	 */
	public void addConstraint(double[] coefficients, double rhs) {
		for (i = 0; i < n; i++) {
			A[constraintsAdded][i] = coefficients[i];
		}
		x[constraintsAdded] = rhs;
		b[constraintsAdded] = rhs;
		constraintsAdded++;
	}

	/*
	 * Creates the first solution to the equations.
	 * 
	 * @param numberOfVariables is the length of the various arrays.
	 * 
	 * @param numberOfConstraints is the height of the constraint matix.
	 * 
	 * @return true if compelted without exception.
	 */
	public boolean preparation(int numberOfVariables, int numberOfConstraints) {
		int lastColumn, NextNonBasic;
		int[] ConstraintVariable = new int[numberOfConstraints];

		lastColumn = numberOfVariables;

		for (i = 0; i < lastColumn; i++) {
			Nonbasicvars[i] = i;
		}

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

		if (numberOfArtificialVariables > 0) {
			this.createPhaseOne();
		}

		return true;
	}

	/*
	 * Finalises the conditions for phase one.
	 * 
	 * Modifies the costs so that phase one can start.
	 */
	public void createPhaseOne() {
		double phaseCosts[] = new double[n];

		for (int i = 0; i < n; i++) {
			OriginalCost[i] = cost[i];
			if (Artificial == varType[i]) {
				phaseCosts[i] = 1;
			} else {
				phaseCosts[i] = 0;
			}
		}
		this.chooseCosts(phaseCosts);
	}

	/*
	 * Performs scalar multiplicatin of two vectors.
	 * 
	 * @param row is the first vector.
	 * 
	 * @param column is the second vector.
	 * 
	 * @param size is the minimum of the lengths of the two vectors.
	 * 
	 * @return the inner product of the two vectors.
	 */
	public double dotProduct(double[] row, double[] column, int size) {
		double result = 0;
		for (int i = 0; i < size; i++) {
			result += row[i] * column[i];
		}
		return result;
	}

	/*
	 * Prepares the system for phase two.
	 */
	public void eliminateArtificialVariables() {
		int i;
		int LastBasic = 0;
		int LastNonBasic = 0;
		double[] temporaryXvalues = new double[n];
		int artificialCount = 0;
		int ArtificialsInBasis;
		int[] BasisType = new int[n];

		for (i = 0; i < numNonbasic; i++) {
			BasisType[Nonbasicvars[i]] = NonBasicType;
		}

		for (i = 0; i < m; i++) {
			BasisType[basicvars[i]] = BasicType;
			temporaryXvalues[basicvars[i]] = x[i];
		}

		/*
		 * Moves the real basic variables to the beginning of the matrix.
		 * Artificial variables are shifted to the right. basisAugmentation will
		 * eliminate them.
		 */

		for (i = 0; i < n; i++) {
			if (varType[i] != Artificial) {
				switch (BasisType[i]) {
				case BasicType: {
					basicvars[LastBasic] = i;
					x[LastBasic] = temporaryXvalues[i];
					LastBasic++;
					break;
				}
				case NonBasicType: {
					Nonbasicvars[LastNonBasic] = i;
					LastNonBasic++;
					break;
				}
				default:
				}
			} else {
				artificialCount++;
			}
		}

		ArtificialsInBasis = 0;

		for (i = 0; i < n; i++) {
			if (varType[i] == Artificial) {
				switch (BasisType[i]) {
				case BasicType: {
					ArtificialsInBasis++;
					basicvars[LastBasic] = i;
					x[LastBasic] = temporaryXvalues[i];
					LastBasic++;
					break;
				}
				case NonBasicType: {
					Nonbasicvars[LastNonBasic] = i;
					LastNonBasic++;
					break;
				}
				default:
				}
			}
		}

		if (ArtificialsInBasis > 0) {
			basisAugmentation(m - ArtificialsInBasis);

			/* Reconstructing the index, Nonbasicvars, basicvars, and x. */

			for (i = 0; i < m; i++) {
				basicvars[i] = getAPerm[i];
			}

			for (i = 0; i < n; i++) {
				BasisType[i] = NonBasicType;
			}

			for (i = 0; i < m; i++) {
				BasisType[basicvars[i]] = BasicType;
			}

			LastBasic = 0;
			LastNonBasic = 0;

			for (i = 0; i < n; i++) {
				switch (BasisType[i]) {
				case BasicType: {
					if (varType[i] == Artificial) {
					}
					basicvars[LastBasic] = i;
					x[LastBasic] = temporaryXvalues[i];
					LastBasic++;
					break;
				}
				case NonBasicType: {
					Nonbasicvars[LastNonBasic] = i;
					LastNonBasic++;
					break;
				}
				default:
				}
			}
		}

		// Move to phase two.
		ArtificialAdded = false;

		this.chooseCosts(OriginalCost);

		numNonbasic -= artificialCount;
		n -= artificialCount;

		Progress = 0;
	}

	/*
	 * Creates a full basis.
	 * 
	 * Augments a partial basis to give us the columns required for a full
	 * implmentation, by eliminating artifical variables.
	 * 
	 * @param sizeOfBasis is the output width of the basis (the number of basic
	 * variables).
	 * 
	 * @return 0 if the basis if full, 1 if it is partial.
	 */
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
			if (isInBasis(i, sizeOfBasis, basicvars)) {
				for (j = 0; j < rows; j++) {
					basicNormal[j][lowI] = A[j][i];
				}
				getAPerm[lowI] = i;
				lowI++;
			} else {
				for (j = 0; j < rows; j++) {
					basicNormal[j][highI] = A[j][i];
				}
				getAPerm[highI] = i;
				highI--;
			}
		}
		vectorT = new double[rows];
		// Does sizeOfBasis stages of QR factorization.
		for (i = 0; i < sizeOfBasis; i++) {
			// Finds the norm of the column.
			normalEigen = 0;
			for (j = i; j < rows; j++) {
				normalEigen += basicNormal[j][i] * basicNormal[j][i];
			}
			normalEigen = (double) Math.sqrt(normalEigen);

			for (j = i; j < rows; j++) {
				vectorT[j] = basicNormal[j][i];
			}

			if (vectorT[i] < 0) {
				basicNormal[i][i] = normalEigen;
				vectorT[i] -= normalEigen;
			} else {
				basicNormal[i][i] = -normalEigen;
				vectorT[i] += normalEigen;
			}

			// Zeroes the column.
			for (j = i + 1; j < rows; j++) {
				basicNormal[j][i] = 0;
			}

			eigenOne = 0;
			for (j = i; j < rows; j++) {
				eigenOne += vectorT[j] * vectorT[j];
			}

			eigenOne = 2 / eigenOne;

			for (k = i + 1; k < cols; k++) {
				eigenTwo = 0;
				for (j = i; j < rows; j++) {
					eigenTwo += vectorT[j] * basicNormal[j][k];
				}
				eigenTwo *= eigenOne;
				for (j = i; j < rows; j++) {
					basicNormal[j][k] -= eigenTwo * vectorT[j];
				}
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
				for (j = i; j < rows; j++) {
					normalEigen += basicNormal[j][i] * basicNormal[j][i];
				}

				normalEigen = (double) Math.sqrt(normalEigen);

				for (j = i; j < rows; j++) {
					vectorT[j] = basicNormal[j][i];
				}

				if (vectorT[i] < 0) {
					basicNormal[i][i] = normalEigen;
					vectorT[i] -= normalEigen;
				} else {
					basicNormal[i][i] = -normalEigen;
					vectorT[i] += normalEigen;
				}

				for (j = i + 1; j < rows; j++) {
					basicNormal[j][i] = 0;
				}

				eigenOne = 0;
				for (j = i; j < rows; j++) {
					eigenOne += vectorT[j] * vectorT[j];
				}
				eigenOne = 2 / eigenOne;

				for (k = i + 1; k < cols; k++) {
					eigenTwo = 0;
					for (j = i; j < rows; j++) {
						eigenTwo += vectorT[j] * basicNormal[j][k];
					}
					eigenTwo *= eigenOne;
					for (j = i; j < rows; j++) {
						basicNormal[j][k] -= eigenTwo * vectorT[j];
					}
				}
			}
		}

		/*
		 * The columns required for a full basis are elements sizeOfBasis to
		 * rows-1 of getAPerm.
		 */

		return 0;
	}

	/*
	 * Searches a basis for a basic variable.
	 * 
	 * @param i is the variable.
	 * 
	 * @param sizeOfBasis si the length of the basis.
	 * 
	 * @param basis is a list of basic variables.
	 * 
	 * @return true if the variable is basic.
	 */
	boolean isInBasis(int i, int sizeOfBasis, int[] basis) {
		int j;
		for (j = 0; j < sizeOfBasis; j++) {
			if (basis[j] == i)
				return true;
		}
		return false;
	}
}
