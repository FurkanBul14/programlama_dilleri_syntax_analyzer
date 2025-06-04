package programlama_dilleri;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Syntax Highlighter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new EditorPanel());
            frame.setSize(600, 400);
            frame.setVisible(true);
        });
    }
}