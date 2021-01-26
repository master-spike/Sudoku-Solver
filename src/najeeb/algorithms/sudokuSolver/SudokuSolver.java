package najeeb.algorithms.sudokuSolver;

public class SudokuSolver {

	private int[] initState;

	private boolean update;

	private int[] squares;
	private int[][] blocks;
	private boolean[][] constraints;
	// private CandidateTrial[] initialTrials;

	public SudokuSolver(int[][] values) throws SudokuException {

		// Make sure the input matches the values
		if (!checkIfValidInputFormat(values)) {
			throw new SudokuException();
		}

		// An array containing the indices for all the squares for each block,
		// with each block being a row, column, or 3x3 square
		blocks = new int[27][9];
		for (int i = 0; i < 9; ++i) {
			for (int k = 0; k < 9; ++k) {
				blocks[i][k] = 9 * i + k;
			}
		}
		for (int i = 0; i < 9; ++i) {
			for (int k = 0; k < 9; ++k) {
				blocks[i + 9][k] = i + 9 * k;
			}
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				int pos = 3 * j + 27 * i;
				for (int k = 0; k < 9; ++k) {
					blocks[3 * i + j + 18][k] = pos + k % 3 + 3 * (k - k % 3);
				}
			}
		}

		constraints = new boolean[81][9];

