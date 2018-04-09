import java.util.*;

public class LevelGenerator {
	
	public static void main(String[] args) {
		if (args.length == 2) {
			int size = Integer.parseInt(args[0]);
			int movesToComplete = Integer.parseInt(args[1]);
			int ret = 0;
			while (ret < movesToComplete) {
				int[][] grid = new int[size][size];
				setUpGrid(grid);
				shuffle(grid);
				ret = bfs(grid);
				printGrid(grid);
				System.out.println();
			}
		} else {
			System.out.println("Usage: java LevelGenerator <size> <minimum moves>");
		}
	}	
	
	public static int bfs(int[][] grid) {
		Set<State> seen = new HashSet<>();
		Queue<State> work = new LinkedList<>();
		work.add(new State(deepCopy(grid), 0));
		while (!work.isEmpty()) {
			State curr = work.remove();
			seen.add(curr);
			if (isGoal(curr.grid)) {
				System.out.println("Moves to complete: " + curr.numMoves);
				System.out.println("States seen: " + seen.size());
				return curr.numMoves;
			} else {
				for (State s : getNextStates(curr)) {
					if (!seen.contains(s)) {
						work.add(s);
					}
				}
			}
		}
		return 0;
	}
	
	public static Set<State> getNextStates(State curr) {
		Set<State> states = new HashSet<>();
		states.add(new State(moveUp(deepCopy(curr.grid)), curr.numMoves + 1));
		states.add(new State(moveDown(deepCopy(curr.grid)), curr.numMoves + 1));
		states.add(new State(moveLeft(deepCopy(curr.grid)), curr.numMoves + 1));
		states.add(new State(moveRight(deepCopy(curr.grid)), curr.numMoves + 1));
		return states;
	}
	
	public static int[][] deepCopy(int[][] grid) {
		int[][] a = new int[grid.length][grid.length];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				a[i][j] = grid[i][j];
			}
		}
		return a;
	}
	
	public static boolean isGoal(int[][] grid) {
		int size = grid.length;
		return grid[size - 1][size / 2] == 1;
	}
	
	public static void setUpGrid(int[][] grid) {
		int size = grid.length;
		int maxBlocks = size * size / 2;
		int numStationary = (int)(Math.random() * maxBlocks);
		int numMoving = (int)(Math.random() * maxBlocks);
		grid[size - 1][size / 2] = 1;
		for (int i = 0; i < numStationary; i++) {
			int x = 0;
			int y = 0;
			do {
				x = (int)(Math.random() * size);
				y = (int)(Math.random() * size);
			} while (grid[y][x] != 0);
			grid[y][x] = 2;
		}
		for (int i = 0; i < numMoving; i++) {
			int x = 0;
			int y = 0;
			do {
				x = (int)(Math.random() * size);
				y = (int)(Math.random() * size);
			} while (grid[y][x] != 0);
			grid[y][x] = 3;
		}
	}
	
	public static void shuffle(int[][] grid) {
		int last = 3;
		for (int i = 0; i < 50; i++) {
			int choice = 0;
			do {
				choice = (int)(Math.random() * 4);
			} while (last % 2 == choice % 2);
			last = choice;
			switch (choice) {
				case 0: moveUp(grid); break;
				case 2: moveDown(grid); break;
				case 1: moveLeft(grid); break;
				case 3: moveRight(grid); break;
			}
		}
	}
	
	public static void printGrid(int[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			String s = "\"";
			for (int j = 0; j < grid.length; j++) {
				char c = ' ';
				switch (grid[i][j]) {
					case 1: c = 'P'; break;
					case 2: c = 'S'; break;
					case 3: c = 'M'; break;
					default: c = ' '; break;
				}
				s += c;
			}
			System.out.println(s + "\",");
		}
	}
	
	public static int[][] moveUp(int[][] grid) {
		for (int i = 1; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[i][j] % 2 == 1) {
					int index = i;
					while (index > 0 && grid[index - 1][j] == 0) {
						grid[index - 1][j] = grid[index][j];
						grid[index][j] = 0;
						index--;
					}
				}
			}
		}
		return grid;
	}
	
	public static int[][] moveDown(int[][] grid) {
		for (int i = grid.length - 2; i >= 0; i--) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[i][j] % 2 == 1) {
					int index = i;
					while (index < grid.length - 1 && grid[index + 1][j] == 0) {
						grid[index + 1][j] = grid[index][j];
						grid[index][j] = 0;
						index++;
					}
				}
			}
		}
		return grid;
	}
	
	public static int[][] moveLeft(int[][] grid) {
		for (int i = 1; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[j][i] % 2 == 1) {
					int index = i;
					while (index > 0 && grid[j][index - 1] == 0) {
						grid[j][index - 1] = grid[j][index];
						grid[j][index] = 0;
						index--;
					}
				}
			}
		}
		return grid;
	}
	
	public static int[][] moveRight(int[][] grid) {
		for (int i = grid.length - 2; i >= 0; i--) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[j][i] % 2 == 1) {
					int index = i;
					while (index < grid.length - 1 && grid[j][index + 1] == 0) {
						grid[j][index + 1] = grid[j][index];
						grid[j][index] = 0;
						index++;
					}
				}
			}
		}
		return grid;
	}
}