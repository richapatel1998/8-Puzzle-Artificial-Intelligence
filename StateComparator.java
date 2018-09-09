package edu.iastate.cs228.hw3;
import java.util.Comparator;

/**
 *  
 * @author Richa Patel
 *
 */

/**
 * This method compares two states in the lexicographical order of the board
 * configuration. The 3X3 array representing each board configuration is
 * converted into a sequence of nine digits starting at the 0th row, and within
 * each row, at the 0th column. For example, the two states
 * 
 * 2 0 3 2 8 1 1 8 4 7 5 3 7 6 5 6 0 4
 *
 * are converted into the sequences <2,0,3,1,8,4,7,6,5>, and
 * <2,8,1,7,5,3,6,0,4>, respectively. By definition the first state is less than
 * the second one.
 * 
 * The comparator will be used for maintaining the CLOSED list used in the A*
 * algorithm.
 */
public class StateComparator implements Comparator<State> {
	@Override
	public int compare(State s1, State s2) {
/*
		
		*/
		
		// compare the board read it element by element. if element smaller than the
		for (int i = 0; i < s1.board.length; i++) { // rows
			for (int j = 0; j < s2.board.length; j++) { // columns
				if (s1.board[i][j] < s2.board[i][j]) {
					return -1;
				} else if (s1.board[i][j] > s2.board[i][j]) {
					return 1;
				}
				// return negative number of s1 < s2
				// return positive number if s1 > s2
			}
		}

		// TODO
		return 0;
		
	}
}

