package at.fhtw.view.DetailView;

import at.fhtw.model.InputData;
import at.fhtw.model.InputTable;
import at.fhtw.view.View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class DetailView implements View {
    private final String FOLDERPATH = "data/in/Dataset1/simulator_export";
    private final String TITLESTRING = "Bildanzeige";
    private int currentId = 0;
    private final InputTable data;

    private JPanel dataPanel;
    private ImagePanel imagePanel;
    private JPanel controlPanel;
    private JPanel plotPanel;

    public DetailView(InputTable data){
        this.data = data;
    }

    private static class ImagePanel extends JPanel {
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

    @Override
    public void load(JFrame frame) {
        frame.setLayout(new GridBagLayout());

        imagePanel = new ImagePanel("Picture Area", Color.decode("#4CAF50"));
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        GridBagConstraints gbcPicture = new GridBagConstraints();
        gbcPicture.fill = GridBagConstraints.BOTH;
        gbcPicture.gridx = 0;
        gbcPicture.gridy = 0;
        gbcPicture.weightx = 0.66;
        gbcPicture.weighty = 0.5;
        frame.add(imagePanel, gbcPicture);

        dataPanel = createColoredPanel("Information Panel", Color.decode("#2196F3"));
        GridBagConstraints gbcInfo = new GridBagConstraints();
        gbcInfo.fill = GridBagConstraints.BOTH;
        gbcInfo.gridx = 1;
        gbcInfo.gridy = 0;
        gbcInfo.weightx = 0.34;
        gbcInfo.weighty = 0.5;
        frame.add(dataPanel, gbcInfo);

        JPanel bottomPanel = new JPanel(new GridBagLayout());

        plotPanel = createColoredPanel("Plot Panel", Color.decode("#FFC107"));
        GridBagConstraints gbcPlot = new GridBagConstraints();
        gbcPlot.fill = GridBagConstraints.BOTH;
        gbcPlot.gridx = 0;
        gbcPlot.gridy = 0;
        gbcPlot.weightx = 1.00;
        gbcPlot.weighty = 0.8;
        bottomPanel.add(plotPanel, gbcPlot);

        controlPanel = createControlPanel(frame);
        GridBagConstraints gbcControl = new GridBagConstraints();
        gbcControl.fill = GridBagConstraints.BOTH;
        gbcControl.gridx = 0;
        gbcControl.gridy = 1;
        gbcControl.weightx = 1.0;
        gbcControl.weighty = 0.2;
        bottomPanel.add(controlPanel, gbcControl);

        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.fill = GridBagConstraints.BOTH;
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 1;
        gbcBottom.gridwidth = 2;
        gbcBottom.weightx = 1.0;
        gbcBottom.weighty = 0.5;
        frame.add(bottomPanel, gbcBottom);
    }

    private JPanel createControlPanel(Frame frame) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#9E9E9E"));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));

        JLabel label = new JLabel("Control Panel");
        label.setForeground(Color.WHITE);
        panel.add(label);

        JTextField idField = new JTextField(8);
        JButton loadButton = new JButton("Load Frame");
        loadButton.addActionListener(e -> {
            try{
                int id = Integer.parseInt(idField.getText());
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
        InputData inputData = this.data.getInputTable().get(currentId);

        dataPanel.removeAll();
        dataPanel.setLayout(new GridLayout(0,1,5,5));

        DecimalFormat df = new DecimalFormat("0.000");

        dataPanel.add(new JLabel("id:" + inputData.getId()));

        String expressionLabelText = inputData.getExpression_best().name();
        dataPanel.add(new JLabel("expression_best" + expressionLabelText));

        dataPanel.add(new JLabel("expression_best_confidence"
                + df.format(inputData.getExpression_best_confidence())));

        dataPanel.add(new JLabel("expression_neutral_confidence"
                + df.format(inputData.getExpression_neutral_confidence())));

        dataPanel.add(new JLabel("expression_happy_confidence"
                + df.format(inputData.getExpression_happy_confidence())));

        dataPanel.add(new JLabel("expression_surprise_confidence"
                + df.format(inputData.getExpression_surprise_confidence())));

        dataPanel.add(new JLabel("expression_anger_confidence"
                + df.format(inputData.getExpression_anger_confidence())));

        dataPanel.revalidate();
        dataPanel.repaint();
    }

    private void loadPicture() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::loadPicture);
            return;
        }

        if (imagePanel == null) {
            System.err.println("Error: Image Panel is not initialized.");
            return;
        }

        String imagePath = String.format("%s%sframe_%06d.jpg", FOLDERPATH, File.separator, currentId);

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

            imagePanel.setImage(originalImage);
            imagePanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            imagePanel.setErrorText("Error loading image: " + e.getMessage());
            imagePanel.repaint();
        }
    }
}
