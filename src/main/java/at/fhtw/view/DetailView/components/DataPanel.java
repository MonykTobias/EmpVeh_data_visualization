package at.fhtw.view.DetailView.components;

import at.fhtw.model.Expression;
import at.fhtw.model.InputData;
import at.fhtw.model.InputDataTable;
import at.fhtw.model.Validation;
import at.fhtw.model.ValidationTable;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;

public class DataPanel extends JPanel {
    private final InputDataTable data;
    private final ValidationTable validationTable;
    private int currentId = 0;

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

    public DataPanel(InputDataTable data, ValidationTable validationTable) {
        this.data = data;
        this.validationTable = validationTable;
        initialize();
    }

    private void initialize() {
        this.setBackground(Color.decode("#2196F3"));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // A small lambda to reduce repetitive code for adding label pairs.
        BiConsumer<String, JComponent> addRow = (labelText, valueComponent) -> {
            gbc.gridx = 0;
            gbc.weightx = 0.0; // Label column should not expand
            gbc.fill = GridBagConstraints.NONE;
            this.add(new JLabel(labelText), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0; // Value column should take up remaining space
            gbc.fill = GridBagConstraints.HORIZONTAL;
            this.add(valueComponent, gbc);

            gbc.gridy++; // Move to the next row
        };

        // Initialize and add all labels
        gbc.gridy = 0;
        
        // Header
        JLabel headerLabel = new JLabel("Information Panel");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        
        // We need to add the header manually or change the layout strategy slightly 
        // compared to the original DetailView "createColoredPanel".
        // In the original, createColoredPanel added the label, and setupDataPanel removeAll()'d it.
        // Let's stick to the setupDataPanel logic but keep the header if we want it, 
        // or re-add it. The original `setupDataPanel` did `dataPanel.removeAll()`, so the header was LOST!
        // Looking at the code: `dataPanel.removeAll(); // Clear the initial "Information Panel" text`
        // So the header "Information Panel" was actually removed in the original code? 
        // Let's check `setupDataPanel` in `DetailView.java` again.
        // `dataPanel = createColoredPanel("Information Panel", ...)` -> adds JLabel.
        // `setupDataPanel()` -> `dataPanel.removeAll()`. 
        // Yes, the "Information Panel" title is removed. I will reproduce this behavior.
        
        idValueLabel = new JLabel("-");
        addRow.accept("ID:", idValueLabel);

        validationStatusLabel = new JLabel("●");
        validationStatusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        addRow.accept("Validated:", validationStatusLabel);

        expressionBestValueLabel = new JLabel("-");
        addRow.accept("Best Expression:", expressionBestValueLabel);

        expressionBestConfidenceValueLabel = new JLabel("-");
        addRow.accept("Confidence:", expressionBestConfidenceValueLabel);

        // --- Add individual confidence labels ---
        neutralConfidenceValueLabel = new JLabel("-");
        addRow.accept("Neutral:", neutralConfidenceValueLabel);

        happyConfidenceValueLabel = new JLabel("-");
        addRow.accept("Happy:", happyConfidenceValueLabel);

        surpriseConfidenceValueLabel = new JLabel("-");
        addRow.accept("Surprise:", surpriseConfidenceValueLabel);

        angerConfidenceValueLabel = new JLabel("-");
        addRow.accept("Anger:", angerConfidenceValueLabel);

        // --- Validation Section ---
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(new JSeparator(), gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        // Manually add the combo box row
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        this.add(new JLabel("Real Emotion:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        validationExpressionComboBox = new JComboBox<>(Expression.values());
        this.add(validationExpressionComboBox, gbc);
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
        this.add(validateButton, gbc);
        gbc.gridy++;
    }

    public void updateData(int id) {
        this.currentId = id;
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

        // Update Validation Fields
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
            // Default to current best expression if no validation exists
            validationExpressionComboBox.setSelectedItem(inputData.getExpression_best());
            validationCommentField.setText("");
            validationStatusLabel.setForeground(Color.RED);
        }
        validationToIdField.setText("");
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
                JOptionPane.showMessageDialog(this, "Invalid To ID");
                return;
            }
        }

        if (endId < currentId) {
            JOptionPane.showMessageDialog(this, "To ID must be >= Current ID");
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
        
        // Update status label for current frame
        validationStatusLabel.setForeground(Color.GREEN);

        // Visual feedback
        if (count == 1) {
            JOptionPane.showMessageDialog(this, "Validated frame " + currentId);
        } else {
            JOptionPane.showMessageDialog(this, "Validated " + count + " frames (" + currentId + " to " + endId + ")");
        }
    }
}
