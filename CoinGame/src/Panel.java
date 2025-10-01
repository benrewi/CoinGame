import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Graphics;

public class Panel extends JPanel implements ActionListener, KeyListener{
	
	private final static Color BACKGROUND_COLOR = Color.BLACK;
	private final static int TIMER_DELAY = 5;
	
    public Panel() {
        setBackground(BACKGROUND_COLOR);
        Timer timer = new Timer(TIMER_DELAY, this);
        timer.start();
    }
    
    public void update() {
    	
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(20,  20,  100,  100);
    }

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
