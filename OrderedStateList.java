package edu.iastate.cs228.hw3;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  
 * @author Richa Patel
 *
 */

/**
 * This class describes a circular doubly-linked list of states to represent
 * both the OPEN and CLOSED lists used by the A* algorithm. The states on the
 * list are sorted in the
 * 
 * a) order of non-decreasing cost estimate for the state if the list is OPEN,
 * or b) lexicographic order of the state if the list is CLOSED.
 * 
 */
public class OrderedStateList {

	/**
	 * Implementation of a circular doubly-linked list with a dummy head node.
	 */
	private State head; // dummy node as the head of the sorted linked list
	private int size = 0;

	private boolean isOPEN; // true if this OrderedStateList object is the list OPEN and false
							// if the list CLOSED.

	/**
	 * Default constructor constructs an empty list. Initialize heuristic. Set the
	 * fields next and previous of head to the node itself. Initialize instance
	 * variables size and heuristic.
	 * 
	 * @param h
	 * @param isOpen
	 */
	public OrderedStateList(Heuristic h, boolean isOpen) {
		// Initialize head with a dummy value
		head = new State(new int[][] { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } });
		head.next = head;
		head.previous = head;
		size = 0;
		isOPEN = isOpen;
		State.heu = h; // initialize heuristic used for evaluating all State objects.
	}

	public int size() {
		return size;
	}

	/**
	 * A new state is added to the sorted list. Traverse the list starting at head.
	 * Stop right before the first state t such that compare(s, t) <= 0, and add s
	 * before t. If no such state exists, simply add s to the end of the list.
	 * 
	 * Precondition: s does not appear on the sorted list.
	 * 
	 * @param s
	 */
	public void addState(State s) {
		
		
		State comp = head.next;

			while (comp != head) {
			if (compareStates(s, comp) <= 0) {
				break;
		}
			comp = comp.next;
		}

			s.previous = comp.previous;
			comp.previous.next = s;
			comp.previous = s;
			s.next = comp;
			size++; //increment
			
	}

	/**
	 * Conduct a sequential search on the list for a state that has the same board
	 * configuration as the argument state s.
	 * 
	 * Calls compareStates().
	 * 
	 * @param s
	 * @return the state on the list if found null if not found
	 */
	public State findState(State s) {

		State comp = head.next;
		do {
			if (comp.equals(s)) {
				return comp;
			}
			comp = comp.next;
		} while (comp != head && compareStates(s, comp) >= 0);
		return null;

	}

	/**
	 * Remove the argument state s from the list. It is used by the A* algorithm in
	 * maintaining both the OPEN and CLOSED lists.
	 * 
	 * @param s
	 * @throws IllegalStateException
	 *             if s is not on the list
	 */
	public void removeState(State s) throws IllegalStateException {
		
		State foundState = findState(s);
		
		if (foundState == null) {
			throw new IllegalStateException();
		}
		
		if (foundState == head) {
			if (this.size > 1) {
				head = head.next;
				head.previous = head;
				size--;	//decrement	
			} else {
				head = null;
				size = 0; 
			}
			
		}
		else if (foundState.next == foundState) {
			State prev = foundState.previous;
			prev.next = prev;
			size--;
		} else {
			State prev = foundState.previous;
			State next = foundState.next;
			prev.next = next;
			next.previous = prev;
			size--; //decrement 
			
		}
		

	}

	/**
	 * Remove the first state on the list and return it. This is used by the A*
	 * algorithm in maintaining the OPEN list.
	 * 
	 * @return
	 */
	public State remove() {
		

		
		if (0 < size) {
			State comp = head.next;
			head.next = comp.next;
			comp.next.previous = head;
			return comp;
		}
		return null;
		
	}

	/**
	 * Compare two states depending on whether this OrderedStateList object is the
	 * list OPEN or the list CLOSE used by the A* algorithm. More specifically,
	 * 
	 * a) call the method compareTo() of the State if isOPEN == true, or b) create a
	 * StateComparator object to call its compare() method if isOPEN == false.
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	private int compareStates(State s1, State s2) {
		if (isOPEN == true) {
			return s1.compareTo(s2);
		} else {
			StateComparator statecomparator = new StateComparator();
			return statecomparator.compare(s1, s2);
		}
	}
}
