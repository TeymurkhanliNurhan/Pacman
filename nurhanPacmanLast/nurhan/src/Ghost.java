import javax.swing.*;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ghost {
    // Constants
    private final int TILE_SIZE = 28;

    // Fields
    private final Random random = new Random();
    private double x;
    private double y;
    private double dx;
    private double dy;
    private int width;
    private int height;
    private Pacman pacman;
    public int[][] GhostLocs;
    public int[][] newGhostLocs;
    public List<JLabel> specFood;
    public List<JLabel> specFoodToRemove = new ArrayList<>();
    private ImageIcon image;
    private Board board;
    private boolean frozen = false;
    private int animationStep = 0;
    private boolean moving = false;
    private Thread animationThread;
    private int color;
    private CustomPoint initialCoord;
    private double scaleX;
    private double scaleY;

    // Constructor
    public Ghost(int a, int b, int color) {
        this.color = color;
        this.initialCoord = new CustomPoint(a, b);
        this.width = TILE_SIZE;
        this.height = TILE_SIZE;
        this.scaleY = 1;
        this.scaleX = 1;
        initGhost(a, b, color);
        startAnimation();
        startLeaveImageThread();
    }

    // Initialization
    private void initGhost(int a, int b, int color) {
        if (color == 1) image = new ImageIcon("nurhan/resources/images/ghost/blue/blueRight1.png");
        if (color == 2) image = new ImageIcon("nurhan/resources/images/ghost/pink/pinkRight1.png");
        if (color == 3) image = new ImageIcon("nurhan/resources/images/ghost/red/redRight1.png");
        x = a;
        y = b;
        specFood = new ArrayList<>();
    }

    // Setters
    public void setPacman(Pacman pacman) {
        this.pacman = pacman;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        updateImage();
    }

    public void setPosition(int a, int b) {
        x = a;
        y = b;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
        if (frozen) {
            Thread freezeThread = new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.frozen = false;
            });
            freezeThread.start();
        }
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    // Getters
    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public ImageIcon getImage() {
        return image;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public CustomPoint getInitialCoord() {
        return initialCoord;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Movement Methods
    public void move() {
        if (frozen) return;

        double newX = x + dx;
        double newY = y + dy;
        newGhostLocs = new int[][]{
                {(int) newX, (int) newY},
                {(int) (newX + width - 1), (int) newY},
                {(int) newX, (int) (newY + height - 1)},
                {(int) (newX + width - 1), (int) (newY + height - 1)}
        };

        if (!board.isWall(newGhostLocs)) {
            GhostLocs = newGhostLocs;
            x = newX;
            y = newY;
            moving = true;
            board.updateGhostImage(this);
        } else {
            randomMove();
        }
    }

    public void randomMove() {
        if (frozen) return;

        int tries = 0;
        boolean moved = false;

        while (tries < 4 && !moved) {
            int direction = random.nextInt(4);
            switch (direction) {
                case 0 -> {
                    dx = -4;
                    dy = 0;
                }
                case 1 -> {
                    dx = 4;
                    dy = 0;
                }
                case 2 -> {
                    dx = 0;
                    dy = -4;
                }
                case 3 -> {
                    dx = 0;
                    dy = 4;
                }
            }
            double newX = x + dx;
            double newY = y + dy;
            newGhostLocs = new int[][]{
                    {(int) newX, (int) newY},
                    {(int) (newX + width - 1), (int) newY},
                    {(int) newX, (int) (newY + height - 1)},
                    {(int) (newX + width - 1), (int) (newY + height - 1)}
            };

            if (!board.isWall(newGhostLocs)) {
                x = newX;
                y = newY;
                GhostLocs = newGhostLocs;
                moved = true;
            } else {
                tries++;
            }
        }
        if (moved) {
            moving = true;
            board.updateGhostImage(this);
        } else {
            dx = 0;
            dy = 0;
        }
    }

    public void correctMove(double gX, double gY, int pX, int pY) {
        if (frozen) return;

        int difX = Math.abs(pX - (int) gX);
        int difY = Math.abs(pY - (int) gY);

        if (difY >= difX) {
            dx = 0;
            dy = (pY - gY >= 0 ? 4 : -4);
            newGhostLocs = new int[][]{
                    {(int) gX, (int) (gY + dy - 1)},
                    {(int) (gX + width - 1), (int) (gY + dy - 1)},
                    {(int) gX, (int) (gY + height - 1 + dy)},
                    {(int) (gX + width - 1), (int) (gY + height - 1 + dy)}
            };
            if (board.isWall(newGhostLocs)) {
                dx = (pX - gX >= 0 ? 4 : -4);
                dy = 0;
                newGhostLocs = new int[][]{
                        {(int) (gX + dx - 1), (int) gY},
                        {(int) (gX + width - 1 + dx), (int) gY},
                        {(int) (gX + dx - 1), (int) (gY + height - 1)},
                        {(int) (gX + width - 1 + dx), (int) (gY + height - 1)}
                };
                if (board.isWall(newGhostLocs)) {
                    randomMove();
                } else {
                    move();
                }
            } else {
                move();
            }
        } else {
            dx = (pX - gX >= 0 ? 4 : -4);
            dy = 0;
            newGhostLocs = new int[][]{
                    {(int) (gX + dx - 1), (int) gY},
                    {(int) (gX + width - 1 + dx), (int) gY},
                    {(int) (gX + dx - 1), (int) (gY + height - 1)},
                    {(int) (gX + width - 1 + dx), (int) (gY + height - 1)}
            };
            if (board.isWall(newGhostLocs)) {
                dx = 0;
                dy = (pY - gY >= 0 ? 4 : -4);
                newGhostLocs = new int[][]{
                        {(int) gX, (int) (gY + dy - 1)},
                        {(int) (gX + width - 1), (int) (gY + dy - 1)},
                        {(int) gX, (int) (gY + height - 1 + dy)},
                        {(int) (gX + width - 1), (int) (gY + height - 1 + dy)}
                };
                if (board.isWall(newGhostLocs)) {
                    randomMove();
                } else {
                    move();
                }
            } else {
                move();
            }
        }
    }

    // Animation Methods
    private void startAnimation() {
        animationThread = new Thread(() -> {
            while (true) {
                try {
                    if (moving) {
                        updateImage();
                    }
                    Thread.sleep(250); // Adjust the speed as needed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        animationThread.start();
    }

    private void updateImage() {
        String direction;
        if (dx > 0) {
            direction = "Right";
        } else if (dx < 0) {
            direction = "Left";
        } else if (dy < 0) {
            direction = "Up";
        } else {
            direction = "Down";
        }

        animationStep = (animationStep + 1) % 2; // Two-step animation
        String newImg = String.format("nurhan/resources/images/ghost/%s/%s%s%d.png", getColorName(color), getColorName(color), direction, animationStep + 1);
        image = new ImageIcon(new ImageIcon(newImg).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        board.updateGhostImage(this);
    }
    private String getColorName(int color) {
        return switch (color) {
            case 1 -> "blue";
            case 2 -> "pink";
            case 3 -> "red";
            default -> "cyan";
        };
    }

    // Special Food Methods
    public String getSpecFoodImgLink(int i) {
        return ((ImageIcon) specFood.get(i).getIcon()).getDescription();
    }
    private void startLeaveImageThread() {
        Thread leaveImageThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    leaveRandomImage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        leaveImageThread.start();
    }
    private void leaveRandomImage() {
        int chance = random.nextInt(4);
        if (chance == 1) {
            int imageIndex = random.nextInt(5) + 1;
            String imagePath = String.format("nurhan/resources/images/food/%d.png", imageIndex);
            JLabel imageLabel = new JLabel(new ImageIcon(imagePath));
            imageLabel.setBounds((int) x, (int) y, (int) (width * scaleX), (int) (height * scaleY));

            specFood.add(imageLabel);
            board.add(imageLabel, JLayeredPane.POPUP_LAYER);
            board.repaint();
        }
    }
    public void resizeSpecialFood(double xScale, double yScale) {
        for (JLabel specFoodLabel : specFood) {
            int newX = (int) (specFoodLabel.getX() * xScale);
            int newY = (int) (specFoodLabel.getY() * yScale);
            int newWidth = (int) (TILE_SIZE * xScale);
            int newHeight = (int) (TILE_SIZE * yScale);

            Image image = ((ImageIcon) specFoodLabel.getIcon()).getImage();
            specFoodLabel.setBounds(newX, newY, newWidth, newHeight);
            specFoodLabel.setIcon(new ImageIcon(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)));
            scaleX = xScale;
            scaleY = yScale;
        }
    }
}
