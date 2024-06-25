import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Screen extends JFrame {
    private Board board;

    public Screen(String link) {
        board = new Board(link);
        setTitle("Pacman Game");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Use DO_NOTHING_ON_CLOSE to handle the closing manually
        setLayout(new BorderLayout());
        add(board, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        getContentPane().setBackground(Color.BLACK);
        setResizable(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(
                        Screen.this,
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (response == JOptionPane.YES_OPTION) {
                    board.stopGame();
                    SwingUtilities.invokeLater(() -> new Menu());
                    dispose();
                }
            }
        });
    }


}
