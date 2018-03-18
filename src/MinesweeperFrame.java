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
	private static final ImageIcon FLAG_ICON = new ImageIcon("flag_icon.png");
	private static final ImageIcon MINE_ICON = new ImageIcon("mine_icon.png");
	private static final Color[] GAME_COLORS = {Color.BLUE, Color.WHITE};
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
		flagsPlaced = new JLabel();
		setFlagsPlacedText();
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
			curSpot.setBackground(GAME_COLORS[0]);
			//curSpot.setEnabled(false); TODO
		}
	}
	
	private void gameOverActions() {
		String message;
		if(minesweeperModel.gameEndedInVictory()) {
			message = "Victory!";
		} else {
			for(Integer[] spot : minesweeperModel.allMineLocations()) {
				GridSpot curSpot = gridSpots[spot[0]][spot[1]];
				Image mine = MINE_ICON.getImage().getScaledInstance(-1, curSpot.getHeight(),
						Image.SCALE_SMOOTH);
				curSpot.setIcon(new ImageIcon(mine));
			}
			message = "Game Over";
		}
		
		if(JOptionPane.showConfirmDialog(this, message, "Play again?", JOptionPane.YES_NO_OPTION)
				== JOptionPane.YES_OPTION) { //play again
			minesweeperModel = new MinesweeperModel(EASY[0], EASY[1], EASY[2]);
			setFlagsPlacedText();
			for(int r = 0; r < gridSpots.length; r++) {
				for(int c = 0; c < gridSpots[0].length; c++) {
					GridSpot cur = gridSpots[r][c];
					cur.setIcon(null);
					cur.setText("");
					cur.setBackground(null);
				}
			}
		}
	}
	
	private void setFlagsPlacedText() {
		flagsPlaced.setText("Flags: " + minesweeperModel.flagsPlaced() + "/" + EASY[2]);
	}
	
	private class GridSpot extends JButton {
		private static final int FONT_SIZE = 40;
		private static final String FONT_NAME = "Arial";
		
		public GridSpot(int row, int col) {
			setOpaque(true);
			setRolloverEnabled(false);
			setForeground(GAME_COLORS[1]);
			setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
			
			
			addActionListener(e -> {
				if(!minesweeperModel.isGameOver()) {
					if(placeFlag.isSelected()) {
						if(minesweeperModel.flagsPlaced() < EASY[2]) {
							if(minesweeperModel.spotIsFlagged(row, col)) {
								minesweeperModel.unflagSpot(row, col);
								setIcon(null);
							} else if(minesweeperModel.spotCanBeRevealed(row, col)) {
								minesweeperModel.flagSpot(row, col);
								Image flag = FLAG_ICON.getImage().getScaledInstance(-1, getHeight(),
										Image.SCALE_SMOOTH);
								setIcon(new ImageIcon(flag));
							}
						}
					} else if(minesweeperModel.spotCanBeRevealed(row, col)){
						//setEnabled(false); TODO it's changing the font color
						Map<Integer[], Integer> affectedSpots = minesweeperModel.revealSpot(row, col);
						if(affectedSpots == null) {
							gameOverActions();
						} else {
							showSeaAroundZeroes(affectedSpots);
							if(minesweeperModel.isGameOver()) {
								gameOverActions();
							}
						}
					}
					setFlagsPlacedText();
				}
			});
		}
	}
}
