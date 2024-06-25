import javax.swing.*;
public class Food {
    private final int TILE_SIZE = 28;
    private int x;
    private int y;
    public int[][] FoodLocs;
    private ImageIcon image;
    public Food(int x, int y) {
        this.x = x+4;
        this.y = y+4;
        initFood();
        FoodLocs = new int[][]{{x, y}, {x + TILE_SIZE, y}, {x, y + TILE_SIZE}, {x + TILE_SIZE, y + TILE_SIZE}};
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public ImageIcon getImage() {
        return image;
    }
    private void initFood() {
        image = new ImageIcon("nurhan/resources/images/map_segments_28px/31.png");
    }
}
