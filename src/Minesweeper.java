import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;

/* if you're looking at a spot x and the spots around it, the directions are
 * 123
 * 8x4
 * 765
 */

public class Minesweeper {
	public final static int startX = 50, startY = 75;
	public final static int gridSize = 60;
	private static DrawingPanel MinesweeperGrid;
	private static Graphics graphic;
	
	
	public static void main(String[] args){
		Scanner input = new Scanner(System.in);
		int difficulty = isInRange(1, 3, "Do you want to play easy(1), medium(2), or hard(3)? ", input);
		int WIDTH = 10, HEIGHT = 10, MINES = 10;
		if(difficulty == 2){
			WIDTH = 14;
			HEIGHT = 14;
			MINES = 30;
		} else if(difficulty == 3){
			WIDTH = 20;
			HEIGHT = 15;
			MINES = 50;
		}
		MinesweeperGrid = new DrawingPanel(WIDTH*60 + 100, HEIGHT*60 + 150);
		graphic = MinesweeperGrid.getGraphics();
		Color LightBlue = new Color(44, 145, 230);
		graphic.setColor(LightBlue);
		graphic.fillRect(startX, startY, WIDTH*60, HEIGHT*60);
		graphic.setColor(Color.BLACK);
		graphic.drawRect(startX, startY, WIDTH*60, HEIGHT*60);
		graphic.setFont(new Font("Title", Font.BOLD, 40));
		graphic.drawString("MINESWEEPER", WIDTH*30 - 120, 45);
		Font numbering = new Font("Numbering", Font.PLAIN, 25);
		Font info = new Font("Columns", Font.PLAIN, 20);
		graphic.setFont(new Font("Exterior Numbers", Font.PLAIN, 15));
		for(int h = 0; h < WIDTH; h++){
			graphic.drawString("" + (h+1), startX + 60*h + 22, startY - 5);
			for(int v = 0; v < HEIGHT; v++){
				graphic.drawRect(startX + 60*h, startY + 60*v, 60, 60);
			}
		}
		for(int v = 0; v < HEIGHT; v++){
			graphic.drawString("" + (v+1), startX - 25, startY + 60*v + 37);
			graphic.drawString("" + (v+1), startX + 60*WIDTH + 15, startY + 60*v + 37);
		}
		graphic.setFont(info);
		graphic.drawString("Flags Placed:", WIDTH*30 - 25, HEIGHT*60 + 100);
		graphic.drawString("0", WIDTH*30 +110, HEIGHT*60 + 100);
		graphic.drawString("Mines: " + MINES, WIDTH*30, HEIGHT*60 + 125);
		
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
		boolean win = false, lose = false;
		int spotsOpen = WIDTH*HEIGHT, flagsPlaced = 0;
		//if you have a 10x10 grid, there are 100 spots of which 10 are mines
		//after every new spot is opened by the player, the spot count decreases
		//when there are only 10 spots left, the player has found all mines and wins
		while(win == false && lose == false){
			int col = isInRange(1, WIDTH, "Which column 1-" + WIDTH + ": ", input) - 1;
			int row = isInRange(1, HEIGHT, "Which row 1-" + HEIGHT + ": ", input) - 1;
			//it subtracts 1 because arrays start at index 0 not 1 like we ask the player
			System.out.print("Do you want to flag/unflag this spot as a mine or click? "
					+ "Anything other than 'flag' will mean click: ");
			String toFlag = input.next();
			graphic.setColor(Color.BLACK);
			graphic.setFont(numbering);
			if(game[row][col] == '-'){
				if(!toFlag.equals("flag")){
					spotsOpen --; //revealing new spot
					game[row][col] = gridRep[row][col];
					drawNum(game, row, col);
					if(grid[row][col] == -1){ //spot chosen is a mine
						graphic.setColor(Color.WHITE);
						graphic.fillRect(startX + 60*col + 15, startY + 60*row + 15, 28, 30);
						graphic.setColor(Color.RED);
						drawNum(game, row, col);
						//fill in the rest of the grid
						for(int r = 0; r < HEIGHT; r++){
							for(int c = 0; c < WIDTH; c++){
								if(game[r][c] == '-' || game[r][c] == 'F'){
									if(gridRep[r][c] == 'X'){
										graphic.setColor(Color.RED);
									} else {
										graphic.setColor(Color.BLACK);
									}
									drawNum(gridRep, r, c);
								}
							}
						}
						
						System.out.println();
						System.out.println("Game over");
						lose = true;
					}
				} else { //flagging it as a mine
					game[row][col] = 'F';
					flagsPlaced++;
					graphic.setColor(Color.GREEN);
					drawNum(game, row, col);
					changeFlagsPlaced(flagsPlaced, WIDTH, HEIGHT, info);
				}
			} else if(game[row][col] == 'F'){
				if(toFlag.equals("flag")){ //removing flag
					game[row][col] = '-';
					flagsPlaced--;
					graphic.setColor(LightBlue);
					graphic.fillRect(startX + 60*col + 10, startY + 60*row + 10, 25, 30);
					drawNum(game, row, col);
					changeFlagsPlaced(flagsPlaced, WIDTH, HEIGHT, info);
				} else {
					System.out.println("You cannot click a flagged spot");
				}
			}
			
			if (game[row][col] == '0'){
				//if the spot has 0 mines around it, all of them can be revealed
				checkZeroes(game, gridRep, row, col, WIDTH, HEIGHT);
				spotsOpen = WIDTH*HEIGHT;
				for(int r = 0; r < HEIGHT; r++){
					for(int c = 0; c < HEIGHT; c++){
						if(game[r][c] != '-' && game[r][c] != 'F'){
							spotsOpen--;
						}
					}
				}
			}
			
			if(spotsOpen == MINES){
				//if the only spots left are the mines, the player wins
				System.out.println();
				System.out.println("You win!");
				win = true;
			}
		}
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
		results = add3(a, b, c, results);
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
	
	public static void drawNum(char[][] game, int r, int c){
		graphic.drawString("" + game[r][c], startX + 60*c + 20, startY + 60*r + 40);
	}
	
	public static void checkZeroes(char[][] game, char[][] gridRep, int startR, int startC, int WIDTH, int HEIGHT){
		//the initial spot [startR][startC] has 0 mines around it
		//so this will recursively reveal every spot around it and every spot around that if it is a 0, and so on
		ArrayList<Integer> placeZero = whichDirections(startR, startC, WIDTH, HEIGHT);
		for(int num : placeZero){
			//some of the spots around the 0 might have already been revealed, so they
			//do not need to be re-revealed and the spot count should not decrease if
			//they have already been revealed
			if (num == 1 && game[startR-1][startC-1] == '-'){
				duringZeroes(game, gridRep, startR-1, startC-1, WIDTH, HEIGHT);
			} else if (num == 2 && game[startR-1][startC] == '-'){
				duringZeroes(game, gridRep, startR-1, startC, WIDTH, HEIGHT);
			} else if (num == 3 && game[startR-1][startC+1] == '-'){
				duringZeroes(game, gridRep, startR-1, startC+1, WIDTH, HEIGHT);
			} else if (num == 4 && game[startR][startC+1] == '-'){
				duringZeroes(game, gridRep, startR, startC+1, WIDTH, HEIGHT);
			} else if (num == 5 && game[startR+1][startC+1] == '-'){
				duringZeroes(game, gridRep, startR+1, startC+1, WIDTH, HEIGHT);
			} else if (num == 6 && game[startR+1][startC] == '-'){
				duringZeroes(game, gridRep, startR+1, startC, WIDTH, HEIGHT);
			} else if (num == 7 && game[startR+1][startC-1] == '-'){
                //game[startR+1][startC-1] = gridRep[startR+1][startC-1];
                //drawNum(game, startR+1, startC-1);
				duringZeroes(game, gridRep, startR+1, startC-1, WIDTH, HEIGHT);
			} else if (num == 8 && game[startR][startC-1] == '-'){
				duringZeroes(game, gridRep, startR, startC-1, WIDTH, HEIGHT);
                //game[startR][startC-1] = gridRep[startR][startC-1];
                //drawNum(game, startR, startC-1);
                
			}
		}
	}
	
	public static void duringZeroes(char[][] game, char[][] gridRep, int r, int c, int WIDTH, int HEIGHT){
		game[r][c] = gridRep[r][c];
        drawNum(game, r, c);
        if(game[r][c] == '0'){
        	checkZeroes(game, gridRep, r, c, WIDTH, HEIGHT);
        }
	}
	
	public static void changeFlagsPlaced(int flagsPlaced, int WIDTH, int HEIGHT, Font info){
		graphic.setColor(Color.WHITE);
		graphic.fillRect(WIDTH*30 +105, HEIGHT*60 + 85, 30, 30);
		graphic.setColor(Color.BLACK);
		graphic.setFont(info);
		graphic.drawString("" + flagsPlaced, WIDTH*30 +110, HEIGHT*60 + 100);
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
