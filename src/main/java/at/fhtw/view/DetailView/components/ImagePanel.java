package at.fhtw.view.DetailView.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage image;
    private String initialText;

    public ImagePanel(String initialText, Color color) {
        this.initialText = initialText;
        this.setBackground(color);
    }

    public void setImage(BufferedImage newImage) {
        this.image = newImage;
        this.initialText = null;
        repaint();
    }

    public void setErrorText(String text) {
        this.image = null;
        this.initialText = text;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        if (image != null) {
            g2d.drawImage(image, 0, 0, w, h, this);
        } else if (initialText != null) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));

            FontMetrics fm = g2d.getFontMetrics();
            int x = (w - fm.stringWidth(initialText)) / 2;
            int y = ((h - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(initialText, x, y);
        }
    }
}
