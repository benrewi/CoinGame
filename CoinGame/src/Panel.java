import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList; 
import java.util.Random;

public class Panel extends JPanel implements ActionListener, KeyListener{
	
	private final static Color BACKGROUND_COLOR = Color.BLACK;
	private final static int TIMER_DELAY = 16;
	private final Timer timer;
	private GameState gameState = GameState.INITIALISING;
	private static final int HUD_HEIGHT = 60;
	
	//player attributes
	private Player player;
	private final int PLAYER_SPEED = 5;
	private boolean up, down, left, right;
	
	//Enemy movements
	private static final int ENEMY_COUNT = 10;
	private static final int ENEMY_SPEED = 3;
	private static final int ENEMY_SIZE = 25;
	private static final int SAFE_ZONE = 150;
	private final Random rng = new Random();
	private final List<Enemy> enemies = new ArrayList<>();
	
	//Coins
	private static final int COIN_COUNT = 10;
	private static final int COIN_EDGE = 12;
	private static final int COIN_SAFETY = 140;
	private final List<Coin> coins = new ArrayList<>();
	
	//HUD Text
	private static final Font HUD_FONT = new Font("SansSerif", Font.BOLD, 18);
	private static final String HEART = "\u2661";
	
	
	
    public Panel() {
        setBackground(BACKGROUND_COLOR);
        this.timer = new Timer(TIMER_DELAY, this);
        this.timer.start();
        addKeyListener(this);
        setFocusable(true);
    }
    

    
    public void createObjects() {
    	player = new Player(getWidth(), getHeight());
    	up = down = left = right = false;
    	
    	int playH = Math.max(0,  getHeight() - HUD_HEIGHT);
    	int startX = Math.max(0,  getWidth() / 2 - player.getWidth() / 2);
    	int startY = HUD_HEIGHT + Math.max(0,  (playH - player.getHeight()) / 2);
    	
    	player.setInitialPosition(startX,  startY);
    	player.resetToInitialPosition();

    	player.setXVelocity(0);
    	player.setYVelocity(0);
    	
    	enemies.clear();
    	for(int i = 0; i < ENEMY_COUNT; i++) {
    		int x,y;
    		while (true) {
    			x = rng.nextInt(Math.max(1, getWidth()  - ENEMY_SIZE));
    			y = HUD_HEIGHT + rng.nextInt(Math.max(1, getHeight()- HUD_HEIGHT - ENEMY_SIZE));
    			if (isFarFromPlayer(x, y, ENEMY_SIZE, ENEMY_SIZE, player, SAFE_ZONE)) {
    				break;
    			}
    	}
    		int[] v = randomVelocity(ENEMY_SPEED);
    		enemies.add(new Enemy(x, y, v[0], v[1]));
    	}
    	coins.clear();
    	for(int i = 0; i < COIN_COUNT; i++) {
    		int x, y;
    		while (true) {
    			x = COIN_EDGE + rng.nextInt(Math.max(1,  getWidth() - Coin.COIN_SIZE - 2 * COIN_EDGE));
    			y = HUD_HEIGHT + COIN_EDGE + rng.nextInt(Math.max(1, getHeight() - HUD_HEIGHT - Coin.COIN_SIZE - 2 * COIN_EDGE));
    			if (isFarFromPlayer(x, y, Coin.COIN_SIZE, Coin.COIN_SIZE, player, COIN_SAFETY)) {
    				break;
    			}
    		}
    		coins.add(new Coin(x, y));
    	}
    }
    
    private void drawHUD(Graphics g) {
        if (gameState == GameState.INITIALISING) return; 
        g.setFont(HUD_FONT);
        g.setColor(Color.WHITE);

        int padding = 12;
        int baseline = HUD_HEIGHT - 12; 

        int hp = (player != null) ? Math.max(0, player.getHealth()) : 0;
        String hearts = HEART.repeat(hp); 
        g.drawString("Health: " + hearts, padding, baseline);

        int collected = COIN_COUNT - coins.size();
        String coinsText = "Coins: " + collected + "/" + COIN_COUNT;

        int coinsWidth = g.getFontMetrics().stringWidth(coinsText);
        g.drawString(coinsText, getWidth() - padding - coinsWidth, baseline);
    }
    
    boolean gameInitialised = false;
    
    public void update() {
    	switch(gameState) {
    	case INITIALISING:{
    		// TODO
    		break;
    	}
    	
    	case PLAYING: {
    		if (player != null) {
    			player.setXPosition(player.getXPosition() + player.getXVelocity(), getWidth());
    			player.setYPositionWithin(player.getYPosition() + player.getYVelocity(), HUD_HEIGHT, getHeight());
    		}
    		for (Enemy e : enemies) {
    			e.setXPosition(e.getXPosition() + e.getXVelocity(), getWidth());
    			e.setYPositionWithin(e.getYPosition() + e.getYVelocity(), HUD_HEIGHT, getHeight());
    			bounceEnemyAtWalls(e);
    		}
    		break;
    	}
    	
    	case GAME_WON: {
    		//TODO
    		break;
    	}
    	
    	case GAME_OVER: {
    		//TODO
    		break;
    		}
    	}
    }
    
