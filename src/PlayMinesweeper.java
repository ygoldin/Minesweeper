import java.awt.EventQueue;

/**
 * PlayMinesweeper can be used to play an interactive game of Minesweeper
 * @author Yael Goldin
 */
public class PlayMinesweeper {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				MinesweeperFrame minesweeper = new MinesweeperFrame();
				minesweeper.pack();
				minesweeper.setVisible(true);
			}
		});
	}

}
