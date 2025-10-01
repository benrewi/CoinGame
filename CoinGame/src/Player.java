import java.awt.Color;

public class Player extends Sprite{
	private static final int PLAYER_WIDTH = 25;
	private static final int PLAYER_HEIGHT = 25;
	private static final Color PLAYER_COLOR = Color.WHITE;
	private int health = 5;
	
	public Player(int panelWidth, int panelHeight) {
		setWidth(PLAYER_WIDTH);
		setHeight(PLAYER_HEIGHT);
		setColor(PLAYER_COLOR);
		setInitialPosition(panelWidth / 2 - (getWidth()/2), panelHeight / 2 - (getHeight()/2));
		resetToInitialPosition();
	}
	
	public void takeDamage() {
		health--;
	}
	
	public int getHealth() {
		return health;
	}

}
