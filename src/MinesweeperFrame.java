import java.awt.*;
import java.util.Map;
import javax.swing.*;

/**
 * MinesweeperFrame can be used to control and display a game of Minesweeper
 * @author Yael Goldin
 */
@SuppressWarnings("serial")
public class MinesweeperFrame extends JFrame {
	private MinesweeperModel minesweeperModel;
	private GridSpot[][] gridSpots;
	private JToggleButton placeFlag;
	private JLabel flagsPlaced;
	private static final Color[] GAME_COLORS = {Color.CYAN, Color.BLUE, Color.WHITE, Color.RED};
	private static final int[] EASY = {10, 10, 10};
	
	public MinesweeperFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1024, 768));
		setTitle("Minesweeper");
		minesweeperModel = new MinesweeperModel(EASY[0], EASY[1], EASY[2]);
		
		gridSpots = new GridSpot[EASY[0]][EASY[1]];
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(gridSpots.length, gridSpots[0].length));
		for(int r = 0; r < gridSpots.length; r++) {
			for(int c = 0; c < gridSpots[0].length; c++) {
				gridSpots[r][c] = new GridSpot(r, c);
				buttonPanel.add(gridSpots[r][c]);
			}
		}
		add(buttonPanel);
		
		JMenuBar menu = new JMenuBar();
		placeFlag = new JToggleButton("Flag");
		menu.add(placeFlag);
		flagsPlaced = new JLabel("Flags: 0/" + EASY[2]);
		menu.add(flagsPlaced);
		setJMenuBar(menu);
	}
	
	private void showSeaAroundZeroes(Map<Integer[], Integer> affectedSpots) {
		for(Integer[] spot : affectedSpots.keySet()) {
			int dangerCount = affectedSpots.get(spot);
			GridSpot curSpot = gridSpots[spot[0]][spot[1]];
			if(dangerCount == 0) {
				curSpot.setText("");
			} else {
				curSpot.setText("" + dangerCount);
			}
			curSpot.setBackground(GAME_COLORS[1]);
			//curSpot.setEnabled(false); TODO
		}
	}
	
	private void gameOverActions() {
		if(minesweeperModel.gameEndedInVictory()) {
			//TODO victory message
		} else {
			for(Integer[] spot : minesweeperModel.allMineLocations()) {
				GridSpot curSpot = gridSpots[spot[0]][spot[1]];
				curSpot.setText("X");
				curSpot.setForeground(GAME_COLORS[3]);
			}
		}
	}
	
	private class GridSpot extends JButton {
		private static final int FONT_SIZE = 40;
		private static final String FONT_NAME = "Arial";
		
		public GridSpot(int row, int col) {
			setBackground(GAME_COLORS[0]);
			setOpaque(true);
			setRolloverEnabled(false);
			setForeground(GAME_COLORS[2]);
			setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
			
			
			addActionListener(e -> {
				if(!minesweeperModel.isGameOver()) {
					if(placeFlag.isSelected()) {
						if(minesweeperModel.spotIsFlagged(row, col)) {
							minesweeperModel.unflagSpot(row, col);
							setText("");
						} else if(minesweeperModel.spotCanBeRevealed(row, col)) {
							minesweeperModel.flagSpot(row, col);
							setText("F");
						}
					} else if(minesweeperModel.spotCanBeRevealed(row, col)){
						//setEnabled(false); TODO it's changing the font color
						Map<Integer[], Integer> affectedSpots = minesweeperModel.revealSpot(row, col);
						if(affectedSpots == null || minesweeperModel.isGameOver()) {
							MinesweeperFrame.this.gameOverActions();
						} else {
							MinesweeperFrame.this.showSeaAroundZeroes(affectedSpots);
						}
					}
					flagsPlaced.setText("Flags: " + minesweeperModel.flagsPlaced() + "/" + EASY[2]);
				}
			});
		}
	}
}
