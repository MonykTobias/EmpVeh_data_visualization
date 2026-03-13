package at.fhtw.view.DetailView.components;

import at.fhtw.view.DetailView.DetailView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PicturePanel extends JPanel {
    private BufferedImage image;
    private String initialText;
    private final DetailView detailView;

    public PicturePanel(String initialText, DetailView detailView) {
        this.initialText = initialText;
        this.setBackground(Colors.PANEL_BACKGROUND);
        this.detailView = detailView;
        this.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));
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
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (image != null) {
            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();

            double imgAspect = (double) imgHeight / imgWidth;
            double panelAspect = (double) panelHeight / panelWidth;

            int x, y, w, h;

            if (panelAspect > imgAspect) {
                // Panel is taller than the image, so width is the limiting factor
                w = panelWidth;
                h = (int) (w * imgAspect);
                x = 0;
                y = (panelHeight - h) / 2;
            } else {
                // Panel is wider than the image, so height is the limiting factor
                h = panelHeight;
                w = (int) (h / imgAspect);
                y = 0;
                x = (panelWidth - w) / 2;
            }

            g2d.drawImage(image, x, y, w, h, this);
        } else if (initialText != null) {
            g2d.setColor(Colors.TEXT);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));

            FontMetrics fm = g2d.getFontMetrics();
            int x = (panelWidth - fm.stringWidth(initialText)) / 2;
            int y = ((panelHeight - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(initialText, x, y);
        }
    }

    public void loadPicture() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::loadPicture);
            return;
        }

        String imagePath = String.format("%s%sframe_%06d.jpg", detailView.getFOLDERPATH(), File.separator, detailView.getCurrentId());

        try {
            BufferedImage originalImage;

            try {
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    throw new IOException("File not found at: " + imagePath);
                }
                originalImage = ImageIO.read(imageFile);

            } catch (IOException e) {
                throw new IOException("Failed to load or read image file: " + imagePath, e);
            }

            if (originalImage == null) {
                throw new IOException("ImageIO could not decode the image file: " + imagePath);
            }

            setImage(originalImage);
            repaint();

        } catch (Exception e) {
            e.printStackTrace();
            setErrorText("Error loading image: " + e.getMessage());
            repaint();
        }
    }
}
