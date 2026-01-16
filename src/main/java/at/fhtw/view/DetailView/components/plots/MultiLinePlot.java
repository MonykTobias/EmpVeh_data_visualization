package at.fhtw.view.DetailView.components.plots;

import at.fhtw.model.InputData;
import at.fhtw.model.InputTable;
import lombok.Getter;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.None;

import java.awt.*;
import java.util.List;
import java.util.stream.IntStream;

@Getter
public class MultiLinePlot extends Plot {

    public MultiLinePlot(InputTable table) {
        super(table);
    }

    /*
    * This function creates, saves and returns a chart
    * */
    @Override
    public XYChart getChart() {
        this.chart = new XYChartBuilder()
                .build();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setMarkerSize(0);
        chart.getStyler().setYAxisMin(0.0); // Ensure Y-axis starts at 0
        chart.getStyler().setYAxisMax(1.0);

        // Return empty chart if there is no data
        List<InputData> data = this.inputTable.getInputTable();
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
}
