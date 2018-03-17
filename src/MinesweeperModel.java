import java.util.Random;

public class MinesweeperModel {
	private int[][] gameLayout;
	private char[][] playersViewLayout;
	private int mines;
	private static final int MINE_IDENTIFIER = -1;
	
	public MinesweeperModel(int rows, int columns, int minesToPlace) {
		if(rows <= 0 || columns <= 0 || minesToPlace > rows*columns) {
			throw new IllegalArgumentException();
		}
		
		gameLayout = new int[rows][columns];
		playersViewLayout = new char[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				playersViewLayout[i][j] = '-';
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
	
	private boolean inBounds(int row, int col) {
		return row >= 0 && row < gameLayout.length && col >= 0 && col < gameLayout[0].length;
	}
	
	
}
