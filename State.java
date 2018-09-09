package edu.iastate.cs228.hw3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 *  
 * @author Richa Patel
 *
 */

/**
 * This class represents a board configuration in the 8-puzzle. Only the initial
 * configuration is generated by a constructor, while intermediate
 * configurations will be generated via calling the method successorState().
 * State objects will form two circular doubly-linked lists OPEN and CLOSED,
 * which will be used by the A* algorithm to search for a path from a given
 * initial board configuration to the final board configuration below:
 * 
 * 1 2 3 8 4 7 6 5
 *
 * The final configuration (i.e., the goal state) above is not explicitly
 * represented as an object of the State class.
 */

public class State implements Cloneable, Comparable<State> {
	public int[][] board; // configuration of tiles

	public State previous; // previous node on the OPEN/CLOSED list
	public State next; // next node on the OPEN/CLOSED list
	public State predecessor; // predecessor node on the path from the initial state

	public Move move; // the move that generated this state from its predecessor
	public int numMoves; // number of moves from the initial state to this state

	public static Heuristic heu; // heuristic used. shared by all the states.

	private int numMismatchedTiles = -1; // number of mismatched tiles between this state
											// and the goal state; negative if not computed yet.
	private int ManhattanDistance = -1; // Manhattan distance between this state and the
										// goal state; negative if not computed yet.

	public static int[][] goalState = { { 1, 2, 3 }, { 8, 0, 4 }, { 7, 6, 5 } }; // goal state configuration

	/**
	 * Constructor (for the initial state).
	 * 
	 * It takes a 2-dimensional array representing an initial board configuration.
	 * The empty square is represented by the number 0.
	 * 
	 * a) Initialize all three links previous, next, and predecessor to null. b) Set
	 * move to null and numMoves to zero.
	 * 
	 * @param board
	 * @throws IllegalArgumentException
	 *             if board is not a 3X3 array or its nine entries are not
	 *             respectively the digits 0, 1, ..., 8.
	 */
	public State(int[][] board) throws IllegalArgumentException {

		this.board = board;

		if (!stateboard()) {
			throw new IllegalArgumentException();
		}
		previous = null;
		next = null;
		predecessor = null;
		move = null;
		numMoves = 0;

	}

