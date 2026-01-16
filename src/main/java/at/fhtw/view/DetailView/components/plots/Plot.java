package at.fhtw.view.DetailView.components.plots;

import at.fhtw.model.InputTable;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.None;

import java.awt.*;

public abstract class Plot implements IPlot{
    protected final InputTable inputTable;
    protected XYChart chart;
    protected static final String MARKER_SERIES_NAME = "marker";

    protected Plot(InputTable table) {
        this.inputTable = table;
        this.chart = null;
    }

    @Override
    public void setMarker(int frameId) {
        // Use updateXYSeries for better performance than remove/add
        double[] xData = new double[]{frameId, frameId};
        double[] yData = new double[]{0, 1}; // From bottom to top of the plot

        if (chart.getSeriesMap().containsKey(MARKER_SERIES_NAME)) {
            chart.updateXYSeries(MARKER_SERIES_NAME, xData, yData, null);
        } else {
            XYSeries markerSeries = chart.addSeries(MARKER_SERIES_NAME, xData, yData);
            markerSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            markerSeries.setLineColor(Color.BLUE); // Different color for distinction
            markerSeries.setLineWidth(2);
            markerSeries.setMarker(new None());
            markerSeries.setShowInLegend(false); // Hide "marker" from the legend
        }
    }
}
