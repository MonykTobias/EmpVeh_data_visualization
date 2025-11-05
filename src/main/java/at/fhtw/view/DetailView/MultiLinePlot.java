package at.fhtw.view.DetailView;

import at.fhtw.model.InputData;
import at.fhtw.model.InputTable;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.None;

import java.awt.*;
import java.util.List;
import java.util.stream.IntStream;

public class MultiLinePlot {
    private final XYChart chart;

    public MultiLinePlot(InputTable table) {
        this.chart = createChart(table);
    }

    private XYChart createChart(InputTable table) {
        final XYChart chart = new XYChartBuilder()
                .xAxisTitle("Frames")
                .yAxisTitle("Confidence")
                .build();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setMarkerSize(0);

        // Return empty chart if there is no data
        List<InputData> data = table.getInputTable();
        if (data == null || data.isEmpty()) {
            return chart;
        }

        double[] frameData = IntStream.range(0, data.size()).mapToDouble(i -> i).toArray();

        // Extract Y-axis data for each expression
        double[] neutralData = data.stream()
                .mapToDouble(InputData::getExpression_neutral_confidence)
                .toArray();

        double[] happyData = data.stream()
                .mapToDouble(InputData::getExpression_happy_confidence)
                .toArray();

        double[] surpriseData = data.stream()
                .mapToDouble(InputData::getExpression_surprise_confidence)
                .toArray();

        double[] angerData = data.stream()
                .mapToDouble(InputData::getExpression_anger_confidence)
                .toArray();

        chart.addSeries("neutral", frameData, neutralData);
        chart.addSeries("happy", frameData, happyData);
        chart.addSeries("surprise", frameData, surpriseData);
        chart.addSeries("anger", frameData, angerData);

        return chart;
    }

    public XYChart getChart() {
        return chart;
    }

    public void setMarker(int frameId) {
        // Remove previous markers
        if (chart.getSeriesMap().containsKey("marker")) {
            chart.removeSeries("marker");
        }

        // Add a vertical line at the specified frameId
        double[] xData = new double[]{frameId, frameId};
        double[] yData = new double[]{0, 1}; // From bottom to top of the plot

        XYSeries markerSeries = chart.addSeries("marker", xData, yData);
        markerSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        markerSeries.setLineColor(Color.RED);
        markerSeries.setLineWidth(2);
        markerSeries.setMarker(new None());
    }
}
