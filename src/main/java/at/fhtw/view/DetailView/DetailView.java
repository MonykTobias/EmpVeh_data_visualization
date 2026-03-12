package at.fhtw.view.DetailView;

import at.fhtw.model.*;
import at.fhtw.view.DetailView.components.ControlPanel;
import at.fhtw.view.DetailView.components.ImagePanel;
import at.fhtw.view.DetailView.components.plots.IPlot;
import at.fhtw.view.DetailView.components.plots.MultiLinePlot;
import at.fhtw.view.View;
import lombok.Getter;
import lombok.Setter;
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

@Getter
@Setter
public class DetailView implements View {
    private final String FOLDERPATH;
    private int currentId = 0;
    private final InputDataTable data;
    private final ValidationTable validationTable;

    // Fields for data panel labels
    private JLabel idValueLabel;
    private JLabel validationStatusLabel;
    private JLabel expressionBestValueLabel;
    private JLabel expressionBestConfidenceValueLabel;
    private JLabel neutralConfidenceValueLabel;
    private JLabel happyConfidenceValueLabel;
    private JLabel surpriseConfidenceValueLabel;
    private JLabel angerConfidenceValueLabel;

    // Validation fields
    private JComboBox<Expression> validationExpressionComboBox;
    private JTextField validationCommentField;
    private JTextField validationToIdField;
    private JButton validateButton;

    private JPanel dataPanel;
    private ImagePanel imagePanel;
    private JPanel plotPanel;
    @Setter
    private IPlot plotter;
    private JPanel bottomPanel;

    public DetailView(InputDataTable data, ValidationTable validationTable, String folderPath) {
        this.data = data;
        this.FOLDERPATH = folderPath;
        this.validationTable = validationTable;
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

        imagePanel = new ImagePanel("Picture Area", Color.decode("#4CAF50"));
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        dataPanel = createColoredPanel("Information Panel", Color.decode("#2196F3"));
        setupDataPanel();

        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imagePanel, dataPanel);
        horizontalSplitPane.setResizeWeight(0.8);
        horizontalSplitPane.setBorder(null);

        bottomPanel = new JPanel(new GridBagLayout());

        this.plotter = new MultiLinePlot(this.data);
        setupPlotPanel();

