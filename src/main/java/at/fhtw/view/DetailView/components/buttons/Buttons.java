package at.fhtw.view.DetailView.components.buttons;

import at.fhtw.model.ValidationTable;
import at.fhtw.model.helpers.CsvConverter;
import at.fhtw.view.DetailView.DetailView;
import at.fhtw.view.DetailView.components.Colors;
import at.fhtw.view.DetailView.components.plots.MultiLinePlot;
import at.fhtw.view.DetailView.components.plots.StackedPlot;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.IntConsumer;

public final class Buttons {

    private Buttons() {
    }

    public static void stylePrimary(JButton button) {
        button.setBackground(Colors.ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    public static void styleSecondary(JButton button){
        button.setBackground(Colors.SECONDARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    public static JButton createLoadFrameButton(DetailView view, JTextField idField, Component parent) {
        JButton button = new JButton("Load Frame");
        stylePrimary(button);

        button.addActionListener(e -> {
            try {
                String text = idField.getText();
                if (text != null && !text.trim().isEmpty()) {
                    int id = Integer.parseInt(text.trim());
                    int maxId = view.getData().getInputTable().size() - 1;

                    if (id >= 0 && id <= maxId) {
                        view.setCurrentId(id);
                        view.reload();
                    } else {
                        JOptionPane.showMessageDialog(parent, "ID must be between 0 and " + maxId);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Please enter a valid number.");
            }
        });

        return button;
    }

    public static JButton createPreviousFrameButton(DetailView view) {
        JButton button = new JButton("<");
        stylePrimary(button);

        button.addActionListener(e -> {
            if (view.getCurrentId() > 0) {
                view.setCurrentId(view.getCurrentId() - 1);
                view.reload();
            }
        });

        return button;
    }

    public static JButton createPlaybackToggleButton(Runnable togglePlaybackAction) {
        JButton button = new JButton("Play/Pause");
        stylePrimary(button);
        button.addActionListener(e -> togglePlaybackAction.run());
        return button;
    }

    public static JButton createAdjustSpeedButton(
            JTextField playbackSpeedField,
            IntConsumer onSpeedChanged,
            Component parent
    ) {
        JButton button = new JButton("Adjust Speed");
        stylePrimary(button);

        button.addActionListener(e -> {
            try {
                String text = playbackSpeedField.getText();
                if (text != null && !text.trim().isEmpty()) {
                    int speed = Integer.parseInt(text.trim());
                    if (speed > 0) {
                        onSpeedChanged.accept(speed);
                        JOptionPane.showMessageDialog(parent, "Successfully changed playback speed to " + speed + " fps");
                    } else {
                        JOptionPane.showMessageDialog(parent, "Speed must be > 0");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Please enter a valid number.");
            }
        });

        return button;
    }

    public static JButton createNextFrameButton(DetailView view) {
        JButton button = new JButton(">");
        stylePrimary(button);

        button.addActionListener(e -> {
            if (view.getCurrentId() < view.getData().getInputTable().size() - 1) {
                view.setCurrentId(view.getCurrentId() + 1);
                view.reload();
            }
        });

        return button;
    }

    public static JButton createResetZoomButton(Runnable resetZoomAction) {
        JButton button = new JButton("Reset Zoom");
        styleSecondary(button);
        button.addActionListener(e -> resetZoomAction.run());
        return button;
    }

    public static JButton selectPlot(DetailView view) {
        JButton button = new JButton("Select Plot");
        stylePrimary(button);

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem multiLineItem = new JMenuItem("MultiLine Plot");
        multiLineItem.addActionListener(e -> {
            view.setPlotter(new MultiLinePlot(view.getData()));
            view.refreshPlotLayout();
        });
        popupMenu.add(multiLineItem);

        JMenuItem stackedPlotItem = new JMenuItem("Stacked Plot");
        stackedPlotItem.addActionListener(e -> {
            view.setPlotter(new StackedPlot(view.getData()));
            view.refreshPlotLayout();
        });
        popupMenu.add(stackedPlotItem);

        button.addActionListener(e -> popupMenu.show(button, 0, button.getHeight()));
        return button;
    }

    public static JButton createValidateFramesButton(Runnable validateAction) {
        JButton button = new JButton("Validate Frame(s)");
        stylePrimary(button);
        button.addActionListener(e -> validateAction.run());
        return button;
    }

    public static JButton saveValidation(DetailView view, String validationCsvPath) {
        JButton button = new JButton("Save Validation");
        stylePrimary(button);

        button.addActionListener(e -> {
            try {
                CsvConverter<ValidationTable> converter = new CsvConverter<>(ValidationTable.class);
                String content = converter.serialize(view.getValidationTable());
                Files.writeString(Paths.get(validationCsvPath), content);
                JOptionPane.showMessageDialog(null, "Validation saved successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving validation: " + ex.getMessage());
            }
        });

        return button;
    }
}
