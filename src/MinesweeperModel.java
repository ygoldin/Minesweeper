import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MinesweeperModel {
	private int[][] gameLayout;
	private char[][] playersViewLayout;
	private int mines;
	private int flagsPlaced;
	private int uncoveredSpots;
	private boolean mineClicked;
	
	private static final int MINE_IDENTIFIER = -1;
	private static final char UNCLICKED_IDENTIFIER = '-';
	private static final char FLAGGED_IDENTIFIER = 'f';
	public static final double MINE_PERCENT_MAX = 0.5;
	
	/**
	 * initializes the state of the game
	 * 
	 * @param rows How many rows the grid should be
	 * @param columns How many columns the grid should be
	 * @param minesToPlace How many mines to place in the grid
	 * @throws IllegalArgumentException if rows <= 0, columns <= 0, or there are more mines to place than
	 * MINE_PERCENT_MAX of the grid area
	 */
	public MinesweeperModel(int rows, int columns, int minesToPlace) {
		if(rows <= 0 || columns <= 0 || minesToPlace > MINE_PERCENT_MAX*rows*columns) {
			throw new IllegalArgumentException();
		}
		
		gameLayout = new int[rows][columns];
		playersViewLayout = new char[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				playersViewLayout[i][j] = UNCLICKED_IDENTIFIER;
			}
		}
		
		mines = minesToPlace;
		placeMines();
		
		for(int r = 0; r < gameLayout.length; r++) {
			for(int c = 0; c < gameLayout[0].length; c++) {
				if(gameLayout[r][c] != MINE_IDENTIFIER) {
					gameLayout[r][c] = identifySurroundingMines(r, c);
				}
			}
		}
	}
	
	//randomly places the mines on the grid
	private void placeMines() {
		Random randomMinePlacer = new Random();
		int minesPlaced = 0;
		while(minesPlaced < mines){
			int mineRow = randomMinePlacer.nextInt(gameLayout.length);
			int mineCol = randomMinePlacer.nextInt(gameLayout[0].length);
			//ensures two mines aren't placed in the same spot
			if(gameLayout[mineRow][mineCol] != MINE_IDENTIFIER){
				gameLayout[mineRow][mineCol] = MINE_IDENTIFIER;
				minesPlaced++;
			}
		}
	}
	
	//identifies how many mines surround the given spot in the grid
	private int identifySurroundingMines(int row, int col) {
		assert(gameLayout[row][col] != MINE_IDENTIFIER);
		int sum = 0;
		for(int i = row - 1; i <= row + 1; i++) {
			for(int j = col - 1; j <= col + 1; j++) {
				if(inBounds(i, j) && gameLayout[i][j] == MINE_IDENTIFIER) {
					sum++;
				}
			}
		}
		return sum;
	}
	
	/**
	 * "clicks" the spot on the grid
	 * 
	 * @param row The row value of the spot
	 * @param col The column value of the spot
	 * @return a map from all affected spots by this "click" (in the form [row, col]) to the numbers
	 * revealed in those spots, or null if this spot was a mine
	 * @throws IllegalStateException if the game is over
	 * @throws IllegalArgumentException if the spot is out of bounds or has already been revealed/flagged
	 */
	public Map<Integer[], Integer> revealSpot(int row, int col) {
		exceptionIfGameIsOver();
		if(!spotCanBeRevealed(row, col)) {
			throw new IllegalArgumentException("unclickable spot");
		}
		if(gameLayout[row][col] == MINE_IDENTIFIER) {
			mineClicked = true;
			return null;
		}
		
		Map<Integer[], Integer> affected = new HashMap<>();
		revealSeaAroundZeroes(row, col, affected);
		return affected;
	}
	
	//updates the map with all of the revealed spots around the given one
	private void revealSeaAroundZeroes(int row, int col, Map<Integer[], Integer> affected) {
		if(inBounds(row, col) && playersViewLayout[row][col] == UNCLICKED_IDENTIFIER) {
			uncoveredSpots++;
			int spotValue = gameLayout[row][col];
			if(playersViewLayout[row][col] == FLAGGED_IDENTIFIER) {
				flagsPlaced--;
			}
			playersViewLayout[row][col] = (char) (spotValue - '0');
			affected.put(new Integer[]{row, col}, spotValue);
			
			if(spotValue == 0) {
				for(int r = row - 1; r <= row + 1; r++) {
					for(int c = col - 1; c <= col + 1; c++) {
						if(r != row || c != col) { //don't look at current spot again
							revealSeaAroundZeroes(r, c, affected);
						}
					}
				}
			}
		}
	}
	
	/**
	 * flags the given spot
	 * 
	 * @param row The row of the spot
	 * @param col The column of the spot
	 * @throws IllegalStateException if the game is over
	 * @throws IllegalArgumentException if the spot is out of bounds, already revealed, or already
	 * flagged
	 */
	public void flagSpot(int row, int col) {
		exceptionIfGameIsOver();
		if(!spotCanBeRevealed(row, col)) {
			throw new IllegalArgumentException("spot already revealed");
		} else if(spotIsFlagged(row, col)) {
			throw new IllegalArgumentException("spot already flagged");
		}
		flagsPlaced++;
		playersViewLayout[row][col] = FLAGGED_IDENTIFIER;
	}
	
	/**
	 * flags the given spot
	 * 
	 * @param row The row of the spot
	 * @param col The column of the spot
	 * @throws IllegalStateException if the game is over
	 * @throws IllegalArgumentException if the spot is out of bounds or not flagged
	 */
	public void unflagSpot(int row, int col) {
		exceptionIfGameIsOver();
		if(!spotIsFlagged(row, col)) {
			throw new IllegalArgumentException("can only unflag spots that are flagged");
		}
		flagsPlaced--;
		playersViewLayout[row][col] = UNCLICKED_IDENTIFIER;
	}
	
	/**
	 * checks if the spot can be uncovered
	 * 
	 * @param row The row of the spot
	 * @param col The column of the spot
	 * @return true if the spot can be uncovered, false otherwise
	 * @throws IllegalArgumentException if the spot is out of bounds
	 */
	public boolean spotCanBeRevealed(int row, int col) {
		exceptionIfOutOfBounds(row, col);
		return playersViewLayout[row][col] == UNCLICKED_IDENTIFIER;
	}
	
	/**
	 * checks if the spot is flagged
	 * 
	 * @param row The row of the spot
	 * @param col The column of the spot
	 * @return true if the spot is flagged, false otherwise
	 * @throws IllegalArgumentException if the spot is out of bounds
	 */
	public boolean spotIsFlagged(int row, int col) {
		exceptionIfOutOfBounds(row, col);
		return playersViewLayout[row][col] == FLAGGED_IDENTIFIER;
	}
	
	/**
	 * @return how many flags are placed
	 */
	public int flagsPlaced() {
		return flagsPlaced;
	}
	
	/**
	 * @return true if the game is over, false otherwise
	 */
	public boolean isGameOver() {
		return mineClicked || uncoveredSpots == gameLayout.length*gameLayout[0].length - mines;
	}
	
	/**
	 * checks if the game ended because of a mine being clicked or the user winning
	 * 
	 * @return true if the user won, false if a mine was clicked
	 * @throws IllegalStateException if the game isn't over
	 */
	public boolean gameWon() {
		if(!isGameOver()) {
			throw new IllegalStateException("game isn't over");
		}
		return !mineClicked;
	}
	
	//returns true if the given spot is in bounds of the grid, false otherwise
	private boolean inBounds(int row, int col) {
		return row >= 0 && row < gameLayout.length && col >= 0 && col < gameLayout[0].length;
	}
	
	//throws an IllegalArgumentException if the spot is out of bounds
	private void exceptionIfOutOfBounds(int row, int col) {
		if(!inBounds(row, col)) {
			throw new IllegalArgumentException("not in bounds");
		}
	}
	
	//throws an IllegalStateException if the game is over
	private void exceptionIfGameIsOver() {
		if(isGameOver()) {
			throw new IllegalStateException("game is over");
		}
	}
}
