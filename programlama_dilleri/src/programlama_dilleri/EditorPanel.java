package programlama_dilleri;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EditorPanel extends JPanel {
    private JTextPane textPane; // Kod yazılacak metin alanı
    private LexicalAnalyzer lexer; // Kelime analizcisi (token'ları ayırır)
    private Highlighter highlighter; // Renklendirme sınıfı
    private JLabel statusLabel; // Alt kısımdaki hata/başarı durumu
    private Timer highlightTimer; // Yazarken gecikmeli analiz tetikler
    // private LineNumberView lineNumberView; // Satır numaralarını gösterir (artık kullanılmıyor)

    public EditorPanel() {
        setLayout(new BorderLayout());

        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Yazı tipi ayarı

        JScrollPane scrollPane = new JScrollPane(textPane);
        // scrollPane.setRowHeaderView(new LineNumberView(textPane)); // Satır numarası gösterimi kaldırıldı
        add(scrollPane, BorderLayout.CENTER);

        lexer = new LexicalAnalyzer();
        highlighter = new Highlighter();

        // Gerçek zamanlı analiz için DocumentListener (kullanıcı her yazdığında tetiklenir)
        textPane.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                restartTimer(); // Gecikmeli kontrol başlatılır
                // lineNumberView.repaint(); // Artık kullanılmıyor
            }
            public void removeUpdate(DocumentEvent e) {
                restartTimer();
                // lineNumberView.repaint();
            }
            public void changedUpdate(DocumentEvent e) {
                restartTimer();
                // lineNumberView.repaint();
            }
        });

        statusLabel = new JLabel("Yapı kontrolü bekleniyor..."); // Alt bilgi etiketi
        add(statusLabel, BorderLayout.SOUTH);

        highlightTimer = new Timer(300, e -> highlight()); // 300ms sonra highlight çalışır
        highlightTimer.setRepeats(false); // Tek seferlik tetiklenir
    }

    // Renklendirme ve sözdizimi analizi yapılır
    private void highlight() {
        SwingUtilities.invokeLater(() -> {
            String code = textPane.getText(); // Kod alınır
            List<LexicalAnalyzer.TokenWithPosition> tokensWithPos = lexer.analyzeWithPositions(code); // Token'lar alınır
            List<Token> tokens = new ArrayList<>();
            for (LexicalAnalyzer.TokenWithPosition twp : tokensWithPos) {
                tokens.add(twp.token);
            }

            StyledDocument doc = textPane.getStyledDocument();

            // Tüm metin varsayılan siyah olarak temizlenir
            Style defaultStyle = textPane.addStyle("default", null);
            StyleConstants.setForeground(defaultStyle, Color.BLACK);
            doc.setCharacterAttributes(0, code.length(), defaultStyle, true);

            // Her token'a göre renk uygulanır
            for (LexicalAnalyzer.TokenWithPosition twp : tokensWithPos) {
                Token token = twp.token;
                int start = twp.startPos;
                int length = twp.endPos - twp.startPos;

                Style style = textPane.addStyle("temp" + token.hashCode(), null);
                String colorHex = highlighter.getColor(token.getType());
                StyleConstants.setForeground(style, Color.decode(colorHex));

                try {
                    doc.setCharacterAttributes(start, length, style, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // SyntaxAnalyzer ile yapı kontrolü yapılır
            SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer();
            SyntaxAnalyzer.SyntaxResult result = syntaxAnalyzer.analyzeWithPositions(tokensWithPos);

            // Sonuç GUI'de alt etikette gösterilir
            if (statusLabel != null) {
                statusLabel.setText("<html>" + result.message.replace("\n", "<br>") + "</html>");
                statusLabel.setForeground(result.message.startsWith("✅") ? Color.GREEN.darker() : Color.RED);
            }

            // Hatalı pozisyon varsa, altını kırmızı çizer
            if (result.startPos >= 0 && result.endPos > result.startPos) {
                StyleContext sc = StyleContext.getDefaultStyleContext();
                AttributeSet redUnderline = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Underline, true);
                redUnderline = sc.addAttribute(redUnderline, StyleConstants.Foreground, Color.RED);
                doc.setCharacterAttributes(result.startPos, result.endPos - result.startPos, redUnderline, false);
            }
        });
    }

    // Timer çalışıyorsa sıfırlar, değilse başlatır
    private void restartTimer() {
        if (highlightTimer.isRunning()) {
            highlightTimer.restart();
        } else {
            highlightTimer.start();
        }
    }
}
