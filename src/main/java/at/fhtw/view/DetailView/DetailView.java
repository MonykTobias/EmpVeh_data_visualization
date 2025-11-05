package at.fhtw.view.DetailView;

import at.fhtw.model.InputData;
import at.fhtw.model.InputTable;
import at.fhtw.view.View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class DetailView implements View {
    private final String FOLDERPATH = "data/in/Dataset1/simulator_export";
    private final String TITLESTRING = "Bildanzeige";
    private int currentId = 0;
    private InputTable data;

    private JPanel dataPanel;
    private ImagePanel imagePanel;
    private JPanel controlPanel;

    public DetailView(InputTable data){
        this.data = data;
    }

    private class ImagePanel extends JPanel {
        private BufferedImage image;
        private String initialText;

        public ImagePanel(String initialText, Color color) {
            this.initialText = initialText;
            this.setBackground(color);
        }

        // Setter for the image
        public void setImage(BufferedImage newImage) {
            this.image = newImage;
            this.initialText = null;
            repaint();
        }

        // Setter for error message
        public void setErrorText(String text) {
            this.image = null;
            this.initialText = text;
            repaint();
        }

        // Overrides the paint method to draw the image scaled to the panel's bounds
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int w = getWidth();
            int h = getHeight();

            if (image != null) {
                // Draw the image, scaling it to fit the panel (w, h)
                g2d.drawImage(image, 0, 0, w, h, this);
            } else if (initialText != null) {
                // If no image, display initial text or error text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));

                // Center the text
                FontMetrics fm = g2d.getFontMetrics();
                int x = (w - fm.stringWidth(initialText)) / 2;
                int y = ((h - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(initialText, x, y);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return super.getPreferredSize();
        }
    }

    @Override
    public void load(JFrame frame) {
        frame.setLayout(new GridBagLayout()); // Use GridBagLayout for precise proportional control

        // --- 1. Top-Left Panel (Picture Area) ---
        imagePanel = new ImagePanel("Picture Area", Color.decode("#4CAF50"));
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        GridBagConstraints gbcPicture = new GridBagConstraints();
        gbcPicture.fill = GridBagConstraints.BOTH;
        gbcPicture.gridx = 0;
        gbcPicture.gridy = 0;
        gbcPicture.weightx = 0.66; // 2/3 of the horizontal space
        gbcPicture.weighty = 0.5;  // 1/2 of the vertical space
        frame.add(imagePanel, gbcPicture);

        // --- 2. Top-Right Panel (Information Panel) ---
        dataPanel = createColoredPanel("Information Panel", Color.decode("#2196F3")); // Blue
        GridBagConstraints gbcInfo = new GridBagConstraints();
        gbcInfo.fill = GridBagConstraints.BOTH;
        gbcInfo.gridx = 1;
        gbcInfo.gridy = 0;
        gbcInfo.weightx = 0.34; // 1/3 of the horizontal space (0.66 + 0.34 = 1.0)
        gbcInfo.weighty = 0.5;  // 1/2 of the vertical space
        frame.add(dataPanel, gbcInfo);

        // --- 3. Bottom Panel (Control Panel) ---
        controlPanel = createControlPanel(frame);
        GridBagConstraints gbcControl = new GridBagConstraints();
        gbcControl.fill = GridBagConstraints.BOTH;
        gbcControl.gridx = 0;
        gbcControl.gridy = 1;
        gbcControl.gridwidth = 2; // Span across both columns (0 and 1) for full width
        gbcControl.weightx = 1.0; // It will take the full width since it spans both columns
        gbcControl.weighty = 0.5;  // Takes the remaining 1/2 of the vertical space
        frame.add(controlPanel, gbcControl);

    }

    private JPanel createControlPanel(Frame frame) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));

        JLabel label = new JLabel("Control Pannel");
        label.setForeground(Color.WHITE);
        panel.add(label);

        JTextField idField = new JTextField(8);
        JButton loadButton = new JButton("Load Frame");
        loadButton.addActionListener(e -> {
            try{
                Integer id = Integer.parseInt(idField.getText());
                currentId = id;

                loadPicture();
                loadData();
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
            }
        });
        panel.add(idField);
        panel.add(loadButton);

        return panel;
    }

    private JPanel createColoredPanel(String labelText, Color color) {
        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.setLayout(new GridBagLayout());
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        panel.add(label);
        return panel;
    }


    @Override
    public void close() {

    }

    @Override
    public String getTitle() {
        return this.TITLESTRING;
    }

    private void loadData() {
        InputData data = this.data.getInputTable().get(currentId);

        dataPanel.removeAll();
        dataPanel.setLayout(new GridLayout(0,1,5,5));

        DecimalFormat df = new DecimalFormat("0.000");

        dataPanel.add(new JLabel("id:" + data.getId()));

        String expressionLabelText = data.getExpression_best().name();
        dataPanel.add(new JLabel("expression_best" + expressionLabelText));

        dataPanel.add(new JLabel("expression_best_confidence"
                + df.format(data.getExpression_best_confidence())));

        dataPanel.add(new JLabel("expression_neutral_confidence"
                + df.format(data.getExpression_neutral_confidence())));

        dataPanel.add(new JLabel("expression_happy_confidence"
                + df.format(data.getExpression_happy_confidence())));

        dataPanel.add(new JLabel("expression_surprise_confidence"
                + df.format(data.getExpression_surprise_confidence())));

        dataPanel.add(new JLabel("expression_anger_confidence"
                + df.format(data.getExpression_anger_confidence())));

        dataPanel.revalidate();
        dataPanel.repaint();
    }

    private void loadPicture() {
        // Ensure we are operating on the EDT
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::loadPicture);
            return;
        }

        // Add a null check for safety
        if (imagePanel == null) {
            System.err.println("Error: Image Panel is not initialized.");
            return;
        }

        String imagePath = String.format("%s%sframe_%06d.jpg", FOLDERPATH, File.separator, currentId);

        try {
            // We only need to load the BufferedImage, the custom panel handles scaling automatically.
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

            // The custom panel will handle scaling inside paintComponent().
            imagePanel.setImage(originalImage);

            // Tell Swing to redraw the area
            imagePanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            // Display error message on the custom panel
            imagePanel.setErrorText("Error loading image: " + e.getMessage());
            imagePanel.repaint();
        }
    }
}