        // Control Panel
        ControlPanel controlPanel = new ControlPanel(this);
        GridBagConstraints gbcControl = new GridBagConstraints();
        gbcControl.fill = GridBagConstraints.HORIZONTAL;
        gbcControl.gridx = 0;
        gbcControl.gridy = 1;
        gbcControl.weightx = 1.0;
        gbcControl.weighty = 0;
        bottomPanel.add(controlPanel, gbcControl);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplitPane, bottomPanel);
        splitPane.setResizeWeight(0.8);

        component.add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerLocation(0.8);
            horizontalSplitPane.setDividerLocation(0.8);
        });

        reload();
        return component;
    }

    private void setupPlotPanel() {
        if (plotPanel != null) {
            bottomPanel.remove(plotPanel);
        }
        XYChart chart = this.plotter.getChart();
        plotPanel = new XChartPanel<>(chart);
        plotPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
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
        gbcPlot.weighty = 1.0;
        bottomPanel.add(plotPanel, gbcPlot);
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    private void setupDataPanel() {
        dataPanel.removeAll();
        dataPanel.setLayout(new GridBagLayout());
        dataPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        BiConsumer<String, JComponent> addRow = (labelText, valueComponent) -> {
            gbc.gridx = 0;
            gbc.weightx = 0.0;
            gbc.fill = GridBagConstraints.NONE;
            dataPanel.add(new JLabel(labelText), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            dataPanel.add(valueComponent, gbc);

            gbc.gridy++;
        };

        gbc.gridy = 0;
        idValueLabel = new JLabel("-");
        addRow.accept("ID:", idValueLabel);

        validationStatusLabel = new JLabel("●");
        validationStatusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        addRow.accept("Validated:", validationStatusLabel);

        expressionBestValueLabel = new JLabel("-");
        addRow.accept("Best Expression:", expressionBestValueLabel);

        expressionBestConfidenceValueLabel = new JLabel("-");
        addRow.accept("Confidence:", expressionBestConfidenceValueLabel);

        neutralConfidenceValueLabel = new JLabel("-");
        addRow.accept("Neutral:", neutralConfidenceValueLabel);

        happyConfidenceValueLabel = new JLabel("-");
        addRow.accept("Happy:", happyConfidenceValueLabel);

        surpriseConfidenceValueLabel = new JLabel("-");
        addRow.accept("Surprise:", surpriseConfidenceValueLabel);

        angerConfidenceValueLabel = new JLabel("-");
        addRow.accept("Anger:", angerConfidenceValueLabel);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dataPanel.add(new JSeparator(), gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        dataPanel.add(new JLabel("Real Emotion:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        validationExpressionComboBox = new JComboBox<>(Expression.values());
        dataPanel.add(validationExpressionComboBox, gbc);
        gbc.gridy++;

        validationCommentField = new JTextField();
        addRow.accept("Comment:", validationCommentField);

        validationToIdField = new JTextField();
        addRow.accept("To ID (opt.):", validationToIdField);

        validateButton = new JButton("Validate Frame(s)");
        validateButton.addActionListener(e -> validateCurrentFrame());

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dataPanel.add(validateButton, gbc);
        gbc.gridy++;
    }

    private void validateCurrentFrame() {
        Expression selectedExpression = (Expression) validationExpressionComboBox.getSelectedItem();
        String comment = validationCommentField.getText();

        int endId = currentId;
        String toIdText = validationToIdField.getText();
        if (toIdText != null && !toIdText.trim().isEmpty()) {
            try {
                endId = Integer.parseInt(toIdText.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid To ID");
                return;
            }
        }

        if (endId < currentId) {
            JOptionPane.showMessageDialog(null, "To ID must be >= Current ID");
            return;
        }

        int maxId = data.getInputTable().size() - 1;
        if (endId > maxId) endId = maxId;

        int count = 0;
        for (int i = currentId; i <= endId; i++) {
            InputData inputData = this.data.getInputTable().get(i);
            Validation validation = new Validation(inputData, selectedExpression, comment);
            validationTable.getValidationTable().put(i, validation);
            count++;
        }

        validationStatusLabel.setForeground(Color.GREEN);

        if (count == 1) {
            JOptionPane.showMessageDialog(null, "Validated frame " + currentId);
        } else {
            JOptionPane.showMessageDialog(null, "Validated " + count + " frames (" + currentId + " to " + endId + ")");
        }
    }

    public void reload() {
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
        return "Bildanzeige";
    }

    private void loadData() {
        if (currentId < 0 || currentId >= data.getInputTable().size()) return;

        InputData inputData = this.data.getInputTable().get(currentId);
        DecimalFormat df = new DecimalFormat("0.000");

        idValueLabel.setText(String.valueOf(inputData.getId()));
        expressionBestValueLabel.setText(inputData.getExpression_best().name());
        expressionBestConfidenceValueLabel.setText(df.format(inputData.getExpression_best_confidence()));

        neutralConfidenceValueLabel.setText(df.format(inputData.getExpression_neutral_confidence()));
        happyConfidenceValueLabel.setText(df.format(inputData.getExpression_happy_confidence()));
        surpriseConfidenceValueLabel.setText(df.format(inputData.getExpression_surprise_confidence()));
        angerConfidenceValueLabel.setText(df.format(inputData.getExpression_anger_confidence()));

        Validation validation = validationTable.getValidationTable().get(currentId);
        if (validation != null) {
            validationExpressionComboBox.setSelectedItem(validation.getRealEmotion());
            validationCommentField.setText(validation.getComment());

            if (Boolean.TRUE.equals(validation.getValidated())) {
                validationStatusLabel.setForeground(Color.GREEN);
            } else {
                validationStatusLabel.setForeground(Color.RED);
            }
        } else {
            validationExpressionComboBox.setSelectedItem(inputData.getExpression_best());
            validationCommentField.setText("");
            validationStatusLabel.setForeground(Color.RED);
        }
        validationToIdField.setText("");
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

    public void refreshPlotLayout() {
        setupPlotPanel();
        reload();
    }
}
