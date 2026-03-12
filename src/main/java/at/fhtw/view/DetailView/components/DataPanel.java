package at.fhtw.view.DetailView.components;

import at.fhtw.model.Expression;
import at.fhtw.model.InputData;
import at.fhtw.model.Validation;
import at.fhtw.view.DetailView.DetailView;
import at.fhtw.view.DetailView.components.buttons.Buttons;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;

public class DataPanel extends JPanel {
    private final DetailView detailView;

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

    public DataPanel(DetailView detailView) {
        this.detailView = detailView;
        setBackground(Colors.PANEL_BACKGROUND);
        
        // Add a titled border for better grouping
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Colors.BORDER, 1),
            "Frame Information"
        );
        border.setTitleColor(Colors.TEXT);
        border.setTitleFont(new Font("Arial", Font.BOLD, 14));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            border
        ));
        
        setLayout(new GridBagLayout());
        setupDataPanel();
    }

    private void setupDataPanel() {
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 10, 4, 10); // Increase spacing
        gbc.anchor = GridBagConstraints.WEST;

        BiConsumer<String, JComponent> addRow = (labelText, valueComponent) -> {
            gbc.gridx = 0;
            gbc.weightx = 0.0;
            gbc.fill = GridBagConstraints.NONE;
            JLabel label = new JLabel(labelText);
            label.setForeground(Colors.TEXT);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            valueComponent.setForeground(Colors.TEXT);
            valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
            add(valueComponent, gbc);

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

        // Separator
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(Colors.BORDER);
        sep1.setBackground(Colors.PANEL_BACKGROUND);
        add(sep1, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        neutralConfidenceValueLabel = new JLabel("-");
        addRow.accept("Neutral:", neutralConfidenceValueLabel);

        happyConfidenceValueLabel = new JLabel("-");
        addRow.accept("Happy:", happyConfidenceValueLabel);

        surpriseConfidenceValueLabel = new JLabel("-");
        addRow.accept("Surprise:", surpriseConfidenceValueLabel);

        angerConfidenceValueLabel = new JLabel("-");
        addRow.accept("Anger:", angerConfidenceValueLabel);

        // Separator
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(Colors.BORDER);
        sep2.setBackground(Colors.PANEL_BACKGROUND);
        add(sep2, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        
        // Validation Section Title
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel validationTitle = new JLabel("Validation");
        validationTitle.setForeground(Colors.ACCENT);
        validationTitle.setFont(new Font("Arial", Font.BOLD, 14));
        validationTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(validationTitle, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel emotionLabel = new JLabel("Real Emotion:");
        emotionLabel.setForeground(Colors.TEXT);
        emotionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        add(emotionLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        validationExpressionComboBox = new JComboBox<>(Expression.values());
        validationExpressionComboBox.setForeground(Colors.TEXT);
        validationExpressionComboBox.setBackground(Colors.BACKGROUND);
        add(validationExpressionComboBox, gbc);
        gbc.gridy++;

        validationCommentField = new JTextField();
        styleTextField(validationCommentField);
        addRow.accept("Comment:", validationCommentField);

        validationToIdField = new JTextField();
        styleTextField(validationToIdField);
        addRow.accept("To ID (opt.):", validationToIdField);

        validateButton = Buttons.createValidateFramesButton(this::validateCurrentFrame);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // More space for button
        add(validateButton, gbc);
        gbc.gridy++;
        
        // Push everything to the top
        gbc.weighty = 1.0;
        add(new JPanel() {{ setBackground(Colors.PANEL_BACKGROUND); }}, gbc);
    }
    
    private void styleTextField(JTextField textField) {
        textField.setBackground(Colors.BACKGROUND);
        textField.setForeground(Colors.TEXT);
        textField.setCaretColor(Colors.TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
    }

    public void loadData() {
        int currentId = detailView.getCurrentId();
        if (currentId < 0 || currentId >= detailView.getData().getInputTable().size()) return;

        InputData inputData = detailView.getData().getInputTable().get(currentId);
        DecimalFormat df = new DecimalFormat("0.000");

        idValueLabel.setText(String.valueOf(inputData.getId()));
        expressionBestValueLabel.setText(inputData.getExpression_best().name());
        expressionBestConfidenceValueLabel.setText(df.format(inputData.getExpression_best_confidence()));

        neutralConfidenceValueLabel.setText(df.format(inputData.getExpression_neutral_confidence()));
        happyConfidenceValueLabel.setText(df.format(inputData.getExpression_happy_confidence()));
        surpriseConfidenceValueLabel.setText(df.format(inputData.getExpression_surprise_confidence()));
        angerConfidenceValueLabel.setText(df.format(inputData.getExpression_anger_confidence()));

        Validation validation = detailView.getValidationTable().getValidationTable().get(currentId);
        if (validation != null) {
            validationExpressionComboBox.setSelectedItem(validation.getRealEmotion());
            validationCommentField.setText(validation.getComment());

            if (Boolean.TRUE.equals(validation.getValidated())) {
                validationStatusLabel.setForeground(Colors.SUCCESS);
            } else {
                validationStatusLabel.setForeground(Colors.ERROR);
            }
        } else {
            validationExpressionComboBox.setSelectedItem(inputData.getExpression_best());
            validationCommentField.setText("");
            validationStatusLabel.setForeground(Colors.ERROR);
        }
        validationToIdField.setText("");
    }

    private void validateCurrentFrame() {
        Expression selectedExpression = (Expression) validationExpressionComboBox.getSelectedItem();
        String comment = validationCommentField.getText();

        int currentId = detailView.getCurrentId();
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

        int maxId = detailView.getData().getInputTable().size() - 1;
        if (endId > maxId) endId = maxId;

        int count = 0;
        for (int i = currentId; i <= endId; i++) {
            InputData inputData = detailView.getData().getInputTable().get(i);
            Validation validation = new Validation(inputData, selectedExpression, comment);
            detailView.getValidationTable().getValidationTable().put(i, validation);
            count++;
        }

        validationStatusLabel.setForeground(Colors.SUCCESS);

        if (count == 1) {
            JOptionPane.showMessageDialog(null, "Validated frame " + currentId);
        } else {
            JOptionPane.showMessageDialog(null, "Validated " + count + " frames (" + currentId + " to " + endId + ")");
        }
    }
}
