import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;

/* if you're looking at a spot x and the spots around it, the directions are
 * 123
 * 8x4
 * 765
 */

public class MinesweeperOriginal {
	public static void main(String[] args){
		Scanner input = new Scanner(System.in);
		int difficulty = isInRange(1, 3, "Do you want to play easy(1), medium(2), or hard(3)? ", input);
		int WIDTH = 1, HEIGHT = 1, MINES = 1;
		if(difficulty == 1){
			WIDTH = 10;
			HEIGHT = 10;
			MINES = 10;
		} else if(difficulty == 2){
			WIDTH = 14;
			HEIGHT = 14;
			MINES = 30;
		} else {
			WIDTH = 20;
			HEIGHT = 15;
			MINES = 50;
		}
		/*int WIDTH = isInRange(5, 20, "What width do you want the grid to be? ", input);
		int HEIGHT = isInRange(5, 20, "What height do you want the grid to be? ", input);
		int minDim = Math.min(WIDTH, HEIGHT);
		int maxDim = Math.max(WIDTH, HEIGHT);
		int MINES = isInRange(minDim, maxDim, "How many mines do you want the grid to have? "
				+ "No fewer than " + minDim + " or more than " + maxDim + ": ");*/
		int[][] grid = new int[HEIGHT][WIDTH];
		char[][] game = new char[HEIGHT][WIDTH];
		for(int r = 0; r < HEIGHT; r++){
			for(int c = 0; c < WIDTH; c++){
				grid[r][c] = 0;
				game[r][c] = '-';
			}
		}
		//grid will store data that isn't seen by the player such as -1 for a mine
		//game will store data the player does see like spots they have checked
		int mine = 0;
		while(mine < MINES){
			//put the mines in random spots
			Random rand = new Random();
			int y_var = rand.nextInt(WIDTH - 1);
			int x_var = rand.nextInt(HEIGHT - 1);
			if(grid[x_var][y_var] != -1){
				grid[x_var][y_var] = -1;
				mine++;
			}
			//ensures two mines aren't placed in the same spot
		}
		char[][] gridRep = new char[HEIGHT][WIDTH];
		for(int r = 0; r < HEIGHT; r++){
			for(int c = 0; c < WIDTH; c++){
				//takes every spot on the grid, if not a mine, and changes it
				//to represent how many mines surround it
				if(grid[r][c] != -1){
					ArrayList<Integer> directions = whichDirections(r, c, WIDTH, HEIGHT);
					grid[r][c] = checkMines(directions, r, c, grid);
					gridRep[r][c] = (char) ('0' + (char) grid[r][c]);
				} else {
					gridRep[r][c] = 'X';
				}
			}
		}
		printGridC(game);
		boolean win = false, lose = false;
		int spotsOpen = WIDTH*HEIGHT, flagsPlaced = 0;
		//if you have a 10x10 grid, there are 100 spots of which 10 are mines
		//after every new spot is opened by the player, the spot count decreases
		//when there are only 10 spots left, the player has found all mines and wins
		while(win == false && lose == false){
			int y_var = isInRange(1, WIDTH, "Which column 1-" + WIDTH + ": ", input) - 1;
			int x_var = isInRange(1, HEIGHT, "Which row 1-" + HEIGHT + ": ", input) - 1;
			//it subtracts 1 because arrays start at index 0 not 1 like we ask the player
			System.out.print("Do you want to flag/unflag this spot as a mine or click? "
					+ "Anything other than 'flag' will mean click: ");
			String toFlag = input.next();
			if(game[x_var][y_var] == '-'){
				if(!toFlag.equals("flag")){
					spotsOpen --; //revealing new spot
					game[x_var][y_var] = gridRep[x_var][y_var];
					if(grid[x_var][y_var] == -1){ //spot chosen is a mine
						printGridC(game);
						System.out.println();
						System.out.println("Game over");
						lose = true;
					}
				} else { //flagging it as a mine
					game[x_var][y_var] = 'F';
					flagsPlaced++;
				}
			} else if(game[x_var][y_var] == 'F'){
				if(toFlag.equals("flag")){ //removing flag
					game[x_var][y_var] = '-';
					flagsPlaced--;
				} else {
					System.out.println("You cannot click a flagged spot");
				}
			}
			if (game[x_var][y_var] == '0'){
				//if the spot has 0 mines around it, all of them can be revealed
				ArrayList<Integer> placeZero = whichDirections(x_var, y_var, WIDTH, HEIGHT);
				for(int num : placeZero){
					int r = x_var, c = y_var;
					//some of the spots around the 0 might have already been revealed, so they
					//do not need to be re-revealed and the spot count should not decrease if
					//they have already been revealed
					if (num == 1 && game[r-1][c-1] == '-'){
		                game[r-1][c-1] = gridRep[r-1][c-1];
		                spotsOpen--;
					} else if (num == 2 && game[r-1][c] == '-'){
		                game[r-1][c] = gridRep[r-1][c];
		                spotsOpen--;
					} else if (num == 3 && game[r-1][c+1] == '-'){
		                game[r-1][c+1] = gridRep[r-1][c+1];
		                spotsOpen--;
					} else if (num == 4 && game[r][c+1] == '-'){
		                game[r][c+1] = gridRep[r][c+1];
		                spotsOpen--;
					} else if (num == 5 && game[r+1][c+1] == '-'){
		                game[r+1][c+1] = gridRep [r+1][c+1];
		                spotsOpen--;
					} else if (num == 6 && game[r+1][c] == '-'){
		                game[r+1][c] = gridRep[r+1][c];
		                spotsOpen--;
					} else if (num == 7 && game[r+1][c-1] == '-'){
		                game[r+1][c-1] = gridRep[r+1][c-1];
		                spotsOpen--;
					} else if (num == 8 && game[r][c-1] == '-'){
		                game[r][c-1] = gridRep[r][c-1];
		                spotsOpen--;
					}
				}
			}
			printGridC(game);
			if(spotsOpen == MINES){
				//if the only spots left are the mines, the player wins
				System.out.println();
				System.out.println("You win!");
				win = true;
			} else {
				System.out.println("Flags placed = " + flagsPlaced);
				System.out.println();
			}
		}
		System.out.println("The grid was: ");
		printGridC(gridRep);
		input.close();
	}
	
