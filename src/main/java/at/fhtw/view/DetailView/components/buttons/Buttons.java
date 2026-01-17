package at.fhtw.view.DetailView.components.buttons;

import at.fhtw.model.ValidationTable;
import at.fhtw.model.helpers.CsvConverter;
import at.fhtw.view.DetailView.DetailView;
import at.fhtw.view.DetailView.components.plots.MultiLinePlot;
import at.fhtw.view.DetailView.components.plots.StackedPlot;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Buttons {
    public static JButton selectPlot(DetailView view){
        JButton button = new JButton("Select Plot");
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

        button.addActionListener(e -> {
            popupMenu.show(button, 0, button.getHeight());
        });
        return button;
    }

    public static JButton saveValidation(DetailView view, String validationCsvPath) {
        JButton button = new JButton("Save Validation");
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
