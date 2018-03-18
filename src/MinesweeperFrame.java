import java.awt.*;
import javax.swing.*;

/**
 * MinesweeperFrame can be used to control and display a game of Minesweeper
 * @author Yael Goldin
 */
@SuppressWarnings("serial")
public class MinesweeperFrame extends JFrame {
	private MinesweeperModel minesweeperModel;
	private GridSpot[][] gridSpots;
	private static final Color[] GAME_COLORS = {Color.CYAN, Color.BLUE, Color.WHITE};
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
	}
	
	private class GridSpot extends JButton {
		private final int row;
		private final int col;
		
		public GridSpot(int row, int col) {
			this.row = row;
			this.col = col;
			setBackground(GAME_COLORS[0]);
			setOpaque(true);
			setRolloverEnabled(false);
		}
	}
}
