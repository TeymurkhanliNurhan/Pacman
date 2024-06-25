import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class sizeMenu extends JFrame implements ActionListener {
    private JButton Button1;
    private JButton Button2;
    private JButton Button3;
    private JButton Button4;
    private JButton Button5;
    private String link = "";
    public sizeMenu() {
        setTitle("Menu");
        setSize(600, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());
        // Butonlar oluşturuluyor
        Button1 = new JButton("11 x 11");
        Button2 = new JButton("15 x 15");
        Button3 = new JButton("15 x 21");
        Button4 = new JButton("19 x 25");
        Button5 = new JButton("25 x 31");
        // Butonlara action listener ekleniyor
        Button1.addActionListener(this);
        Button2.addActionListener(this);
        Button3.addActionListener(this);
        Button4.addActionListener(this);
        Button5.addActionListener(this);
        // Butonlar frame'e ekleniyor
        add(Button1);
        add(Button2);
        add(Button3);
        add(Button4);
        add(Button5);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Button1) {
            link = "nurhan/maps/map1";
        } else if (e.getSource() == Button2) {
            link = "nurhan/maps/map2";
        } else if (e.getSource() == Button3) {
            link = "nurhan/maps/map3";
        } else if (e.getSource() == Button4) {
            link = "nurhan/maps/map4";
        } else if (e.getSource() == Button5) {
            link = "nurhan/maps/map5";
        }

        if (!link.isEmpty()) {
            closeSizeMenu();
            SwingUtilities.invokeLater(() -> new Screen(link));
        }
    }
    private void closeSizeMenu() {
        dispose();
    }
}