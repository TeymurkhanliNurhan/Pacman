import javax.swing.ImageIcon;

public class Obstacle {
    // Fields
    private int x;
    private int y;
    private ImageIcon image;
    private Board board;
    public int[][] ObstacleLocs;
    private final int TILE_SIZE = 28;

    // Constructor
    public Obstacle(int tile, int x, int y) {
        this.x = x;
        this.y = y;
        initObstacle(tile);
        ObstacleLocs = new int[][]{{x, y}, {x + TILE_SIZE, y}, {x, y + TILE_SIZE}, {x + TILE_SIZE, y + TILE_SIZE}};
    }

    // Initialize obstacle image
    private void initObstacle(int tile) {
        String path = String.format("nurhan/resources/images/map_segments_28px/%d.png", tile);
        image = new ImageIcon(path);
    }

    // Getters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public ImageIcon getImage() {
        return image;
    }
}
