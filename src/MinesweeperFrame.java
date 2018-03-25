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
	private FlagToggle placeFlag;
	private JLabel flagsPlaced;
	private JButton help;
	private int difficulty;
	private final ImageIcon FLAG_ICON;
	private final ImageIcon MINE_ICON;
	private static final Color[] GAME_COLORS = {Color.BLUE, Color.WHITE};
	private static final int[][] DIFFICULTY_VALUES = {{10, 10, 10}, {16, 16, 40}, {30, 16, 99}};
	
	/**
	 * initializes the frame
	 */
	public MinesweeperFrame() {
		FLAG_ICON = new ImageIcon(getClass().getResource("/images/flag_icon.png"));
		MINE_ICON = new ImageIcon(getClass().getResource("/images/mine_icon.png"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1024, 768));
		setTitle("Minesweeper");
		minesweeperModel = new MinesweeperModel(DIFFICULTY_VALUES[difficulty][0],
				DIFFICULTY_VALUES[difficulty][1], DIFFICULTY_VALUES[difficulty][2]);
		
		gridSpots = new GridSpot[DIFFICULTY_VALUES[difficulty][0]][DIFFICULTY_VALUES[difficulty][1]];
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(gridSpots.length, gridSpots[0].length));
		for(int r = 0; r < gridSpots.length; r++) {
			for(int c = 0; c < gridSpots[0].length; c++) {
				gridSpots[r][c] = new GridSpot(r, c);
				buttonPanel.add(gridSpots[r][c]);
			}
		}
		add(buttonPanel);
		setupMenu();		
	}
	
	//sets up the menu bar with the flag button, flag tally, and help button
	private void setupMenu() {
		JMenuBar menu = new JMenuBar();
		setJMenuBar(menu);
		placeFlag = new FlagToggle("Flag");
		menu.add(placeFlag);
		menu.add(Box.createHorizontalGlue());
		
		flagsPlaced = new JLabel();
		setFlagsPlacedText();
		menu.add(flagsPlaced);
		menu.add(Box.createHorizontalGlue());
		
		help = new JButton("Help");
		menu.add(help);
		help.addActionListener(e -> {
			String message = "Click on the flag button to toggle the flagging ability on/off\n";
			message += "If it's off, clicking on a square will reveal it\n";
			message += "If it's on, clicking on a square will flag it, making it impossible to reveal\n";
			message += "If a square is already flagged, it will instead unflag it";
			JOptionPane.showMessageDialog(this, message, "Game Rules", JOptionPane.INFORMATION_MESSAGE);
		});
	}
	
	//reveals all of the spots in the map and what their danger counts are
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
		}
	}
	
	//performs the end of game actions, including resetting the game if necessary
	private void gameOverActions() {
		String message;
		if(minesweeperModel.gameEndedInVictory()) {
			message = "Victory!";
		} else {
			for(Integer[] spot : minesweeperModel.allMineLocations()) {
				GridSpot curSpot = gridSpots[spot[0]][spot[1]];
				curSpot.setIcon(scaledIcon(MINE_ICON, curSpot, -1));
			}
			message = "Game Over";
		}
		
		if(JOptionPane.showConfirmDialog(this, message, "Play again?", JOptionPane.YES_NO_OPTION)
				== JOptionPane.YES_OPTION) { //play again
			minesweeperModel = new MinesweeperModel(DIFFICULTY_VALUES[difficulty][0],
					DIFFICULTY_VALUES[difficulty][1], DIFFICULTY_VALUES[difficulty][2]);
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
	
	//returns a scaled version of the icon based on the parent and desired height
	private Icon scaledIcon(ImageIcon icon, JComponent parent, int height) {
		if(parent != null) {
			height = parent.getHeight();
		}
		Image scaled = icon.getImage().getScaledInstance(-1, height, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
	
	//updates the display of the number of flags the user has placed
	private void setFlagsPlacedText() {
		flagsPlaced.setText("Flags: " + minesweeperModel.flagsPlaced() + "/" +
				DIFFICULTY_VALUES[difficulty][2]);
	}
	
	//this class represents the toggle button for flagging
	private class FlagToggle extends JButton {
		private boolean toggled;
		private final Color TOGGLED_COLOR = Color.GREEN;
		private final int ICON_HEIGHT = 16; //TODO: fix random 16
		
		/**
		 * initializes the button with the given text
		 * 
		 * @param text The text to put in the button
		 */
		public FlagToggle(String text) {
			super(text);
			setIcon(scaledIcon(FLAG_ICON, null, ICON_HEIGHT));
			addActionListener(e -> {
				toggled = !toggled;
				if(toggled) {
					setBackground(TOGGLED_COLOR);
				} else {
					setBackground(null);
				}
			});
		}
		
		/**
		 * checks if the toggling mode is active
		 * 
		 * @return true if the button is toggled, false otherwise
		 */
		public boolean isToggled() {
			return toggled;
		}
	}
	
	//this class represents one spot on the grid
	private class GridSpot extends JButton {
		private static final int FONT_SIZE = 40;
		private static final String FONT_NAME = "Arial";
		
		/**
		 * constructs a spot representing the row/column location on the grid
		 * 
		 * @param row The row value of the spot
		 * @param col The column value of the spot
		 */
		public GridSpot(int row, int col) {
			setOpaque(true);
			setRolloverEnabled(false);
			setForeground(GAME_COLORS[1]);
			setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
			
			addActionListener(e -> {
				if(!minesweeperModel.isGameOver()) {
					if(placeFlag.isToggled()) {
						if(minesweeperModel.flagsPlaced() < DIFFICULTY_VALUES[difficulty][2]) {
							if(minesweeperModel.spotIsFlagged(row, col)) {
								minesweeperModel.unflagSpot(row, col);
								setIcon(null);
							} else if(minesweeperModel.spotCanBeRevealed(row, col)) {
								minesweeperModel.flagSpot(row, col);
								setIcon(scaledIcon(FLAG_ICON, this, -1));
							}
						}
					} else if(minesweeperModel.spotCanBeRevealed(row, col)){
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
