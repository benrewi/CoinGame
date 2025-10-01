import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Panel extends JPanel implements ActionListener, KeyListener{
	
	private final static Color BACKGROUND_COLOR = Color.BLACK;
	private final static int TIMER_DELAY = 5;
	
    public Panel() {
        setBackground(BACKGROUND_COLOR);
        Timer timer = new Timer(TIMER_DELAY, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
    }
    
    boolean gameInitialised = false;
    Player player;
    
    public void createObjects() {
    	player = new Player(getWidth(), getHeight());
    }
    
    
    public void update() {
    	if(!gameInitialised) {
    		createObjects();
    		gameInitialised = true;
    	}
    }
    
    private void paintSprite(Graphics g, Sprite sprite) {
    	g.setColor(sprite.getColor());
    	g.fillRect(sprite.getXPosition(), sprite.getYPosition(), sprite.getWidth(), sprite.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(gameInitialised) {
        	paintSprite(g, player);
        }
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
		update();
		repaint();
		
	}
}
