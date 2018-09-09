package edu.iastate.cs228.hw3;

import java.io.FileNotFoundException;

/**
 * 
 * @author Richa Patel
 *
 */

public class EightPuzzle {
	/**
	* This static method solves an 8-puzzle with a given initial state using two
	* heuristics which compare the board configuration with the goal configuration
	* by the number of mismatched tiles, and by the Manhattan distance,
	* respectively. The goal configuration is set for all puzzles as
	* 
	* 1 2 3 8 4 7 6 5 F
	* 
	* @param s0
	* @return
	*/
	public static String solve8Puzzle(State s0) {
		// 1) Return null if the puzzle is not solvable.
		if (!s0.solvable()) {
			// return s0.toString();
			System.out.println("No solution exists for the following initial state: ");
			System.out.println();
			System.out.println(s0);
		}

		// 2) Otherwise, solve the puzzle with two heuristics. The two solutions may be
		// different
		// but must have the same length for optimality.

		Heuristic h[] = { Heuristic.TileMismatch, Heuristic.ManhattanDist };
		String[] moves = new String[2];

		for (int i = 0; i < 2; i++) {
			moves[i] = AStar(s0, h[i]);
		}

		// 3) Combine the two solution strings into one that would print out in the
		// output format specified in Section 5 of the project description.

		return moves[0] + "\n\n" + moves[1];
	}

	/**
	* This method implements the A* algorithm to solve the 8-puzzle with an input
	* initial state s0. The algorithm is described in Section 3 of the project
	* description.
	* 
	* Precondition: the puzzle is solvable with the initial state s0.
	* 
	* @param s0
	*            initial state
	* @param h
	*            heuristic
	* @return solution string
	*/
	public static String AStar(State s0, Heuristic h) {
		// Initialize the two lists used by the algorithm.
		OrderedStateList OPEN = new OrderedStateList(h, true);
		OrderedStateList CLOSE = new OrderedStateList(h, false);

		// Implement the algorithm described in Section 3 to solve the puzzle.
		// Once a goal state s is reached, call solutionPath(s) and return the solution
		// string.

		OPEN.addState(s0);
		while (OPEN.size() > 0) {
			State s = OPEN.remove();
			CLOSE.addState(s);

			
			if (s.isGoalState()) {
				String result = s.numMoves + " moves in total (heuristic: ";
				if (h == Heuristic.TileMismatch) {
					result += "number of mismatched tiles";
				} else if (h == Heuristic.ManhattanDist) {
					result += "the Manhattan distance";
				}
				result += ")\n" + (new EightPuzzle()).solutionPath(s);
				return result;
			}
//if the goal state is valid, then we return the result.
			
			for (Move moveposition : Move.values()) {

				State successor = null;
				try {
					successor = s.successorState(moveposition);
				} catch (IllegalArgumentException e) {
					continue; 
				}

			
				
				if (OPEN.findState(successor) == null) {
					OPEN.addState(successor);
				} else if (CLOSE.findState(successor) == null) {
					OPEN.addState(successor);
				} else if (OPEN.findState(successor) != null) {
					State previous = OPEN.findState(successor);
					if (previous.cost() > successor.cost()) {
						OPEN.removeState(previous);
						OPEN.addState(successor);
					}
				} else if (CLOSE.findState(successor) != null) {
					State previous = CLOSE.findState(successor);
					if (successor.cost() < previous.cost()) {
						CLOSE.removeState(previous);
						OPEN.addState(successor);
					}
				}
			}
		}

		
		return null;
	}

	/**
	* From a goal state, follow the predecessor link to trace all the way back to
	* the initial state. Meanwhile, generate a string to represent board
	* configurations in the reverse order, with the initial configuration appearing
	* first. Between every two consecutive configurations is the move that causes
	* their transition. A blank line separates a move and a configuration. In the
	* string, the sequence is preceded by the total number of moves and a blank
	* line.
	* 
	* See Section 5 in the projection description for an example.
	* 
	* Call the toString() method of the State class.
	* 
	* @param goal
	* @return
	*/
	private String solutionPath(State goal) {
		String string = "";
		State state = goal;
		while (state != null) {
			if (state.move != null) {
				string = "\n" + state.move + "\n\n" + state + string;
			} else {
				string = "\n" + state + string;
			}
			state = state.predecessor;
		}
		return string;
	}

}


