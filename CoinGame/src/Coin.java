import java.awt.Color;

public class Coin extends Sprite{
	
	public static final int COIN_SIZE = 20;
	private static final Color COIN_COLOR = Color.YELLOW;
	
	public Coin(int startX, int startY) {
		setWidth(COIN_SIZE);
		setHeight(COIN_SIZE);
		setColor(COIN_COLOR);
		setInitialPosition(startX, startY);
		resetToInitialPosition();
	}

}
