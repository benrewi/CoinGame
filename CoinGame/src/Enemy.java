import java.awt.Color;

public class Enemy extends Sprite{
	private static final int ENEMY_SIZE = 25;
	private static final Color ENEMY_COLOR = Color.BLUE;
	
	public Enemy(int startX, int startY, int vx, int vy) {
		setWidth(ENEMY_SIZE);
		setHeight(ENEMY_SIZE);
		setColor(ENEMY_COLOR);
		setInitialPosition(startX, startY);
		resetToInitialPosition();
		setXVelocity(vx);
		setYVelocity(vy);
	}
	
	

}
