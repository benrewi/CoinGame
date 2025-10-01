import java.awt.Color;
import java.awt.Rectangle;

public class Sprite {
	
	private int xPosition, yPosition;
    private int xVelocity, yVelocity;
    private int width, height;
    private int initialXPosition, initialYPosition;
    
    public void setInitialPosition(int initialX, int initialY) {
    	initialXPosition = initialX;
    	initialYPosition = initialY;
    }
    
    public void resetToInitialPosition() {
    	setXPosition(initialXPosition);
    	setYPosition(initialYPosition);
    }
    
    public void setXPosition(int newX, int panelWidth) {
		xPosition = newX;
		if(xPosition < 0){
			xPosition=0;
		}
		else if (xPosition + width > panelWidth) {
			xPosition = panelWidth - width;
		};
	}
    
    public int getXPosition() {
        return xPosition;
    }
 
    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }
 
    public int getYPosition() {
        return yPosition;
    }
 
    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }
    
    public void setYPosition(int newY, int panelHeight) {
		yPosition = newY;
		if(yPosition < 0){
			yPosition=0;
		}
		else if (yPosition + height > panelHeight) {
			yPosition = panelHeight - height;
		};
	}
    public void setYPositionWithin(int newY, int minY, int maxY) {
        yPosition = newY;
        if (yPosition < minY) {
            yPosition = minY;
        } else if (yPosition + height > maxY) {
            yPosition = maxY - height;
        }
    }
    
    public int getXVelocity() {
        return xVelocity;
    }
    
    public void setXVelocity(int xVelocity) {
        this.xVelocity = xVelocity;
    }

    public int getYVelocity() {
        return yVelocity;
    }
    public void setYVelocity(int yVelocity) {
        this.yVelocity = yVelocity;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    private Color color;
    
    public Color getColor() {
        return color;
    }
	
    public void setColor(Color color) {
        this.color = color;
    }
    
    public Rectangle getRectangle() {
    	return new Rectangle(getXPosition(), getYPosition(), getWidth(), getHeight());
    	}

}
