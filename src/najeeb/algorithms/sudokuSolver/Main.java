package najeeb.algorithms.sudokuSolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import najeeb.algorithms.sudokuSolver.SudokuException;
import najeeb.algorithms.sudokuSolver.SudokuSolver;

public class Main {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		String path = sc.nextLine();
		SudokuSolver sudokuSolver;
		sc.close();
		int[][] data = null;
		try {
			data = fileToSudokuData(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			printIntData(data);
			 
			sudokuSolver = new SudokuSolver(data);
			System.out.println("Already solved: " + sudokuSolver.checkIfSolved());
			long t1 = System.nanoTime();
			SudokuReturn out_data = sudokuSolver.solve(true);
			System.out.println("Final result valid: " + sudokuSolver.checkIfValidSudoku());
			long t2 = System.nanoTime();
			long time = t2 - t1;
			System.out.println("Time elapsed: " + time + "ns");
			
			System.out.println("");
			if (out_data.solve == 0) System.out.println("Valid solution");
			if (out_data.solve == 1) System.out.println("Inalid solution");
			if (out_data.solve == 2) System.out.println("Solution not found"); 
			printIntData(out_data.data);
			
			
		} catch (SudokuException e) {
			e.printStackTrace();
		}
		
		
	}

	static int[][] fileToSudokuData(String path) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("input/" + path));

		// Take the first 9 rows from the file as a string
		// Place them successively into a 1d string array
		String[] rows = new String[9];
		try {

			for (int i = 0; i < 9; i++)
				rows[i] = br.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}

		// Split the string of each row into its own array
		// Put each line of strings into 2d string array "digits"
		String[][] digits = new String[9][];
		for (int i = 0; i < 9; i++) {
			digits[i] = rows[i].split(" ");
		}

		/*
		 * Checks all values in a 9x9 in the "upper-left" of digits and for each
		 * spot, if a valid integer is found in that spot, places it in the 2-d
		 * integer array "numbers" at the same coordinate
		 */
		int[][] numbers = new int[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				try {
					numbers[i][j] = Integer.parseInt((digits[i][j]), 10);
				} catch (ArrayIndexOutOfBoundsException e) {
					numbers[i][j] = 10;
				} catch (NumberFormatException f) {
				}
			}
		}

		return numbers;

	}

	public static void printIntData(int[][] data) {
		for (int[] ln : data) {
			for (int n : ln) {
				System.out.print(n + " ");
			}
			System.out.print("\n");
		}
	}
	
	public static void printIntData(int[] data) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(data[j+i*9] + " ");
			}
			System.out.print("\n");
		}
	}

}