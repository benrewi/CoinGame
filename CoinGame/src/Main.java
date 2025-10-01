import javax.swing.JFrame;

public class Main extends JFrame {
	
	public final static String WINDOW_TITLE = "Coin Game";
	public final static int WINDOW_WIDTH = 800;
	public final static int WINDOW_HEIGHT = 800;
	
	public Main() {
		setTitle(WINDOW_TITLE);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		add(new Panel());
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
	
	
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main();
			}
		});
	}
}
		

