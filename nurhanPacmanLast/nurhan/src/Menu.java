import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Menu extends JFrame implements ActionListener {
    // Buttons
    private JButton newGameButton;
    private JButton highScoresButton;
    private JButton exitButton;

    // Constructor
    public Menu() {
        setTitle("Menu");
        setSize(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // Create buttons
        newGameButton = new JButton("New Game");
        highScoresButton = new JButton("High Scores");
        exitButton = new JButton("Exit");

        // Add action listeners to buttons
        newGameButton.addActionListener(this);
        highScoresButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Add buttons to frame
        add(newGameButton);
        add(highScoresButton);
        add(exitButton);
    }

    // Handle button actions
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton) {
            closeMenu();
            SwingUtilities.invokeLater(() -> new sizeMenu());
        } else if (e.getSource() == highScoresButton) {
            displayHighScores();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    // Close the menu
    private void closeMenu() {
        dispose();
    }

    // Display high scores
    private void displayHighScores() {
        List<Score> scores = readScoresFromFile("highscores.dat");
        Collections.sort(scores, (s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));

        JFrame highScoresFrame = new JFrame("High Scores");
        highScoresFrame.setSize(400, 300);
        highScoresFrame.setLocationRelativeTo(null);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Score score : scores) {
            listModel.addElement(score.toString());
        }
        JList<String> scoreList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(scoreList);
        highScoresFrame.add(scrollPane);
        highScoresFrame.setVisible(true);
    }

    // Read scores from file
    private List<Score> readScoresFromFile(String fileName) {
        List<Score> scores = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            scores = (List<Score>) ois.readObject();
        } catch (FileNotFoundException e) {
            // No scores file found, returning an empty list
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return scores;
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Menu());
    }
}
