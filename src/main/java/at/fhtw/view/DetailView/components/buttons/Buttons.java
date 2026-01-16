package at.fhtw.view.DetailView.components.buttons;

import at.fhtw.view.DetailView.DetailView;
import at.fhtw.view.DetailView.components.plots.MultiLinePlot;
import at.fhtw.view.DetailView.components.plots.StackedPlot;
import at.fhtw.view.DetailView.components.plots.StackedPlot;

import javax.swing.*;

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
}
