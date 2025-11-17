package at.fhtw.view.DetailView;

import at.fhtw.model.InputData;
import at.fhtw.model.InputTable;
import at.fhtw.view.DetailView.components.MultiLinePlot;
import at.fhtw.view.View;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;

public class DetailView implements View{
    private final String FOLDERPATH;
    private final String TITLESTRING = "Bildanzeige";
    private int currentId = 0;
    private final InputTable data;

    // Fields for data panel labels
    private JLabel idValueLabel;
    private JLabel expressionBestValueLabel;
    private JLabel expressionBestConfidenceValueLabel;
    private JLabel neutralConfidenceValueLabel;
    private JLabel happyConfidenceValueLabel;
    private JLabel surpriseConfidenceValueLabel;
    private JLabel angerConfidenceValueLabel;

    private JPanel dataPanel;
    private ImagePanel imagePanel;
    private JPanel controlPanel;
    private JPanel plotPanel;
    private MultiLinePlot plotter;

    // SETTINGS
    private boolean playbackMode = false;
    private Timer playbackTimer;
    private int playBackSpeed = 15;

    public DetailView(InputTable data, String folderPath){
        this.data = data;
        this.FOLDERPATH = folderPath;
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
    public JComponent load() {
        JComponent component = new JComponent() {
            @Override
            public void setInheritsPopupMenu(boolean value) {
                super.setInheritsPopupMenu(value);
            }
        };
        component.setLayout(new BorderLayout());

        /*
         * ############################
         * IMAGE PANEL
         * ############################
         * */
        imagePanel = new ImagePanel("Picture Area", Color.decode("#4CAF50"));
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        /*
         * ############################
         * DATA PANEL
         * ############################
         * */
        dataPanel = createColoredPanel("Information Panel", Color.decode("#2196F3"));
        setupDataPanel();

        /*
         * ############################
         * HORIZONTAL SPLIT PANE
         * ############################
         * */
        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imagePanel, dataPanel);
        horizontalSplitPane.setResizeWeight(0.8);
        horizontalSplitPane.setBorder(null);

        /*
         * ############################
         * BOTTOM PANEL (PLOT AND CONTROLS)
         * ############################
         * */
        JPanel bottomPanel = new JPanel(new GridBagLayout());

        // Plot Panel
        this.plotter = new MultiLinePlot(this.data);
        plotPanel = new XChartPanel<>(this.plotter.getChart());
        plotPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                XYChart chart = plotter.getChart();
                double xValue = chart.getChartXFromCoordinate(e.getX());
                int newId = (int) Math.round(xValue);
                int maxId = data.getInputTable().size() - 1;
                if (newId >= 0 && newId <= maxId) {
                    currentId = newId;
                    reload();
                }
            }
        });
        GridBagConstraints gbcPlot = new GridBagConstraints();
        gbcPlot.fill = GridBagConstraints.BOTH;
        gbcPlot.gridx = 0;
        gbcPlot.gridy = 0;
        gbcPlot.weightx = 1.0;
        gbcPlot.weighty = 1.0; // Plot takes available space in its container
        bottomPanel.add(plotPanel, gbcPlot);

        // Control Panel
        controlPanel = createControlPanel(component);
        GridBagConstraints gbcControl = new GridBagConstraints();
        gbcControl.fill = GridBagConstraints.HORIZONTAL;
        gbcControl.gridx = 0;
        gbcControl.gridy = 1;
        gbcControl.weightx = 1.0;
        gbcControl.weighty = 0; // Control panel should not grow vertically
        bottomPanel.add(controlPanel, gbcControl);

        /*
         * ############################
         * SPLIT PANE
         * ############################
         * */
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplitPane, bottomPanel);
        splitPane.setResizeWeight(0.8); // Give 80% of the extra space to the top panel

        component.add(splitPane, BorderLayout.CENTER);

        // Set the initial divider location after the component is realized.
        // Using invokeLater to ensure that the component has been laid out and has a size.
        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerLocation(0.8);
            horizontalSplitPane.setDividerLocation(0.8);
        });

        reload();
        return component;
    }

    // Setup the DataPanel
    private void setupDataPanel() {
        dataPanel.removeAll(); // Clear the initial "Information Panel" text
        dataPanel.setLayout(new GridBagLayout());
        dataPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // A small lambda to reduce repetitive code for adding label pairs.
        BiConsumer<String, JLabel> addRow = (labelText, valueLabel) -> {
            gbc.gridx = 0;
            gbc.weightx = 0.0; // Label column should not expand
            gbc.fill = GridBagConstraints.NONE;
            dataPanel.add(new JLabel(labelText), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0; // Value column should take up remaining space
            gbc.fill = GridBagConstraints.HORIZONTAL;
            dataPanel.add(valueLabel, gbc);

            gbc.gridy++; // Move to the next row
        };

        // Initialize and add all labels
        gbc.gridy = 0;
        idValueLabel = new JLabel("-");
        addRow.accept("ID:", idValueLabel);

        expressionBestValueLabel = new JLabel("-");
        addRow.accept("Best Expression:", expressionBestValueLabel);

        expressionBestConfidenceValueLabel = new JLabel("-");
        addRow.accept("Confidence:", expressionBestConfidenceValueLabel);

        // Add a visual separator
        //gbc.gridx = 0;
        //gbc.gridwidth = 2;
        //gbc.fill = GridBagConstraints.HORIZONTAL;
        //dataPanel.add(new JSeparator(), gbc);
        //gbc.gridy++;
        //gbc.gridwidth = 1; // Reset gridwidth

        // --- Add individual confidence labels ---
        neutralConfidenceValueLabel = new JLabel("-");
        addRow.accept("Neutral:", neutralConfidenceValueLabel);

        happyConfidenceValueLabel = new JLabel("-");
        addRow.accept("Happy:", happyConfidenceValueLabel);

        surpriseConfidenceValueLabel = new JLabel("-");
        addRow.accept("Surprise:", surpriseConfidenceValueLabel);

        angerConfidenceValueLabel = new JLabel("-");
        addRow.accept("Anger:", angerConfidenceValueLabel);
    }

    private JPanel createControlPanel(Component frame) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#9E9E9E"));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));

        JLabel label = new JLabel("Control Panel");
        label.setForeground(Color.WHITE);
        panel.add(label);

        JTextField idField = new JTextField("Id",8);
        JButton loadButton = new JButton("Load Frame");
        loadButton.addActionListener(e -> {
            try{
                int id = Integer.parseInt(idField.getText());
                currentId = id;

                reload();
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
            }
        });

        // Previouse Button
        JButton prev = new JButton("<");
        prev.addActionListener(e -> {
            if (currentId > 0) {
                currentId--;
                reload();
            }
        });

        // Play Button
        JButton play = new JButton("Play/Pause");
        play.addActionListener(e -> {
            // Already Playing
            if(this.playbackMode){
                pausePlaybackMode();
            // Not Already Playing
            }else{
                startPlaybackMode();
            }
        });

        // Adjust speed of playback
        JTextField playbackSpeed = new JTextField("Speed",8);
        JButton adjustSpeed = new JButton("adjust Speed");
        adjustSpeed.addActionListener(e -> {
            try{
                this.playBackSpeed = Integer.parseInt(playbackSpeed.getText());
                JOptionPane.showMessageDialog(frame, "Successfully changed playBackSpeed to " + this.playBackSpeed + " fps ");
                pausePlaybackMode();
                startPlaybackMode();
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
            }
        });

        // Next Button
        JButton next = new JButton(">");
        next.addActionListener(e -> {
            if (currentId < data.getInputTable().size() - 1) {
                currentId++;
                reload();
            }
        });

        panel.add(idField);
        panel.add(loadButton);
        panel.add(playbackSpeed);
        panel.add(adjustSpeed);
        panel.add(prev);
        panel.add(play);
        panel.add(next);

        return panel;
    }

    private void startPlaybackMode() {
        if (playbackMode) {
            return; // Avoid starting multiple timers
        }
        this.playbackMode = true;

        int delay = 1000 / this.playBackSpeed;
        playbackTimer = new Timer(delay, e -> {
            if (currentId < data.getInputTable().size() - 1) {
                currentId++;
                reload();
            } else {
                pausePlaybackMode();
            }
        });
        playbackTimer.start();
    }

    private void pausePlaybackMode() {
        if (playbackTimer != null && playbackTimer.isRunning()) {
            playbackTimer.stop();
        }
        this.playbackMode = false;
    }

    private void reload() {
        loadPicture();
        loadData();
        plotter.setMarker(currentId);
        plotPanel.repaint();
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
    public String getTitle() {
        return this.TITLESTRING;
    }

    // Load Data given by currentId
    private void loadData() {
        if (currentId < 0 || currentId >= data.getInputTable().size()) return;

        InputData inputData = this.data.getInputTable().get(currentId);
        DecimalFormat df = new DecimalFormat("0.000");

        // Update the text of the labels.
        idValueLabel.setText(String.valueOf(inputData.getId()));
        expressionBestValueLabel.setText(inputData.getExpression_best().name());
        expressionBestConfidenceValueLabel.setText(df.format(inputData.getExpression_best_confidence()));

        neutralConfidenceValueLabel.setText(df.format(inputData.getExpression_neutral_confidence()));
        happyConfidenceValueLabel.setText(df.format(inputData.getExpression_happy_confidence()));
        surpriseConfidenceValueLabel.setText(df.format(inputData.getExpression_surprise_confidence()));
        angerConfidenceValueLabel.setText(df.format(inputData.getExpression_anger_confidence()));
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
