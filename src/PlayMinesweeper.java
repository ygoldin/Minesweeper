import java.awt.EventQueue;

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