	private boolean isFarFromPlayer(int x, int y, int w, int h, Player p, int minDist) {
    	int cx = x + w / 2;
    	int cy = y + h / 2;
    	int px = p.getXPosition() + p.getWidth() / 2;
    	int py = p.getYPosition() + p.getHeight() / 2;
    	int dx = cx - px, dy = cy - py;
    	return dx * dx + dy * dy >= minDist * minDist;
    }

    private int[] randomVelocity(int speed) {
    	int[] choices = {-speed, 0, speed};
    	int vx, vy;
    	do {
    		vx = choices[rng.nextInt(choices.length)];
    		vy = choices[rng.nextInt(choices.length)];
    	} while (vx == 0 && vy == 0); // must move
    	return new int[] {vx, vy};
    }
    
    private void bounceEnemyAtWalls(Enemy e) {
    	if (e.getXPosition() <= 0 || e.getXPosition() + e.getWidth() >= getWidth()) {
    		e.setXVelocity(-e.getXVelocity());
    	}
    	if (e.getYPosition() <= HUD_HEIGHT || e.getYPosition() + e.getHeight() >= getHeight()) {
    		e.setYVelocity(-e.getYVelocity());
    	}
    }

    	
    
    private void startGame() {
    	createObjects();
    	gameState = GameState.PLAYING;
    }
    
    private void setGameWon() {
    	gameState = GameState.GAME_WON;
    }
    
    private void setGameOver() {
    	gameState = GameState.GAME_OVER;
    }
    
    private void resetToInitialising() {
    	player = null;
    	enemies.clear();
    	coins.clear();
    	gameState = GameState.INITIALISING;
    	requestFocusInWindow();
    }
    
    private void paintSprite(Graphics g, Sprite sprite) {
    	g.setColor(sprite.getColor());
    	g.fillRect(sprite.getXPosition(), sprite.getYPosition(), sprite.getWidth(), sprite.getHeight());
    }
    
    private void paintCoin(Graphics g, Coin c) {
    	g.setColor(c.getColor()); 
    	g.fillOval(c.getXPosition(), c.getYPosition(), c.getWidth(), c.getHeight());
    }   
    

    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	//painting the game score area
    	boolean showHUD = (gameState != GameState.INITIALISING);
    	if(showHUD){
	    	g.setColor(Color.BLACK);
	    	g.fillRect(0, 0, getWidth(), HUD_HEIGHT);
	    	
	    	//adding a line to make game play area identifiable
	    	g.setColor(Color.WHITE);
	    	g.drawLine(0, HUD_HEIGHT, getWidth(), HUD_HEIGHT);
	    	
	    	drawHUD(g);
    	}
    	
        switch (gameState) {
        	case INITIALISING: {
        		drawCenteredText(g, "Press SPACE to start!");
        		break;
        	}
        	case PLAYING: {
        		for (Coin c : coins) {
        			paintCoin(g, c);
        		}
        		
        		for(Enemy e : enemies) {
        			paintSprite(g, e);
        		}
        	
        		if (player != null) {
        			paintSprite(g, player);
        		}
        		break;
        	}
        	case GAME_WON: {
        		drawCenteredText(g, "You Win! Press Space to restart");
        		break;
        	}
        	case GAME_OVER: {
        		drawCenteredText(g, "GAME OVER! Press Space to restart");
        		break;
        	}
        }
    }
    
    private static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 22);

    private void drawCenteredText(Graphics g, String text) {
        g.setFont(MESSAGE_FONT);
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(text, x, y);
    }
    
    private void movePlayer() {
    	if (player == null) return;
    	int vx = 0, vy = 0;
    	if (left) vx -= PLAYER_SPEED;
    	if (right) vx += PLAYER_SPEED;
    	if (up) vy -= PLAYER_SPEED;
    	if (down) vy += PLAYER_SPEED;
    	player.setXVelocity(vx);
    	player.setYVelocity(vy);
    	
    }

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(gameState == GameState.INITIALISING && code == KeyEvent.VK_SPACE) {
			startGame();
			return;
		}
		if ((gameState == GameState.GAME_WON || gameState == GameState.GAME_OVER) && code == KeyEvent.VK_SPACE) {
			resetToInitialising();
			return;
		}
		
		//temporary
		if(gameState == GameState.PLAYING && player != null) {
			if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) left  = true;
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) right = true;
			if (code == KeyEvent.VK_UP    || code == KeyEvent.VK_W) up    = true;
			if (code == KeyEvent.VK_DOWN  || code == KeyEvent.VK_S) down  = true;
			movePlayer();
			if(code == KeyEvent.VK_Z) setGameWon();
			if(code == KeyEvent.VK_X) setGameOver();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
	
		if (gameState == GameState.PLAYING && player != null) {
			if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) left  = false;
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) right = false;
			if (code == KeyEvent.VK_UP    || code == KeyEvent.VK_W) up    = false;
			if (code == KeyEvent.VK_DOWN  || code == KeyEvent.VK_S) down  = false;
			movePlayer();
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		update();
		repaint();
		
	}
}
