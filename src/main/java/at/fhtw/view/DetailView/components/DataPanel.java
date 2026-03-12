package at.fhtw.view.DetailView.components;

import at.fhtw.model.Expression;
import at.fhtw.model.InputData;
import at.fhtw.model.Validation;
import at.fhtw.view.DetailView.DetailView;

import javax.swing.*;
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
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setLayout(new GridBagLayout());
        setupDataPanel();
    }

    private void setupDataPanel() {
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        BiConsumer<String, JComponent> addRow = (labelText, valueComponent) -> {
            gbc.gridx = 0;
            gbc.weightx = 0.0;
            gbc.fill = GridBagConstraints.NONE;
            JLabel label = new JLabel(labelText);
            label.setForeground(Colors.TEXT);
            add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            valueComponent.setForeground(Colors.TEXT);
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
        add(new JSeparator(), gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel emotionLabel = new JLabel("Real Emotion:");
        emotionLabel.setForeground(Colors.TEXT);
        add(emotionLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        validationExpressionComboBox = new JComboBox<>(Expression.values());
        validationExpressionComboBox.setForeground(Colors.TEXT);
        add(validationExpressionComboBox, gbc);
        gbc.gridy++;

        validationCommentField = new JTextField();
        validationCommentField.setBackground(Colors.BACKGROUND);
        validationCommentField.setForeground(Colors.TEXT);
        validationCommentField.setCaretColor(Colors.TEXT);
        addRow.accept("Comment:", validationCommentField);

        validationToIdField = new JTextField();
        validationToIdField.setBackground(Colors.BACKGROUND);
        validationToIdField.setForeground(Colors.TEXT);
        validationToIdField.setCaretColor(Colors.TEXT);
        addRow.accept("To ID (opt.):", validationToIdField);

        validateButton = new JButton("Validate Frame(s)");
        validateButton.setBackground(Colors.ACCENT);
        validateButton.setForeground(Color.WHITE);
        validateButton.addActionListener(e -> validateCurrentFrame());

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(validateButton, gbc);
        gbc.gridy++;
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
