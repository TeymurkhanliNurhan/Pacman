import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class Board extends JLayeredPane {
    //pacman
    private Pacman pacman;  private JLabel pacmanLabel;
    //ghosts
    private Ghost ghost1; private Ghost ghost2;private Ghost ghost3;
    private List<Ghost> ghosts;
    private int[][] ghostInitialPositions;
    private JLabel ghostLabel1;
    private JLabel ghostLabel2;
    private JLabel ghostLabel3;
    private List<JLabel> ghostLabels;

    private JLabel scoreLabel;
    private JLabel livesLabel; // Pacman's lives label
    private final int TILE_SIZE = 28;
    private int initScreenWidth;
    private int initScreenHeight;
    private int scoreIncrease = 10;
    private boolean deactiveGhosts=false;
    private List<CustomPoint> initialObstacleCoords = new ArrayList<>();
    private List<CustomPoint> initialFoodCoords = new ArrayList<>();
    private boolean isIncreasing = false;
    private int[][] map;
    private boolean running=true;
    public final List<int[]> walls;
    public List<JLabel> foods;
    public List<Food> foodItems;
    public List<JLabel> obstacleLabels;
    public List<Obstacle> obstacles;
    private JLabel timerLabel;
    private int seconds;
    private Thread timerThread;
    private Thread gameThread;
    private int score;
    private int foodCount = 0;
    private int lives = 3; // Initial lives
    private final ImageIcon[] images;
    private int speedMultiplier = 1;
    private Thread gameThreadPacman;
    private boolean isGameOver = false; // Flag to track if the game is over
    public Board(String link) {
        walls = new ArrayList<>();
        foods = new ArrayList<>();
        foodItems = new ArrayList<>();
        obstacles = new ArrayList<>();
        obstacleLabels = new ArrayList<>();
        ghosts = new ArrayList<>();
        ghostLabels = new ArrayList<>();
        images = new ImageIcon[40]; // Assuming maximum index in map is 30
        score = 0;
        loadImages();
        loadMap(link);
        initBoard();
    }
    private void initBoard() {
        setFocusable(true);
        setLayout(null);
        setSize(map[0].length * TILE_SIZE, map.length * TILE_SIZE);
        pacman = new Pacman();
        pacman.setBoard(this);
        pacmanLabel = new JLabel(pacman.getImage());
        pacmanLabel.setBounds(pacman.getX(), pacman.getY(), pacman.getImage().getIconWidth(), pacman.getImage().getIconHeight());
        add(pacmanLabel, JLayeredPane.POPUP_LAYER);

        int x1 = (map[0].length - 2) * TILE_SIZE;
        int y1 = TILE_SIZE;
        int x2 = TILE_SIZE;
        int y2 = (map.length - 2) * TILE_SIZE;
        int x3 = (map[0].length - 2) * TILE_SIZE;
        int y3 = (map.length - 2) * TILE_SIZE;

        // Initialize the positions array
        ghostInitialPositions = new int[][]{
                {x1, y1},
                {x2, y2},
                {x3, y3}
        };

        //ghost1
        ghost1 = new Ghost(x1, y1, 1);
        ghost1.setBoard(this);
        ghost1.setPacman(pacman);
        ghostLabel1 = new JLabel(ghost1.getImage());
        ghostLabel1.setBounds(ghost1.getX(), ghost1.getY(), ghost1.getImage().getIconWidth(), ghost1.getImage().getIconHeight());
        add(ghostLabel1, JLayeredPane.POPUP_LAYER);
        ghosts.add(ghost1);
        ghostLabels.add(ghostLabel1);

        if (map[0].length!=11) {
            //ghost2
            ghost2 = new Ghost(x2, y2, 2);
            ghost2.setBoard(this);
            ghost2.setPacman(pacman);
            ghostLabel2 = new JLabel(ghost2.getImage());
            ghostLabel2.setBounds(ghost2.getX(), ghost2.getY(), ghost2.getImage().getIconWidth(), ghost2.getImage().getIconHeight());
            add(ghostLabel2, JLayeredPane.POPUP_LAYER);
            ghosts.add(ghost2);
            ghostLabels.add(ghostLabel2);

            if (map[0].length!=15) {
                //ghost3
                ghost3 = new Ghost(x3, y3, 3);
                ghost3.setBoard(this);
                ghost3.setPacman(pacman);
                ghostLabel3 = new JLabel(ghost3.getImage());
                ghostLabel3.setBounds(ghost3.getX(), ghost3.getY(), ghost3.getImage().getIconWidth(), ghost3.getImage().getIconHeight());
                add(ghostLabel3, JLayeredPane.POPUP_LAYER);
                ghosts.add(ghost3);
                ghostLabels.add(ghostLabel3);
            }
        }

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setBounds(0, 0, 130, 28);
        scoreLabel.setForeground(Color.WHITE); // Set text color to white
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        add(scoreLabel, JLayeredPane.PALETTE_LAYER);

        // Create lives label
        livesLabel = new JLabel("Lives: " + lives);
        livesLabel.setBounds(130, 0, 80, 28);
        livesLabel.setForeground(Color.WHITE); // Set text color to white
        livesLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        add(livesLabel, JLayeredPane.PALETTE_LAYER);

        // Create timer label
        timerLabel = new JLabel("Time: 0");
        timerLabel.setBounds(210, 0, 100, 28);
        timerLabel.setForeground(Color.WHITE); // Set text color to white
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size and style
        add(timerLabel, JLayeredPane.PALETTE_LAYER);

        // Initialize and start the timer thread
        timerThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(1000);
                    seconds++;
                    SwingUtilities.invokeLater(() -> timerLabel.setText("Time: " + seconds));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  // Reset the interrupt status
                    break;  // Exit the loop if interrupted
                }
            }
        });
        timerThread.start();

        // Draw the map
        drawMap();

        // Add all food labels to the board after drawing the map
        for (JLabel foodLabel : foods) {
            add(foodLabel, JLayeredPane.MODAL_LAYER);
        }
        addKeyListener(new TAdapter());
        // Game loop thread
        gameThread = new Thread(() -> {
            while (running) {
                try {
                    if (!isGameOver) {
                        for (int i = 0; i < ghosts.size(); i++) {
                            Ghost ghost = ghosts.get(i);
                            ghost.correctMove(ghost.getX(), ghost.getY(), pacman.getX(), pacman.getY());
                            ghostLabels.get(i).setLocation(ghost.getX(), ghost.getY());
                        }
                        checkGhostCollision(); // Check collision with ghosts
                        SwingUtilities.invokeLater(this::repaint);
                    }
                    Thread.sleep(70); // 0.07 second wait
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        gameThreadPacman = new Thread(() -> {
            while (running) {
                try {
                    if (!isGameOver) {
                        pacman.move();
                        pacmanLabel.setLocation(pacman.getX(), pacman.getY());
                        checkFoodCollision();
                        checkSpecFoodCollision();
                        SwingUtilities.invokeLater(this::repaint);
                    }
                    Thread.sleep(70 / speedMultiplier); // 0.07 second wait
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        gameThread.start();
        gameThreadPacman.start();

        initScreenWidth = TILE_SIZE * map[0].length;
        initScreenHeight = TILE_SIZE * map.length;
        // JFrame boyut değişikliklerini dinle
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePositionAndSize();
            }
        });
    }
    public void updateSpecialFoods(double xScale, double yScale) {
        for (Ghost ghost : ghosts) {
            ghost.resizeSpecialFood(xScale, yScale);
        }
    }
    public void updatePositionAndSize() {
        int newScreenWidth = getWidth();
        int newScreenHeight = getHeight();
        double xScale = (double) newScreenWidth / initScreenWidth;
        double yScale = (double) newScreenHeight / initScreenHeight;
        // Resizing obstacles
        for (int i = 0; i < obstacleLabels.size(); i++) {
            JLabel obstacleLabel = obstacleLabels.get(i);
            CustomPoint initialCoord = initialObstacleCoords.get(i);

            int newXCoor = (int) (initialCoord.x * xScale);
            int newYCoor = (int) (initialCoord.y * yScale);
            int newWidth = (int) (TILE_SIZE * xScale);
            int newHeight = (int) (TILE_SIZE * yScale);

            Image image = obstacles.get(i).getImage().getImage();

            obstacleLabel.setBounds(newXCoor, newYCoor, newWidth, newHeight);
            obstacleLabel.setIcon(new ImageIcon(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)));
        }
        // Resizing foods (regular foods)
        for (int i = 0; i < foods.size(); i++) {
            JLabel foodLabel = foods.get(i);
            CustomPoint initialCoord = initialFoodCoords.get(i);

            int newXCoor = (int) (initialCoord.x * xScale);
            int newYCoor = (int) (initialCoord.y * yScale);
            int newWidth = (int) (TILE_SIZE * xScale);
            int newHeight = (int) (TILE_SIZE * yScale);

            Image image = foodItems.get(i).getImage().getImage();

            foodLabel.setBounds(newXCoor, newYCoor, newWidth, newHeight);
            foodLabel.setIcon(new ImageIcon(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)));
        }

        // Resizing unpassable walls
        updateWalls(xScale, yScale);

        // Update Pacman
        double pacmanXScale = (double) newScreenWidth / initScreenWidth;
        double pacmanYScale = (double) newScreenHeight / initScreenHeight;

        int newPacmanX = (int) (pacman.getX() * pacmanXScale);
        int newPacmanY = (int) (pacman.getY() * pacmanYScale);
        int newPacmanWidth = (int) (TILE_SIZE * xScale);
        int newPacmanHeight = (int) (TILE_SIZE * yScale);

        pacman.setSize(newPacmanWidth, newPacmanHeight);  // Pacman'in boyutlarını güncelle
        pacman.setPosition(newPacmanX, newPacmanY); // Pacman'in konumunu güncelle
        pacmanLabel.setBounds(newPacmanX, newPacmanY, newPacmanWidth, newPacmanHeight);
        pacmanLabel.setIcon(new ImageIcon(pacman.getImage().getImage().getScaledInstance(newPacmanWidth, newPacmanHeight, Image.SCALE_SMOOTH)));

        pacman.setScreenSize(newScreenWidth, newScreenHeight);  // Ekran boyutlarını Pacman'e bildir

        // Update Ghosts
        for (int i = 0; i < ghosts.size(); i++) {
            Ghost ghost = ghosts.get(i);

            double ghostXScale = (double) newScreenWidth / initScreenWidth;
            double ghostYScale = (double) newScreenHeight / initScreenHeight;

            int newGhostX = (int) (ghost.getX() * ghostXScale);
            int newGhostY = (int) (ghost.getY() * ghostYScale);
            int newGhostWidth = (int) (TILE_SIZE * xScale);
            int newGhostHeight = (int) (TILE_SIZE * yScale);

            ghost.setSize(newGhostWidth, newGhostHeight);  // Ghost'un boyutlarını güncelle
            ghost.setPosition(newGhostX, newGhostY); // Ghost'un konumunu güncelle
            JLabel ghostLabel = ghostLabels.get(i);
            ghostLabel.setBounds(newGhostX, newGhostY, newGhostWidth, newGhostHeight);
            ghostLabel.setIcon(new ImageIcon(ghost.getImage().getImage().getScaledInstance(newGhostWidth, newGhostHeight, Image.SCALE_SMOOTH)));
        }
        // Update special foods
        updateSpecialFoods(xScale, yScale);

    }
  private void updateWalls(double xScale, double yScale) {
        for (int[] wall : walls) {
            wall[0] = (int) (wall[0] * xScale);
            wall[1] = (int) (wall[1] * yScale);
        }
    }
    public void updatePacmanImage() {
        pacmanLabel.setIcon(pacman.getImage());
    }
    private void loadImages() {
        for (int i = 0; i <= 30; i++) {
            String path = String.format("resources/images/map_segments_28px/%d.png", i);
            images[i] = new ImageIcon(path);
        }
    }
    private void loadMap(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            List<String[]> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(" ");
                lines.add(tokens);
            }
            int rows = lines.size();
            int cols = lines.get(0).length;
            map = new int[rows][cols];
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    map[row][col] = Integer.parseInt(lines.get(row)[col]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawMap() {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                int tile = map[row][col];

                Obstacle obstacle1 = new Obstacle(9, col * TILE_SIZE, row * TILE_SIZE);
                JLabel label1 = new JLabel(obstacle1.getImage());
                label1.setBounds(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                obstacleLabels.add(label1);
                obstacles.add(obstacle1);
                CustomPoint initialCoord = new CustomPoint(col * TILE_SIZE, row * TILE_SIZE);
                initialObstacleCoords.add(initialCoord);
                add(label1, JLayeredPane.DEFAULT_LAYER);

                Obstacle obstacle = new Obstacle(tile, col * TILE_SIZE, row * TILE_SIZE);
                JLabel label = new JLabel(obstacle.getImage());
                label.setBounds(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                obstacleLabels.add(label);
                obstacles.add(obstacle);
                initialObstacleCoords.add(new CustomPoint(col * TILE_SIZE, row * TILE_SIZE));
                add(label, JLayeredPane.PALETTE_LAYER);

                if (tile == 9) {
                    Food food = new Food(col * TILE_SIZE, row * TILE_SIZE);
                    JLabel foodLabel = new JLabel(food.getImage());
                    foodLabel.setBounds(food.getX(), food.getY(), food.getImage().getIconWidth(), food.getImage().getIconHeight());
                    foods.add(foodLabel);
                    foodItems.add(food);
                    initialFoodCoords.add(new CustomPoint(col * TILE_SIZE, row * TILE_SIZE));
                    foodCount++;
                } else {
                    for (int i = col * TILE_SIZE; i < col * TILE_SIZE + TILE_SIZE; i++) {
                        for (int j = row * TILE_SIZE; j < row * TILE_SIZE + TILE_SIZE; j++) {
                            int[] arr = new int[]{i, j};
                            walls.add(arr);
                        }
                    }
                }
            }
        }
    }

    private void checkFoodCollision() {
        List<JLabel> foodsToRemove = new ArrayList<>();
        List<Food> foodItemsToRemove = new ArrayList<>();
        for (int i = 0; i < foods.size(); i++) {
            JLabel foodLabel = foods.get(i);
            Food food = foodItems.get(i);
            Rectangle foodBounds = new Rectangle(foodLabel.getX(), foodLabel.getY(), foodLabel.getWidth(), foodLabel.getHeight());
            if (pacman.getBounds().intersects(foodBounds)) {
                foodsToRemove.add(foodLabel);
                foodItemsToRemove.add(food);
                score += scoreIncrease;
                scoreLabel.setText("Score: " + score);
                foodCount--;
                if (foodCount == 0) {
                    restartGame();
                }
            }
        }
        foods.removeAll(foodsToRemove);
        foodItems.removeAll(foodItemsToRemove);
        for (JLabel foodLabel : foodsToRemove) {
            remove(foodLabel);
        }
    }
    public void checkSpecFoodCollision() {
        for (Ghost ghost : ghosts) {
            for (int i = 0; i < ghost.specFood.size(); i++) {
                JLabel specFoodLabel = ghost.specFood.get(i);
                Rectangle specFoodBounds = new Rectangle(specFoodLabel.getX(), specFoodLabel.getY(), specFoodLabel.getWidth(), specFoodLabel.getHeight());
                if (pacman.getBounds().intersects(specFoodBounds)) {
                    ghost.specFoodToRemove.add(specFoodLabel);
                    String specFoodImgLink = ghost.getSpecFoodImgLink(i);
                    switch (specFoodImgLink) {
                        case "resources/images/food/1.png":
                            for (Ghost g : ghosts) {
                                g.setFrozen(true);
                            }
                            break;
                        case "resources/images/food/2.png":
                            setSpeedMultiplier(2, 5000);
                            break;
                        case "resources/images/food/3.png":
                            ghost.setFrozen(true);
                            break;
                        case "resources/images/food/4.png":
                            increaseScoreIncrement();
                            break;
                        case "resources/images/food/5.png":
                            setGhostsDeactive();
                            break;
                    }
                }
            }
        }
        for (Ghost ghost : ghosts) {
            ghost.specFood.removeAll(ghost.specFoodToRemove);
            for (JLabel specFoodLabel : ghost.specFoodToRemove) {
                remove(specFoodLabel);
            }
            ghost.specFoodToRemove.clear();
        }
    }
    private void checkGhostCollision() {
        for (int i = 0; i < ghosts.size(); i++) {
            JLabel ghostLabel = ghostLabels.get(i);
            Rectangle ghostBounds = new Rectangle(ghostLabel.getX(), ghostLabel.getY(), ghostLabel.getWidth(), ghostLabel.getHeight());
           if (!deactiveGhosts) {
               if (pacman.getBounds().intersects(ghostBounds)) {
                   lives--;
                   livesLabel.setText("Lives: " + lives);
                   if (lives <= 0) {
                       //playLostAnimation("over");
                       gameOver();
                       //pacman.playLostAnimation("over");
                   } else {
                       // pacman.playLostAnimation("reset");
                     // playLostAnimation("reset");
                       resetPositions();
                   }
                   break; // Exit the loop after handling collision with one ghost
               }
           }
        }
    }
    public void freezeGhosts(int duration) {
        for (Ghost ghost : ghosts) {
            ghost.setFrozen(true);
        }
        new Thread(() -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Ghost ghost : ghosts) {
                ghost.setFrozen(false);
            }
        }).start();
    }
    public void resetPositions() {
        double xScale = (double) getWidth() / initScreenWidth;
        double yScale = (double) getHeight() / initScreenHeight;

        // Reset Pacman's position
        int pacmanInitialX = (int) (pacman.getInitialCoord().x * xScale);
        int pacmanInitialY = (int) (pacman.getInitialCoord().y * yScale);
        pacman.setPosition(pacmanInitialX, pacmanInitialY);
        pacmanLabel.setBounds(pacman.getX(), pacman.getY(), pacman.getWidth(), pacman.getHeight());

        // Reset all ghosts' positions
        for (int i = 0; i < ghosts.size(); i++) {
            int ghostInitialX = (int) (ghostInitialPositions[i][0] * xScale);
            int ghostInitialY = (int) (ghostInitialPositions[i][1] * yScale);
            ghosts.get(i).setPosition(ghostInitialX, ghostInitialY);
            ghostLabels.get(i).setBounds(ghosts.get(i).getX(), ghosts.get(i).getY(), ghosts.get(i).getWidth(), ghosts.get(i).getHeight());
        }
    }
    public void setGhostsDeactive(){
        new Thread(() -> {
            try {
                deactiveGhosts = true;
                Thread.sleep(5000); // Sleep for the specified duration
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            deactiveGhosts = false;
               }).start();
    }
    private void restartGame() {
        for (JLabel label : obstacleLabels) {
            remove(label);
        }
        for (JLabel label : foods) {
            remove(label);
        }
        foods.clear();
        foodItems.clear();
        walls.clear();
        obstacles.clear();
        obstacleLabels.clear();
        initialObstacleCoords.clear();
        initialFoodCoords.clear();
        resetPositions();
        drawMap();
        for (JLabel foodLabel : foods) {
            add(foodLabel, JLayeredPane.MODAL_LAYER);
        }
        revalidate();
        repaint();
    }
    public void gameOver() {
        if (isGameOver) return; // Prevent multiple executions

        isGameOver = true;

        String mapSize = map.length + "x" + map[0].length;
        String name = JOptionPane.showInputDialog(this, "Game Over! Final Score: " + score + "\nPlease enter your name:");
        if (name != null && !name.trim().isEmpty()) {
            saveScoreToFile(new Score(name, score, mapSize)); // Save the name and score to the file
        } else {
            saveScoreToFile(new Score("Anonymous", score, mapSize)); // Save as "Anonymous" if no name is entered
        }

        // Dispose the current game window
        SwingUtilities.getWindowAncestor(this).dispose();

        // Open the menu again
        SwingUtilities.invokeLater(() -> new Menu());
    }
    public void stopGame(){
        running=false;
        if (timerThread.isAlive() && timerThread!=null)timerThread.interrupt();
        if (gameThread.isAlive() && gameThread!=null)gameThread.interrupt();
        if (gameThreadPacman.isAlive() && gameThreadPacman!=null)gameThreadPacman.interrupt();
    }

    private void saveScoreToFile(Score score) {
        List<Score> scores = loadScoresFromFile();
        scores.add(score);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("highscores.dat"))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private List<Score> loadScoresFromFile() {
        List<Score> scores = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("highscores.dat"))) {
            scores = (List<Score>) ois.readObject();
        } catch (FileNotFoundException e) {
            // No scores file found, returning an empty list
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return scores;
    }
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            pacman.keyPressed(e);
        }
    }

    public int[][] getMap() {
        return map;
    }
    public boolean isWall(int[][] arr) {

        for (int[] coord : arr) {
            for (int[] wall : walls) {
                if (wall[0] == coord[0] && wall[1] == coord[1]) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateGhostImage(Ghost ghost) {
        int index = ghosts.indexOf(ghost);
        if (index != -1) {
            ghostLabels.get(index).setIcon(ghost.getImage());
        }
    }
    public synchronized void increaseScoreIncrement() {
        if (isIncreasing) {
            return; // Zaten artırma işlemi devam ediyor, tekrar başlatma
        }
        isIncreasing = true;
        int originalScoreIncrease = scoreIncrease;
        scoreIncrease *= 3; // Çağırıldığı anda 3 kat artır
        System.out.println("Increased scoreIncrease: " + scoreIncrease);
        Thread resetThread = new Thread(() -> {
            try {
                Thread.sleep(5000); // 5 saniye bekle
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                synchronized (this) {
                    scoreIncrease = originalScoreIncrease; // 5 saniye sonra eski değerine geri dön
                    System.out.println("Reset scoreIncrease: " + scoreIncrease);
                    isIncreasing = false;
                }
            }
        });
        resetThread.start();
    }

    public void setSpeedMultiplier(int multiplier, int duration) {
        speedMultiplier = multiplier;
        new Thread(() -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            speedMultiplier = 1;
        }).start();
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(map[0].length * TILE_SIZE, map.length * TILE_SIZE);
    }
    public int getTILE_SIZE() {
        return TILE_SIZE;
    }

    public void changeSizes(int widthScale, int heightScale) {
        // Calculating new sizes
        int newWidth = TILE_SIZE * widthScale;
        int newHeight = TILE_SIZE * heightScale;

        // Resizing pacmanImage
        resizeImage(pacman.getImage(), pacmanLabel, pacman.getX(), pacman.getY(), newWidth, newHeight);

        // Resizing ghosts
        for (int i = 0; i < ghosts.size(); i++) {
            Ghost ghost = ghosts.get(i);
            JLabel label = ghostLabels.get(i);
            resizeImage(ghost.getImage(), label, ghost.getX(), ghost.getY(), newWidth, newHeight);
        }

        // Resizing foods
        for (JLabel foodLabel : foods) {
            Food food = foodItems.get(foods.indexOf(foodLabel));
            resizeImage(food.getImage(), foodLabel, food.getX() * widthScale, food.getY() * heightScale, newWidth, newHeight);
        }

        // Resizing obstacles
        for (JLabel obstacleLabel : obstacleLabels) {
            Obstacle obstacle = obstacles.get(obstacleLabels.indexOf(obstacleLabel));
            resizeImage(obstacle.getImage(), obstacleLabel, obstacle.getX() * widthScale, obstacle.getY() * heightScale, newWidth, newHeight);
        }
        // Ekranı yenile
        revalidate();
        repaint();
    }
    private void resizeImage(ImageIcon icon, JLabel label, int x, int y, int width, int height) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(resizedImage));
        label.setBounds(x, y, width, height);
    }
}



