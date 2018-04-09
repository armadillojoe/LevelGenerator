import java.util.*;

public class State {
	int[][] grid;
	int numMoves;
	
	public State(int[][] grid, int numMoves) {
		this.grid = grid;
		this.numMoves = numMoves;
	}
	
	@Override
	public boolean equals(Object other) {
		return this.toString().equals(((State)other).toString());
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	public String toString() {
		String s = "[";
		for (int i = 0; i < grid.length; i++) {
			s += Arrays.toString(grid[i]) + ",";
		}
		return s + "]";
	}
}