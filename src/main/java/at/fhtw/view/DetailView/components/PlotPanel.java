package at.fhtw.view.DetailView.components;

import at.fhtw.model.Expression;
import at.fhtw.view.DetailView.DetailView;
import at.fhtw.view.DetailView.components.plots.IPlot;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PlotPanel extends JPanel {
    private static final int CLICK_DRAG_TOLERANCE = 5;

    private final DetailView detailView;
    private IPlot plotter;
    private XChartPanel<XYChart> chartPanel;

    private Double visibleXMin;
    private Double visibleXMax;

    public PlotPanel(DetailView detailView) {
        super(new BorderLayout());
        this.detailView = detailView;
    }

    public void setupPanel() {
        if (chartPanel != null) {
            this.remove(chartPanel);
        }

        XYChart chart = this.plotter.getChart();
        configureChart(chart);

        chartPanel = new XChartPanel<>(chart);
        installMouseHandling(chart);
        styleChart(chart);


        this.add(chartPanel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void styleChart(XYChart chart){
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setYAxisTicksVisible(false);
        //chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setMarkerSize(0);
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setYAxisMax(1.0);
        chart.getStyler().setPlotContentSize(1.0);

        chart.getStyler().setPlotBackgroundColor(Colors.PANEL_BACKGROUND);
        chart.getStyler().setChartBackgroundColor(Colors.PANEL_BACKGROUND);
        chart.getStyler().setChartFontColor(Colors.TEXT);
        chart.getStyler().setLegendBackgroundColor(Colors.BACKGROUND);
        chart.getStyler().setAxisTickLabelsColor(Colors.TEXT);

        // Set consistent series colors
        Color[] seriesColors = new Color[] {
                Colors.EXPRESSION_COLORS.get(Expression.ANGER),
                Colors.EXPRESSION_COLORS.get(Expression.SURPRISE),
                Colors.EXPRESSION_COLORS.get(Expression.HAPPY),
                Colors.EXPRESSION_COLORS.get(Expression.NEUTRAL)
        };
        chart.getStyler().setSeriesColors(seriesColors);
    }
    
    public void resetZoom() {
        visibleXMin = null;
        visibleXMax = null;

        if (plotter != null) {
            rebuildChart();
        }
    }

    public void updateMarker() {
        if (plotter == null) {
            return;
        }

        int currentId = detailView.getCurrentId();

        if (visibleXMin != null && visibleXMax != null) {
            double width = visibleXMax - visibleXMin;

            if (width > 0 && (currentId < visibleXMin || currentId > visibleXMax)) {
                moveViewportToCurrentFrame(currentId, width);
                rebuildChart();
                return;
            }
        }

        plotter.setMarker(currentId);

        if (chartPanel != null) {
            chartPanel.repaint();
        }
    }

    public void setPlotter(IPlot plotter) {
        this.plotter = plotter;
        setupPanel();
    }

    private void rebuildChart() {
        setupPanel();
        plotter.setMarker(detailView.getCurrentId());
        if (chartPanel != null) {
            chartPanel.repaint();
        }
    }

    private void configureChart(XYChart chart) {
        chart.getStyler().setZoomEnabled(true);
        chart.getStyler().setZoomResetByButton(false);

        if (visibleXMin != null && visibleXMax != null) {
            chart.getStyler().setXAxisMin(visibleXMin);
            chart.getStyler().setXAxisMax(visibleXMax);
        }
    }

    private void installMouseHandling(XYChart chart) {
        chartPanel.addMouseListener(new MouseAdapter() {
            private Point mousePressedPoint;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mousePressedPoint = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (mousePressedPoint == null || e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }

                int deltaX = Math.abs(e.getX() - mousePressedPoint.x);
                int deltaY = Math.abs(e.getY() - mousePressedPoint.y);
                boolean isClick = deltaX <= CLICK_DRAG_TOLERANCE && deltaY <= CLICK_DRAG_TOLERANCE;

                if (!isClick) {
                    storeZoomRange(chart, mousePressedPoint, e.getPoint());
                    rebuildChart();
                    return;
                }

                if (e.isShiftDown()) {
                    return;
                }

                double xValue = chart.getChartXFromCoordinate(e.getX());
                int newId = (int) Math.round(xValue);
                int maxId = detailView.getData().getInputTable().size() - 1;

                if (newId >= 0 && newId <= maxId) {
                    detailView.setCurrentId(newId);
                    detailView.reload();
                }
            }
        });
    }

    private void storeZoomRange(XYChart chart, Point start, Point end) {
        double x1 = chart.getChartXFromCoordinate(start.x);
        double x2 = chart.getChartXFromCoordinate(end.x);

        double min = Math.min(x1, x2);
        double max = Math.max(x1, x2);

        if (Math.abs(max - min) < 1.0) {
            return;
        }

        int maxId = detailView.getData().getInputTable().size() - 1;
        visibleXMin = Math.max(0, min);
        visibleXMax = Math.min(maxId, max);
    }

    private void moveViewportToCurrentFrame(int currentId, double width) {
        int maxId = detailView.getData().getInputTable().size() - 1;

        double newMin = currentId - width * 0.1;
        double newMax = newMin + width;

        if (newMin < 0) {
            newMin = 0;
            newMax = width;
        }

        if (newMax > maxId) {
            newMax = maxId;
            newMin = Math.max(0, newMax - width);
        }

        visibleXMin = newMin;
        visibleXMax = newMax;
    }
}