	public static int isInRange(int minRange, int maxRange, String phrase){
		//checks if input given from user is an integer
		//and fits the range it's supposed to be in
		Scanner input = new Scanner(System.in);
		boolean correct = false;
		int var = 0;
		while(correct == false){
			System.out.print(phrase);
			String given = input.next();
			try {
				var = Integer.parseInt(given);
			} catch (NumberFormatException e){
				System.out.print("Try again. ");
			}
			if(var >= minRange && var <= maxRange){
				correct = true;
			} else {
				System.out.print("Try again. ");
			}
		}
		input.close();
		return var;
	}
	
	public static int isInRange(int minRange, int maxRange, String phrase, Scanner input){
		//checks if input given from user is an integer
		//and fits the range it's supposed to be in
		int var = 0;
		System.out.print(phrase);
		if(input.hasNextInt()){
			var = input.nextInt();
			//input.nextLine();
			if(var >= minRange && var <= maxRange){
				return var;
			} else {
				return isInRange(minRange, maxRange, phrase, input);
			}
		} else {
			//input.next();
			if(input.hasNext()){
				input.nextLine();
			}
			return isInRange(minRange, maxRange, phrase, input);
		}
	}
	
	public static ArrayList<Integer> whichDirections(int r, int c, int WIDTH, int HEIGHT){
		//based on where in the grid the spot is, it returns which directions to check
		ArrayList<Integer> results = new ArrayList<Integer>();
		if(r == 0){
			if(c == 0){
				results = add3(4, 5, 6, results);
			} else if(c == WIDTH - 1){
				results = add3(6, 7, 8, results);
			} else {
				results = add5(4, 5, 6, 7, 8, results);
			}
		} else if(r == HEIGHT - 1){
			if(c == 0){
				results = add3(2, 3, 4, results);
			} else if(c == WIDTH - 1){
				results = add3(1, 2, 8, results);
			} else {
				results = add5(1, 2, 3, 4, 8, results);
			}
		} else {
			if(c == 0){
				results = add5(2, 3, 4, 5, 6, results);
			} else if(c == WIDTH - 1){
				results = add5(1, 2, 6, 7, 8, results);
			} else {
				results = add3(1, 2, 3, results);
				results = add5(4, 5, 6, 7, 8, results);
			}
		}
		return results;
	}
	
	public static ArrayList<Integer> add3(int a, int b, int c, ArrayList<Integer> results){
		results.add(a);
		results.add(b);
		results.add(c);
		return results;
	}
	
	public static ArrayList<Integer> add5(int a, int b, int c, int d, int e, ArrayList<Integer> results){
		results.add(a);
		results.add(b);
		results.add(c);
		results.add(d);
		results.add(e);
		return results;
	}
	
	public static int checkMines(ArrayList<Integer> directions, int r, int c, int[][] grid){
		//takes in a list of which directions are valid for the spot [r][c]
		//calculates and returns how many mines surround it
		int total = 0;
		for(int num : directions){
			if (num == 1 && grid[r-1][c-1] == -1){
	            total++;
			} else if (num == 2 && grid[r-1][c] == -1){
	            total++;
			} else if (num == 3 && grid[r-1][c+1] == -1){
	            total++;
			} else if (num == 4 && grid[r][c+1] == -1){
	            total++;
			} else if (num == 5 && grid[r+1][c+1] == -1){
	            total++;
			} else if (num == 6 && grid[r+1][c] == -1){
	            total++;
			} else if (num == 7 && grid[r+1][c-1] == -1){
	            total++;
			} else if (num == 8 && grid[r][c-1] == -1){
	            total++;
			}
		}
		return total;
	}
	
	public static String returnGridC(char[][] grid){
		//can return a grid as a string
		String result = "";
		for(int r = 0; r < grid.length; r++){
			result += "[";
			for(int c = 0; c < grid[r].length - 1; c++){
				result += grid[r][c] + ", ";
			}
			result += grid[r][grid[r].length - 1] + "]\n";
		}
		return result;
	}
	
	public static void printGridC(char[][] grid){
		for(int r = 0; r < grid.length; r++){
			System.out.print("[");
			for(int c = 0; c < grid[r].length - 1; c++){
				System.out.print(grid[r][c] + ", ");
			}
			System.out.println(grid[r][grid[r].length - 1] + "]");
		}
	}
	
	public static void printGridI(int[][] grid){
		String result = "";
		for(int r = 0; r < grid.length; r++){
			result += "[";
			for(int c = 0; c < grid[r].length - 1; c++){
				result += grid[r][c] + ", ";
			}
			result += grid[r][grid[r].length - 1] + "]\n";
		}
		System.out.print(result);
	}
}
