
/*  Starı numarısnı yazdırmak için yazdım ama hatalı çalışıyor o yüzden kullanamdım 

package programlama_dilleri;
 
 

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;

public class LineNumberView extends JPanel {
    private final JTextPane textPane;

    public LineNumberView(JTextPane textPane) {
        this.textPane = textPane;
        setPreferredSize(new Dimension(40, Integer.MAX_VALUE));
        setBackground(Color.LIGHT_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Rectangle clip = g.getClipBounds();
        FontMetrics fm = textPane.getFontMetrics(textPane.getFont());
        int lineHeight = fm.getHeight();

        Element root = textPane.getDocument().getDefaultRootElement();
        int startOffset = textPane.viewToModel2D(new Point(0, clip.y));
        int endOffset = textPane.viewToModel2D(new Point(0, clip.y + clip.height));

        int startLine = root.getElementIndex(startOffset);
        int endLine = root.getElementIndex(endOffset);

        for (int i = startLine; i <= endLine; i++) {
            try {
                Rectangle r = textPane.modelToView2D(root.getElement(i).getStartOffset()).getBounds();
                int y = r.y + fm.getAscent();
                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(i + 1), 5, y);
            } catch (Exception ex) {
                // bazı satırlarda görünüm alınamazsa geç
            }
        }
    }



}
*/


