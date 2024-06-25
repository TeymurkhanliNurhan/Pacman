import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.event.KeyEvent;
public class Pacman {
    private int x;
    private int y;
    private int dx = 0;
    private int dy = 0;
    private int newDx;
    private int newDy;
    private int width; // width alanını ekleyin
    private int height; // height alanını ekleyin
    private int screenWidth; // Ekran genişliği
    private int screenHeight; // Ekran yüksekliği
    private final int TILE_SIZE = 28;
    public int[][] PacmanLocs = new int[][]{{x, y}, {x + TILE_SIZE - 1, y}, {x, y + TILE_SIZE - 1}, {x + TILE_SIZE - 1, y + TILE_SIZE - 1}};
    public int[][] newPacmanLocs;
    public int[][] newPacmanLocs2;
    private ImageIcon image;
    private Board board;
    private int animationStep = 0;
    private int lostAnimationSep = 0;
    private boolean moving = false;
    private Thread animationThread;
    private double speedMultiplier = 1.0; // Normal speed
    private boolean frozen = false;
    private CustomPoint initialCoord;

    public Pacman() {
        this.initialCoord = new CustomPoint(TILE_SIZE, TILE_SIZE);
        this.width = TILE_SIZE;
        this.height = TILE_SIZE;
        initPacman();
        startAnimation();
    }

    private void initPacman() {
        image = new ImageIcon("nurhan/resources/images/pac_28px/pac0Right.png");
        x = TILE_SIZE;
        y = TILE_SIZE;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void move() {
        if (frozen) return;
        int newX = x + (int) (newDx * speedMultiplier);
        int newY = y + (int) (newDy * speedMultiplier);
        int newX2 = x + (int) (dx * speedMultiplier);
        int newY2 = y + (int) (dy * speedMultiplier);
        newPacmanLocs = new int[][]{{newX, newY}, {newX + width - 1, newY}, {newX, newY + height - 1}, {newX + width - 1, newY + height - 1}};
        newPacmanLocs2 = new int[][]{{newX2, newY2}, {newX2 + width - 1, newY2}, {newX2, newY2 + height - 1}, {newX2 + width - 1, newY2 + height - 1}};
        if (!board.isWall(newPacmanLocs)) {
            PacmanLocs = newPacmanLocs;
            x = newX;
            y = newY;
            dx = newDx;
            dy = newDy;
            moving = true;
            board.updatePacmanImage();
            board.checkSpecFoodCollision(); // Pacman hareket ederken special food çarpışmasını kontrol et
        } else {
            if (isZero(dy) != isZero(newDy)) {
                if (board.isWall(newPacmanLocs2)) {
                    newDx = 0;
                    newDy = 0;
                    move();
                }
                y = y + dy;
                x = x + dx;
            }
        }
        pasWall();
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
        animationStep = (animationStep + 1) % 8;
        int step = animationStep < 4 ? animationStep : 7 - animationStep;
        String newImg = String.format("nurhan/resources/images/pac_28px/pac%d%s.png", step, direction);
        image = new ImageIcon(new ImageIcon(newImg).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        board.updatePacmanImage();
    }

    private void startAnimation() {
        animationThread = new Thread(() -> {
            while (true) {
                try {
                    if (moving) {
                        updateImage();
                    }
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        animationThread.start();
    }

    public boolean isZero(int x) {
        return x == 0;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
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
    public void setImage(ImageIcon image){
        image=image;
    }
    public void pasWall() {
        // Ekran boyutları henüz ayarlanmadıysa harita boyutlarını kullan
        int maxX = screenWidth > 0 ? screenWidth - width : (board.getMap()[0].length - 1) * TILE_SIZE;
        int maxY = screenHeight > 0 ? screenHeight - height : (board.getMap().length - 1) * TILE_SIZE;
        if (x > maxX) {x = 0;}
        else if (x < 0) x = maxX;
        if (y > maxY) y = 0;
        else if (y < 0) y = maxY;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            //this if statement (e.g  if (dx!=-7)) is for this: if someone clicks a button multiple times, pacman will not take it as a new input, so move in normal speed
            if (dx!=-7) {
                newDx = -7;
                newDy = 0;
                move();
            }

        }
        if (key == KeyEvent.VK_RIGHT) {
            if(dx!=7) {
                newDx = 7;
                newDy = 0;
                move();
            }

        }
        if (key == KeyEvent.VK_UP) {
            if (dy != -7) {
                newDy = -7;
                newDx = 0;
                move();
            }
        }
        if (key == KeyEvent.VK_DOWN) {
            if (dy!=7) {
                newDy = 7;
                newDx = 0;
                move();
            }
        }
    }
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        // Pacman'in resmini yeniden boyutlandır ve güncelle
        updateImage();
    }
    public void setPosition(int a, int b) {
        x = a;
        y = b;
    }
    public CustomPoint getInitialCoord() {
        return initialCoord;
    }
    public void setScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}