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
	
	//Setting the attributes for the visuals and loop timing, and state
	private final static Color BACKGROUND_COLOR = Color.BLACK;
	private final static int TIMER_DELAY = 16;
	private final Timer timer;
	private GameState gameState = GameState.INITIALISING;
	private static final int HUD_HEIGHT = 60;
	
	//player attributes
	private Player player;
	private final int PLAYER_SPEED = 5;
	private boolean up, down, left, right;
	
	//Enemy movements and attributes
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
	
	//HUD
	private static final Font HUD_FONT = new Font("SansSerif", Font.BOLD, 18);
	private static final String HEART = "\u2661";
	private static final int FLASH_LAST_LIFE   = 90;
	private static final int FLASH_COOLDOWN    = 90;
	private static final int LAST_LIFE_TEXT    = 200;
	
	//starting score for coins
	private int score = 0;
	
	//health information
	private static final int DAMAGE_COOLDOWN = 3000;
	private long takenDamage = 0; 
	
	
	//setting up the main panel - background, timer and inputs
    public Panel() {
        setBackground(BACKGROUND_COLOR);
        this.timer = new Timer(TIMER_DELAY, this);
        this.timer.start();
        addKeyListener(this);
        setFocusable(true);
    }
    

    //Create/reset all of the game objects - make sure player is centered when starting, randomise enemies and coins
    public void createObjects() {
    	//create the player, and place at the middle of the playable area
    	player = new Player(getWidth(), getHeight());
    	up = down = left = right = false;
    	
    	int playH = Math.max(0,  getHeight() - HUD_HEIGHT);
    	int startX = Math.max(0,  getWidth() / 2 - player.getWidth() / 2);
    	int startY = HUD_HEIGHT + Math.max(0,  (playH - player.getHeight()) / 2);
    	
    	player.setInitialPosition(startX,  startY);
    	player.resetToInitialPosition();

    	player.setXVelocity(0);
    	player.setYVelocity(0);
    	
    	//resets score
    	score = 0;
    	
    	//clear the enemies and spawn them in random places, in a set distance from the player
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
    	
    	//clear the coins and place them in random spots in the playable area
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
    
    //coin collection - remove the coins when collected, and increase the score. If there are no coins left, the player wins
    private void collectCoins() {
    	if (player == null || coins.isEmpty())
    		return;
    		
    	//using an iteractor so we can safely remove the coins when collected
    		java.util.Iterator<Coin> it = coins.iterator();
    		while(it.hasNext()) {
    			Coin c = it.next();
    			if (player.getRectangle().intersects(c.getRectangle())) {
    				it.remove();
    				score++;
    			}
    		}
    		
    		//if all the couns are collected, trigger the win text
			if(coins.isEmpty()) {
				setGameWon();
    		}
    }
    
    //Helper to know that once damage is taken, there is a grace period until further damage can be taken.
    private boolean hasTakenDamageRecently() {
    	return System.currentTimeMillis() < takenDamage;
    }
    
    // Applies the enemy contact damage and applies the 3 second cool down
    private void checkDamage() {
    	if (player == null || enemies.isEmpty()) return;
    	if (hasTakenDamageRecently()) return;
    	
    	//if the play collides with an enemy, reduce the player's health and begin the 3 second cool down. If no lives left, game over
    	for (Enemy e : enemies) {
    		if (player.getRectangle().intersects(e.getRectangle())) {
    			player.takeDamage();
    			takenDamage = System.currentTimeMillis() + DAMAGE_COOLDOWN;
    			if (player.getHealth() <= 0 ) {
    				setGameOver();
    			}
    			break;
    		}
    	}
    }
    
   
    //Drawing the HUD where the player health and score will be displayed. This is not playable area
    private void drawHUD(Graphics g) {
        if (gameState == GameState.INITIALISING) return; 
        g.setFont(HUD_FONT);
        g.setColor(Color.WHITE);

        int padding = 12;
        int baseline = HUD_HEIGHT - 12; 

        int hp = (player != null) ? Math.max(0, player.getHealth()) : 0;
        
        //health label is always shown while in gameplay - values always show after it
        final String label = "Health: ";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, padding, baseline);
        int xAfterLabel = padding + fm.stringWidth(label);
        
        //if the player is on their last life, remove the hearts and flash LAST LIFE. Otherwise, show hearts
        if (hp == 1) {
        	boolean show = ((System.currentTimeMillis() / LAST_LIFE_TEXT) % 2) == 0;
        	if (show) {
        		g.drawString("LAST LIFE", xAfterLabel, baseline);
        	}
        }
        else { 
        	String hearts = HEART.repeat(hp);
        	g.drawString(hearts, xAfterLabel, baseline);
        }
        
        //Setting the coin counter on the right side of the screen
        int collected = score;
        String coinsText = "Coins: " + collected + "/" + COIN_COUNT;

        int coinsWidth = g.getFontMetrics().stringWidth(coinsText);
        g.drawString(coinsText, getWidth() - padding - coinsWidth, baseline);
    }
    
    //Stating the game - main game loop. Runs logic by state
    public void update() {
    	switch(gameState) {
    	case INITIALISING:{
    		break;
    	}
    	
    	case PLAYING: {
    		
    		//move player within the game play area. Player can not move in to the HUD space
    		if (player != null) {
    			player.setXPosition(player.getXPosition() + player.getXVelocity(), getWidth());
    			player.setYPositionWithin(player.getYPosition() + player.getYVelocity(), HUD_HEIGHT, getHeight());
    		}
    		
    		//Allow enemies to move within the game play area and bounce off the boundaries
    		for (Enemy e : enemies) {
    			e.setXPosition(e.getXPosition() + e.getXVelocity(), getWidth());
    			e.setYPositionWithin(e.getYPosition() + e.getYVelocity(), HUD_HEIGHT, getHeight());
    			bounceEnemyAtWalls(e);
    		}
    		//using methods set above to ensure coins are removed, score increased, or damage taken etc
    		collectCoins();
    		checkDamage();
    		break;
    	}
    	
    	case GAME_WON: {
    		break;
    	}
    	
    	case GAME_OVER: {
    		break;
    		}
    	}
    }
    //makes sure that the objects are a specific distance away from the player when starting the game
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
    
    //makes sure that the enemies bouce around the game playable area
    private void bounceEnemyAtWalls(Enemy e) {
    	if (e.getXPosition() <= 0 || e.getXPosition() + e.getWidth() >= getWidth()) {
    		e.setXVelocity(-e.getXVelocity());
    	}
    	if (e.getYPosition() <= HUD_HEIGHT || e.getYPosition() + e.getHeight() >= getHeight()) {
    		e.setYVelocity(-e.getYVelocity());
    	}
    }

    	
    //State change to start a new game
    private void startGame() {
    	createObjects();
    	takenDamage = 0L;
    	gameState = GameState.PLAYING;
    }
    
    //Changes state to game won
    private void setGameWon() {
    	gameState = GameState.GAME_WON;
    }
    
    //changes state to show game over screen
    private void setGameOver() {
    	gameState = GameState.GAME_OVER;
    }
    
    //resets the game to ensure that it is starting off fresh - no old info carries over
    private void resetToInitialising() {
    	player = null;
    	enemies.clear();
    	coins.clear();
    	score = 0;
    	gameState = GameState.INITIALISING;
    	requestFocusInWindow();
    	takenDamage = 0L;
    }
    
    //generic rectangle for any sprite - player or enemy
    private void paintSprite(Graphics g, Sprite sprite) {
    	g.setColor(sprite.getColor());
    	g.fillRect(sprite.getXPosition(), sprite.getYPosition(), sprite.getWidth(), sprite.getHeight());
    }
    
    //settings for the player. Handles the flashing to show that damage has been taken, and last life colour change
    private void paintPlayer (Graphics g) {
    	if (player == null) return;
    	
    	long time = System.currentTimeMillis();
    	
    	if (player.getHealth() == 1) {
    		if(hasTakenDamageRecently()) {
    		boolean on = ((time / FLASH_LAST_LIFE) % 2) == 0;
    		if (on) {
	    		g.setColor(Color.RED);
	    		g.fillRect(player.getXPosition(), player.getYPosition(),  player.getWidth(), player.getHeight());
    			}
    		}
    		else {
    			g.setColor(Color.RED);
                g.fillRect(player.getXPosition(), player.getYPosition(),
                           player.getWidth(), player.getHeight());
    		}
    		return;
    	}
    	if (hasTakenDamageRecently()) {
    		boolean flash = ((time/FLASH_COOLDOWN) % 2) == 0;
    		g.setColor(flash ? Color.WHITE:Color.BLACK);
    		g.fillRect(player.getXPosition(), player.getYPosition(),  player.getWidth(), player.getHeight());
    	}
    	else {
    		paintSprite(g, player);
    	}
    }
    
    //setting the colour for the coins
    private void paintCoin(Graphics g, Coin c) {
    	g.setColor(c.getColor()); 
    	g.fillOval(c.getXPosition(), c.getYPosition(), c.getWidth(), c.getHeight());
    }   
    
    //Painting all of the objects that are to be displayed
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
    	
    	//draw the screen by state
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
        			paintPlayer(g);
        		}
        		break;
        	}
        	case GAME_WON: {
        		drawCenteredText(g, "You Win!\n\nPress SPACE to continue...");
        		break;
        	}
        	case GAME_OVER: {
        		drawCenteredText(g, "GAME OVER!\n\nPress SPACE to retry...");
        		break;
        	}
        }
    }
    
    private static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 22);
    
    
    //Draw multi-line centered text - for game won and game over text
    private void drawCenteredText(Graphics g, String text) {
        g.setFont(MESSAGE_FONT);
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        
        String[] lines = text.split("\n");
        int totalHeight = lines.length * fm.getHeight();
        int y = (getHeight() - totalHeight) / 2 + fm.getAscent();
        
        for (String line : lines) {
        	int x = (getWidth() - fm.stringWidth(line))/2;
        	g.drawString(line, x, y);
        	y += fm.getHeight();
        }

    }
    
    //setting the keys to be able to move the player
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
	
	//Sets the keys and directions for the player to move
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		//makes it so the space bar will start the game
		if(gameState == GameState.INITIALISING && code == KeyEvent.VK_SPACE) {
			startGame();
			return;
		}
		
		if ((gameState == GameState.GAME_WON || gameState == GameState.GAME_OVER) && code == KeyEvent.VK_SPACE) {
			resetToInitialising();
			return;
		}
		
		//movement keys
		if(gameState == GameState.PLAYING && player != null) {
			if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) left  = true;
			if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) right = true;
			if (code == KeyEvent.VK_UP    || code == KeyEvent.VK_W) up    = true;
			if (code == KeyEvent.VK_DOWN  || code == KeyEvent.VK_S) down  = true;
			movePlayer();

		}
		
	}
	
	//If keys are released, player will stop moving
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