	/**
	 * Constructor (for the initial state)
	 * 
	 * It takes a state from an input file that has three rows, each containing
	 * three digits separated by exactly one blank. Every row starts with a digit.
	 * The nine digits are from 0 to 8 with no duplicates.
	 * 
	 * Do the same initializations as for the first constructor.
	 * 
	 * @param inputFileName
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             if the file content does not meet the above requirements.
	 */
	public State(String inputFileName) throws FileNotFoundException, IllegalArgumentException {
		this.board = new int[3][3];

		//reading the input file
		Scanner scan = new Scanner(new FileReader(inputFileName));
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				board[row][column] = scan.nextInt();
			}
		}
		scan.close();

		if (!stateboard()) {
			throw new IllegalArgumentException();
		}

		previous = null;
		next = null;
		predecessor = null;
		move = null;
		numMoves = 0;
	}

	//This is a helper method and it is used for the first method. This
	//checks the board.
	private boolean stateboard() {

		if ((board.length != 3) || board[0].length != 3) {
			return false;
		}
		
		ArrayList<Integer> numbers = new ArrayList<>();
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				int num = board[r][c];
				if (num < 0) {
					return false;
				}
				if (num > 8 || numbers.contains(num)) {
					return false;
				}

				numbers.add(num);
			}
		}
		return true;
	}

	/**
	 * Generate the successor state resulting from a given move. Throw an exception
	 * if the move cannot be executed. Besides setting the array board[][] properly,
	 * you also need to do the following:
	 * 
	 * a) set the predecessor of the successor state to this state; b) set the
	 * private instance variable move of the successor state to the parameter m; c)
	 * Set the links next and previous to null; d) Update numMoves.
	 * 
	 * @param m
	 *            one of the moves LEFT, RIGHT, UP, and DOWN
	 * @return
	 * @throws IllegalArgumentException
	 *             if RIGHT when the empty square is in the left column, or if LEFT
	 *             when the empty square is in the right column, or if UP when the
	 *             empty square is in the bottom row, or if DOWN when the empty
	 *             square is in the top row.
	 */
	public State successorState(Move m) throws IllegalArgumentException {
		
		int blankrow = 0; 
		int blankcolumn = 0;
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				if (board[row][column] == 0) {
					blankrow = row;
					blankcolumn = column;
					break;
				}
			}
		}

		
		int finalrow = blankrow, finalcolumn = blankcolumn;
		while(m == Move.RIGHT && blankcolumn != 0) {
			finalcolumn--;
			break;
			
		}
		while(m == Move.LEFT && blankcolumn != 2) {
			finalcolumn++;
			break;
	
		}
		while (m == Move.UP && blankrow != 2) {
			finalrow++;
			break;
		}
		while(m == Move.DOWN && blankrow != 0) {
			finalrow--;
			break;
		 
		
	}
	
		

		State successor = (State) clone();

		int temporary = successor.board[finalrow][finalcolumn];
		successor.board[finalrow][finalcolumn] = successor.board[blankrow][blankcolumn];
		successor.board[blankrow][blankcolumn] = temporary;

		successor.move = m;
		successor.numMoves = this.numMoves + 1;
		successor.predecessor = this;
	
		return successor;
		}
	
	/**
	 * Determines if the board configuration in this state can be rearranged into
	 * the goal configuration. According to the appendix in the project description,
	 * we check if this state has an odd number of inversions.
	 */
	/**
	 * 
	 * @return true if the puzzle starting in this state can be rearranged into the
	 *         goal state.
	 */
	public boolean solvable() {

		boolean solve = true;
		for (int i = 0; i < board.length; i++) {
			if (board[i].length == 0)
				continue;
			for (int j = i + 1; j < board.length; j++) {
				if (board[j].length == 0)
					continue;
				if (board[i].length > board[j].length)
					return false;
			}
		}
		return solve;
		
		 
	}

	
	/**
	 * Check if this state is the goal state, namely, if the array board[][] stores
	 * the following contents:
	 * 
	 * 1 2 3 8 0 4 7 6 5
	 * 
	 * @return
	 */
	public boolean isGoalState() //this is what the final output should look like in 10 moves
	{

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				if (board[row][column] != goalState[row][column]) {
					return false;
				}
			}
		}
		return true;

	}

	/**
	 * Write the board configuration according to the following format:
	 * 
	 * a) Output row by row in three lines with no indentations. b) Two adjacent
	 * tiles in each row have exactly one blank in between. c) The empty square is
	 * represented by a blank.
	 * 
	 * For example,
	 * 
	 * 2 3 1 8 4 7 6 5
	 * 
	 */
	@Override
	public String toString() 
	{
		StringBuilder stateStringBuilder = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == 0) {
					stateStringBuilder.append(' ');
				} else {
					stateStringBuilder.append(board[i][j]);
				}
				stateStringBuilder.append(' ');
			}
			stateStringBuilder.append('\n');
		}
		return stateStringBuilder.toString();
	}

	/**
	 * Create a clone of this State object by copying over the board[][]. Set the
	 * links previous, next, and predecessor to null.
	 * 
	 * The method is called by SuccessorState();
	 */
	@Override
	public Object clone() 
	{
		int[][] copying = new int[board.length][board[0].length];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				copying[i][j] = board[i][j];

			}
		}
		State s = new State(copying);
		return s;

	}

	/**
	 * Compare this state with the argument state. Two states are equal if their
	 * arrays board[][] have the same content.
	 */
	@Override
	public boolean equals(Object o) { // excellent
		if (o == null || getClass() != o.getClass())
			return false;
		State p = (State) o;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (this.board[i][j] != p.board[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Evaluate the cost of this state as the sum of the number of moves from the
	 * initial state and the estimated number of moves to the goal state using the
	 * heuristic stored in the instance variable heu.
	 * 
	 * If heu == TileMismatch, add up numMoves and the return values from
	 * computeNumMismatchedTiles(). If heu == MahattanDist, add up numMoves and the
	 * return values of computeMahattanDistance().
	 * 
	 * @param h
	 * @return estimated number of moves from the initial state to the goal state
	 *         via this state.
	 * @throws IllegalArgumentException
	 *             if heuristic is neither 0 nor 1.
	 */
	public int cost() throws IllegalArgumentException {

		if (heu == Heuristic.TileMismatch) {
			return numMoves + computeNumMismatchedTiles();
		} else if (heu == Heuristic.ManhattanDist) {
			return numMoves + computeManhattanDistance();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Compare two states by the cost. Let c1 and c2 be the costs of this state and
	 * the argument state s.
	 * 
	 * @return -1 if c1 < c2 0 if c1 = c2 1 if c1 > c2
	 * 
	 *         Call the method cost(). This comparison will be used in maintaining
	 *         the OPEN list by the A* algorithm.
	 */
	@Override
	public int compareTo(State s) 
	{
		if (this.cost() < s.cost()) {
			return -1;
		}
		if (this.cost() == s.cost()) {
			return 0;
		}
		return 1;

	}

	/**
	 * Return the value of numMismatchedTiles if it is non-negative, and compute the
	 * value otherwise.
	 * 
	 * @return number of mismatched tiles between this state and the goal state.
	 */
	private int computeNumMismatchedTiles() 
	{
		int value = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (goalState[i][j] == 0) {
					continue;
				}
				if (goalState[i][j] != board[i][j]) {
					value++;
				}
			}

		}
		return value;

	}

	/**
	 * Return the value of ManhattanDistance if it is non-negative, and compute the
	 * value otherwise.
	 * 
	 * @return Manhattan distance between this state and the goal state.
	 */
	private int computeManhattanDistance() {

		// for every element in board except for 0;
		// find the row and column
		// find the row and column of that number in the goal state
		// add the absolute value of the difference between the rows and cols.

		if (ManhattanDistance > 1) 
		{
			return ManhattanDistance;
		}
		ManhattanDistance = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int integer = board[i][j];
				if (integer != 0) {
					for (int r = 0; r < 3; r++) {
						for (int c = 0; c < 3; c++) {
							int goal = goalState[r][c];
							if (integer == goal) {
								int row = Math.abs(i - r);
								int column = Math.abs(j - c);
								ManhattanDistance += (row + column);
							}
						}
					}
				}
			}
		}
		return ManhattanDistance;

	}
	// public static void main(String[] args) {
	// int[][] board = new int[][] {{4,1,2},{5,3,0},{8,6,7}};
	// State s1 = new State(board2);

}


