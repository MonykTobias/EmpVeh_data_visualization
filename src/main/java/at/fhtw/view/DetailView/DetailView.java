package at.fhtw.view.DetailView;

import at.fhtw.model.InputDataTable;
import at.fhtw.model.ValidationTable;
import at.fhtw.view.DetailView.components.Colors;
import at.fhtw.view.DetailView.components.ControlPanel;
import at.fhtw.view.DetailView.components.DataPanel;
import at.fhtw.view.DetailView.components.PicturePanel;
import at.fhtw.view.DetailView.components.PlotPanel;
import at.fhtw.view.DetailView.components.ValidationTimelinePanel;
import at.fhtw.view.DetailView.components.plots.IPlot;
import at.fhtw.view.DetailView.components.plots.MultiLinePlot;
import at.fhtw.view.View;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

@Getter
@Setter
public class DetailView implements View {
    private final String FOLDERPATH;
    private int currentId = 0;
    private final InputDataTable data;
    private final ValidationTable validationTable;

    private DataPanel dataPanel;
    private PicturePanel picturePanel;
    private PlotPanel plotPanel;
    private ValidationTimelinePanel validationTimelinePanel;
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
        component.setBackground(Colors.BACKGROUND);

        picturePanel = new PicturePanel("Picture Area", this);
        dataPanel = new DataPanel(this);

        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, picturePanel, dataPanel);
        horizontalSplitPane.setResizeWeight(0.6);
        horizontalSplitPane.setBorder(null);
        horizontalSplitPane.setBackground(Colors.BACKGROUND);
        horizontalSplitPane.setDividerSize(5);

        bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(Colors.BACKGROUND);

        plotPanel = new PlotPanel(this);
        this.plotter = new MultiLinePlot(this.data);
        plotPanel.setPlotter(this.plotter);

        validationTimelinePanel = new ValidationTimelinePanel(this);

        GridBagConstraints gbcPlot = new GridBagConstraints();
        gbcPlot.fill = GridBagConstraints.BOTH;
        gbcPlot.gridx = 0;
        gbcPlot.gridy = 0;
        gbcPlot.weightx = 1.0;
        gbcPlot.weighty = 1.0;
        bottomPanel.add(plotPanel, gbcPlot);

        GridBagConstraints gbcTimeline = new GridBagConstraints();
        gbcTimeline.fill = GridBagConstraints.HORIZONTAL;
        gbcTimeline.gridx = 0;
        gbcTimeline.gridy = 1;
        gbcTimeline.weightx = 1.0;
        gbcTimeline.weighty = 0;
        bottomPanel.add(validationTimelinePanel, gbcTimeline);

        ControlPanel controlPanel = new ControlPanel(this);
        GridBagConstraints gbcControl = new GridBagConstraints();
        gbcControl.fill = GridBagConstraints.HORIZONTAL;
        gbcControl.gridx = 0;
        gbcControl.gridy = 2;
        gbcControl.weightx = 1.0;
        gbcControl.weighty = 0;
        bottomPanel.add(controlPanel, gbcControl);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplitPane, bottomPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setBackground(Colors.BACKGROUND);
        splitPane.setDividerSize(5);

        component.add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerLocation(0.5);
            horizontalSplitPane.setDividerLocation(0.5);
        });

        reload();
        return component;
    }

    public void reload() {
        picturePanel.loadPicture();
        dataPanel.loadData();
        plotPanel.updateMarker();
        if (validationTimelinePanel != null) {
            validationTimelinePanel.repaint();
        }
    }

    @Override
    public String getTitle() {
        return "Bildanzeige";
    }

    public void refreshPlotLayout() {
        plotPanel.setupPanel();
        reload();
    }

    public void setPlotter(IPlot plotter) {
        this.plotter = plotter;
        if (plotPanel != null) {
            plotPanel.setPlotter(plotter);
        }
    }

    public void resetPlotZoom() {
        plotPanel.resetZoom();
    }
}