# A Simple Level Generator
I have been working on a puzzle game for the past few weeks, and one of the most tedious aspects was creating levels. Naturally when things are tedious, I try to write code to automate the process. It turned out to be very useful for me, so I'm writing this in hopes it can be useful to other amateur game devs.

**Disclaimer:** This definitely isn't the only way to generate levels, and probably isn't even the best way, but it's pretty simple and it works. And although it was designed for a puzzle game, with some thought the same processes can be used to make levels for other genres.

## Setting Up A Game State
In my level generator, a game state is an n by n array of ints, where 0s are empty spaces, a 1 is the player block, 2s are non moving blocks, and 3s are moving blocks. To generate a level, the obvious approach would be to just randomly stick each type of block in different places in the array. This is basically what we will do, but we need to be careful to make sure that the random state we set up is solvable. To do this, we will initialize the game to a solved state, and then shuffle the state by making legal moves.

Here is our main method to begin with:

```java
public class LevelGenerator {

	public static void main(String[] args) {
		int size = 4;
		int[][] grid = new int[size][size];
	}
}
```

### Initializing the State
We start by initializing to a solved state as mentioned above. We will do this in a method called setUpGrid.

```java
public static void setUpGrid(int[][] grid) {
	int size = grid.length;
	int maxBlocks = size * size / 2;
	int numStationary = (int)(Math.random() * maxBlocks);
	int numMoving = (int)(Math.random() * maxBlocks);
	
	// Set player block in a solved state
	grid[size - 1][size / 2] = 1; 
	
	// Randomly set stationary blocks
	for (int i = 0; i < numStationary; i++) {
		int x = 0;
		int y = 0;
		do {
			x = (int)(Math.random() * size);
			y = (int)(Math.random() * size);
		} while (grid[y][x] != 0);
		grid[y][x] = 2;
	}
	
	// Randomly set moving blocks
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
```

The code above works by first determining the max amount of each non player block that can be added to the level, which we call maxBlocks, and is equal to half the area of the game state. Then we randomly decide the amount of each of these two types of blocks that we will add, using the formula (int)(Math.random() * maxBlocks). This formula generates a number from 0 inclusive, to maxBlocks exclusive. So with a 4 by 4 game state, there are 16 total spaces, with [0, 8) moving and stationary blocks. That is a max of 14 of these together, which leaves us 1 space for the player block, and an empty space to allow movement.
In this particular puzzle game, a state is solved if the player block is in the goal space (the space in the middle of the bottom row), which causes us to set grid[size - 1][size / 2] = 1, where size is the size of the array. Then we randomly determine empty positions to place the stationary and moving blocks.

### Shuffling the State
To shuffle the state, we need to know what a legal move in our game is. For my game, you can move up, down, left, or right. When you move, the player block and all moving blocks will move as far as they can in that direction, stopping if they reach the bounds of the level, or if they collide with another block. I'll skip explaining the implementation of these methods as they are not too important, but if you are curious the full code is included in the repo. But anyways, we will need to write a shuffling method.

```java
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
```

Basically, this method chooses and moves in a random direction 50 times (50 is arbitrary, can use any number you think sufficiently shuffles). If you are randomly choosing a move, there is a good chance you will for example choose to move up, then down afterward. This essentially wastes 2 steps of the shuffle, as the down move counteracts the up move. I solved this in a sort of hacky manner, by assigning up and down to 0 and 2 (even numbers), and left and right to 1 and 3 (odd numbers), so you can keep randomly generating a choice while the last choice and the current choice are the same when modded by 2. This is what the do while loop here is doing.

So now we have this in our main method:

```java
public static void main(String[] args) {
	int size = 4;
	int[][] grid = new int[size][size];
	setUpGrid(grid);
	shuffle(grid);
	printGrid(grid);
}
```

Where printGrid is some method where you print out the grid in whatever way works for you. This will generate very reasonable levels, but you still need to test each level by hand to confirm it is solvable, and to determine how difficult it is. This, of course, can be automated also.

## Solving Your New Levels
To solve the newly created level, we will use the breadth first search algorithm (BFS), as it will find if it's solvable in the least amount of moves possible. But first, we need to create a class that represents a game state that can be used in BFS.

### The State Class
```java
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
```

Our state class simply consists of the 2d array grid which is the actual state, and the number of moves we took to reach this state. In order to make this class usable in a HashSet for BFS, we need to override the equals and hashCode method.

For this game, two states are equal if they have the same blocks in the same positions. The simplest way to test this, is to see if the String representations of both grids are equal. To do this we make a toString method that concatenates the toStrings of each 1d array within the grid array. Then for equals we just call the String equals method for both States toStrings, and for hashCode we just call the hashCode of the states toString.

### BFS
```java
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
```
For BFS, we have a queue of states needed to be expanded, and a HashSet of states we have already seen, so we don't get lost in cycles. We add a new State to the work queue which is that state we start from. Then, while the work queue is not empty, we remove the front of the queue, and add it to the seen set.
Then we see if the states grid is in the goal position with this method:

```java
public static boolean isGoal(int[][] grid) {
	int size = grid.length;
	return grid[size - 1][size / 2] == 1;
}
```

If it is, then this level has been solved, and you can print out the number of moves it took to solve. (Also printing the number of states seen just for my own curiosity).
If it is not the goal state, then we have move work to do. We get the possible next states from this method:

```java
public static Set<State> getNextStates(State curr) {
	Set<State> states = new HashSet<>();
	states.add(new State(moveUp(deepCopy(curr.grid)), curr.numMoves + 1));
	states.add(new State(moveDown(deepCopy(curr.grid)), curr.numMoves + 1));
	states.add(new State(moveLeft(deepCopy(curr.grid)), curr.numMoves + 1));
	states.add(new State(moveRight(deepCopy(curr.grid)), curr.numMoves + 1));
	return states;
}
```

And if each state is not already in the seen set, then we add it to the work queue and the while loop continues. Eventually this method finishes and returns the number of moves needed to solve the level, or 0 if the level is unsolvable.
**Note:** Java passes arrays by reference, so when we create new states, we need to copy the grid (using our deepCopy method, pretty trivial, check code if interested) before passing it to a move function, or else we would always just be modifying the original grid, which would cause us issues.

## Finishing Up
Now we can add BFS to our main method:

```java
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
```

We can also take command line arguments for the size of the grid, and the minimum amount of moves you want to be needed to complete a level. Then when running the program, you will generate levels and automatically test them with BFS until you find one with the difficulty you are looking for.

I hope this helps someone else with level generation, and if you have any questions create an issue or something and I would be happy to answer.

### Slidey Blocks
If you are curious about the game I used this for, you can find it here:

+ Kongregate: https://www.kongregate.com/games/joejimenezgames/slidey-blocks
+ NewGrounds: https://www.newgrounds.com/portal/view/709155
+ Google Play: https://play.google.com/store/apps/details?id=com.joejoe.slideyblocks