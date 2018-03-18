import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MinesweeperModel {
	private int[][] gameLayout;
	private char[][] playersViewLayout;
	private int mines;
	private int flagsPlaced;
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
	 * @throws IllegalArgumentException if the spot is out of bounds or has already been revealed/flagged
	 */
	public Map<Integer[], Integer> revealSpot(int row, int col) {
		if(!spotCanBeRevealed(row, col)) {
			throw new IllegalArgumentException("unclickable spot");
		}
		if(gameLayout[row][col] == MINE_IDENTIFIER) {
			return null;
		}
		
		Map<Integer[], Integer> affected = new HashMap<>();
		revealSeaAroundZeroes(row, col, affected);
		return affected;
	}
	
	private void revealSeaAroundZeroes(int row, int col, Map<Integer[], Integer> affected) {
		if(inBounds(row, col) && playersViewLayout[row][col] == UNCLICKED_IDENTIFIER) {
			int spotValue = gameLayout[row][col];
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
	
	public boolean spotCanBeRevealed(int row, int col) {
		if(!inBounds(row, col)) {
			throw new IllegalArgumentException("not in bounds");
		}
		return playersViewLayout[row][col] == UNCLICKED_IDENTIFIER;
	}
	
	//returns true if the given spot is in bounds of the grid, false otherwise
	private boolean inBounds(int row, int col) {
		return row >= 0 && row < gameLayout.length && col >= 0 && col < gameLayout[0].length;
	}
	
	
}
