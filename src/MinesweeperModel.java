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
		mines = minesToPlace;
		gameLayout = new int[rows][columns];
		playersViewLayout = new char[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				playersViewLayout[i][j] = '-';
			}
		}
		placeMines();
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
}
