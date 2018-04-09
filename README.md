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

The code above works by first determining the max amount of each non player block that can be added to the level, which we call maxBlocks, and is equal to half the area of the game state. Then we randomly decide the amount of each of these two types of blocks that we will add with the formula (int)(Math.random() * maxBlocks). This formula generate a number from 0 inclusive, to maxBlocks exclusive. So with a 4 by 4 game state, there are 16 total spaces, with [0, 8) moving and stationary blocks. That is a max of 14 of these together, which leaves us 1 space for the player block, and an empty space to allow movement.
In this particular puzzle game, a state is solved if the player block is in the goal space (the space in the middle of the bottom row), which causes us to set grid[size - 1][size / 2] = 1, where size is the size of the array. Then we randomly determine empty positions to place the stationary and moving blocks.

###Shuffling the State