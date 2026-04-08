package at.fhtw.view.DetailView.components.plots;

import at.fhtw.model.Expression;
import at.fhtw.model.InputDataTable;
import at.fhtw.view.DetailView.components.Colors;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.None;

import java.awt.*;

public abstract class Plot implements IPlot{
    protected final InputDataTable inputTable;
    protected XYChart chart;
    protected static final String MARKER_SERIES_NAME = "marker";

    protected Plot(InputDataTable table) {
        this.inputTable = table;
        this.chart = null;
    }

    protected void applyCommonFormatting() {
        if (chart == null) return;

        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setChartBackgroundColor(Colors.PANEL_BACKGROUND);
        chart.getStyler().setPlotBackgroundColor(Colors.PANEL_BACKGROUND);
        chart.getStyler().setChartFontColor(Colors.TEXT);
        chart.getStyler().setLegendBackgroundColor(Colors.PANEL_BACKGROUND);
        chart.getStyler().setLegendBorderColor(Colors.BORDER);
        chart.getStyler().setPlotBorderVisible(false);
        chart.getStyler().setAxisTickLabelsColor(Colors.TEXT);

        // Apply colors to specific series if they exist
        if (chart.getSeriesMap().containsKey("neutral")) {
            chart.getSeriesMap().get("neutral").setLineColor(Colors.EXPRESSION_COLORS.get(Expression.NEUTRAL));
            chart.getSeriesMap().get("neutral").setMarker(new None());
        }
        if (chart.getSeriesMap().containsKey("happy")) {
            chart.getSeriesMap().get("happy").setLineColor(Colors.EXPRESSION_COLORS.get(Expression.HAPPY));
            chart.getSeriesMap().get("happy").setMarker(new None());
        }
        if (chart.getSeriesMap().containsKey("surprise")) {
            chart.getSeriesMap().get("surprise").setLineColor(Colors.EXPRESSION_COLORS.get(Expression.SURPRISE));
            chart.getSeriesMap().get("surprise").setMarker(new None());
        }
        if (chart.getSeriesMap().containsKey("anger")) {
            chart.getSeriesMap().get("anger").setLineColor(Colors.EXPRESSION_COLORS.get(Expression.ANGER));
            chart.getSeriesMap().get("anger").setMarker(new None());
        }
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