		initState = new int[81];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				initState[9 * i + j] = values[i][j];
			}
		}

		squares = new int[81];

		for (int i = 0; i < 81; i++) {
			squares[i] = values[(i - i % 9) / 9][i % 9];
		}

	}

	public SudokuSolver(int[] values) throws SudokuException {

		// Make sure the input matches the values
		if (!checkIfValidInputFormat(values)) {
			throw new SudokuException();
		}

		// An array containing the indices for all the squares for each block,
		// with each block being a row, column, or 3x3 square
		blocks = new int[27][9];

		for (int i = 0; i < 9; ++i) {
			for (int k = 0; k < 9; ++k) {
				blocks[i][k] = 9 * i + k;
			}
		}
		for (int i = 0; i < 9; ++i) {
			for (int k = 0; k < 9; ++k) {
				blocks[i + 9][k] = i + 9 * k;
			}
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				int pos = 3 * j + 27 * i;
				for (int k = 0; k < 9; ++k) {
					blocks[3 * i + j + 18][k] = pos + k % 3 + 3 * (k - k % 3);
				}
			}
		}

		constraints = new boolean[81][9];

		initState = values.clone();
		squares = values.clone();

	}

	public int[] getInitState() {
		return initState;
	}

	public SudokuReturn solve(boolean print_data) throws SudokuException {

		if (print_data)
			printSudokuData();
		update = false;
		updateConstraints();
		updateSquares();
		while (!checkIfSolved() & update & checkIfValidSudoku()) {

			updateConstraints();
			updateSquares();

			if (print_data)
				printSudokuData();

			update = false;

		}

		if (checkIfSolved()) {
			if (print_data)
				System.out.println("Solved");
			return new SudokuReturn(0, squares);

		}

		if (!checkIfValidSudoku()) {
			if (print_data)
				System.out.println("False end");
			return new SudokuReturn(1, null);
		}

		if (print_data)
			System.out.println("Result Inconclusive");

		int ind_empty = 0;
		while (squares[ind_empty] != 0) {
			ind_empty++;
		}
		int valid_num = 0;

		for (int i = 0; i < 9; i++) {

			if (!constraints[ind_empty][i]) {
				if (print_data)
					System.out.println("Trying to insert " + (i + 1) + " in square " + ind_empty);

				int[] next_squares = squares.clone();
				next_squares[ind_empty] = i + 1;

				SudokuSolver next_solver = new SudokuSolver(next_squares);
				SudokuReturn return_data = next_solver.solve(print_data);

				if (return_data.solve == 0) {
					return return_data;
				}
			}

		}

		if (valid_num == 0)
			return new SudokuReturn(1, null);
		else {
			if (print_data)
				System.out.println("Found valid number - trying to solve");
			squares[ind_empty] = valid_num;
			return solve(true);
		}

	}

	private void updateConstraints() {
		for (int i = 0; i < 27; i++) {

			boolean[] newCtrs = { false, false, false, false, false, false, false, false, false };
			for (int j = 0; j < 9; j++) {
				if (squares[blocks[i][j]] > 0)
					newCtrs[squares[blocks[i][j]] - 1] = true;
			}

			for (int num = 0; num < 9; num++) {
				if (newCtrs[num]) {
					for (int j = 0; j < 9; j++) {
						if (!constraints[blocks[i][j]][num]) {
							constraints[blocks[i][j]][num] = true;
							update = true;
						}
					}
				}
			}
		}

	}

	void updateSquares() {
		for (int i = 0; i < 81; i++) {
			if (squares[i] == 0) {
				int n_allowed = 0;
				int num = 0;
				for (int j = 0; j < 9 & n_allowed < 2; j++) {
					if (!constraints[i][j]) {
						n_allowed++;
						num = j + 1;
					}
				}
				if (n_allowed == 1) {
					squares[i] = num;
					update = true;
				}
			}

		}

		for (int i = 0; i < 27; i++) {

			for (int n = 0; n < 9; n++) {
				int sforn = 0;
				int index = 0;
				boolean not_filled = true;
				for (int j = 0; j < 9 & not_filled; j++) {
					if (squares[blocks[i][j]] == n + 1) {
						not_filled = false;
					}
					if (!constraints[blocks[i][j]][n] & squares[blocks[i][j]] == 0) {
						sforn++;
						index = j;
					}
				}
				if (sforn == 1 & not_filled) {
					squares[blocks[i][index]] = n + 1;
				}
			}

		}

	}

	public boolean checkIfValidSudoku() {
		for (int i = 0; i < 27; i++) {
			boolean[] numbersIn = { false, false, false, false, false, false, false, false, false };
			for (int j = 0; j < 9; j++) {
				if (squares[blocks[i][j]] == 0)
					;
				else if (numbersIn[squares[blocks[i][j]] - 1]) {
					return false;
				} else
					numbersIn[squares[blocks[i][j]] - 1] = true;
			}
		}
		for (int i = 0; i < 81; i++) {
			boolean no_candidates = true;
			for (int j = 0; j < 9; j++) {
				if (constraints[i][j] == false)
					no_candidates = false;
			}
			if (no_candidates & squares[i] == 0)
				return false;
		}
		return true;
	}

	public boolean checkIfSolved() {
		for (int i = 0; i < 27; i++) {
			boolean[] numbersIn = { false, false, false, false, false, false, false, false, false };
			for (int j = 0; j < 9; j++) {
				if (squares[blocks[i][j]] == 0)
					return false;
				if (numbersIn[squares[blocks[i][j]] - 1]) {
					return false;
				}
				numbersIn[squares[blocks[i][j]] - 1] = true;
			}
		}
		return true;
	}

	private boolean checkIfValidInputFormat(int[][] ints) {
		boolean valid = true;
		for (int i = 0; i < 9 & valid; i++) {
			if (ints[i].length < 9)
				valid = false;
			for (int j = 0; j < 9; j++) {
				if (ints[i][j] < 0 | ints[i][j] > 9)
					valid = false;
			}
		}
		return valid;
	}

	private boolean checkIfValidInputFormat(int[] ints) {
		boolean valid = true;
		if (ints.length < 81)
			valid = false;
		for (int i = 0; i < 81 & valid; i++) {
			if (ints[i] < 0 | ints[i] > 9)
				valid = false;
		}
		return valid;
	}

	public int[][] getSudokuCurrent() {
		int[][] ret = new int[9][9];
		for (int i = 0; i < 81; i++) {
			ret[(i - i % 9) / 9][i % 9] = squares[i];
		}
		return ret;
	}

	public int getNumberAt(int index) {
		return squares[index];
	}

	public boolean getConstraint(int index, int number) {
		return constraints[index][number - 1];
	}

	public void printSudokuData() {
		System.out.print("\n");
		for (int i = 0; i < 9; i++) {
			System.out.print("\n");
			for (int j = 0; j < 9; j++) {
				System.out.print(squares[i * 9 + j] + " ");
			}
		}
		System.out.print("\n");
	}

}
