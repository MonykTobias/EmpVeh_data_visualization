package at.fhtw.view.DetailView.components;

import at.fhtw.view.DetailView.DetailView;
import at.fhtw.view.DetailView.components.plots.IPlot;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PlotPanel extends JPanel {
    private final DetailView detailView;
    private IPlot plotter;
    private XChartPanel<XYChart> chartPanel;

    public PlotPanel(DetailView detailView) {
        super(new BorderLayout());
        this.detailView = detailView;
    }

    public void setupPanel() {
        if (chartPanel != null) {
            this.remove(chartPanel);
        }

        XYChart chart = this.plotter.getChart();
        chartPanel = new XChartPanel<>(chart);
        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                double xValue = chart.getChartXFromCoordinate(e.getX());
                int newId = (int) Math.round(xValue);
                int maxId = detailView.getData().getInputTable().size() - 1;
                if (newId >= 0 && newId <= maxId) {
                    detailView.setCurrentId(newId);
                    detailView.reload();
                }
            }
        });
        this.add(chartPanel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    public void updateMarker() {
        if (plotter != null) {
            plotter.setMarker(detailView.getCurrentId());
            if (chartPanel != null) {
                chartPanel.repaint();
            }
        }
    }

    public void setPlotter(IPlot plotter) {
        this.plotter = plotter;
        setupPanel();
    }
}
