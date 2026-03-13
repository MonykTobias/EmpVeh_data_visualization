package at.fhtw.view.DetailView.components.plots;

import at.fhtw.model.Expression;
import at.fhtw.model.InputData;
import at.fhtw.model.InputDataTable;
import at.fhtw.view.DetailView.components.Colors;
import lombok.Getter;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.awt.Color;
import java.util.List;
import java.util.stream.IntStream;

@Getter
public class MultiLinePlot extends Plot {

    public MultiLinePlot(InputDataTable table) {
        super(table);
    }

    @Override
    public XYChart getChart() {
        this.chart = new XYChartBuilder().build();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setMarkerSize(0);
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setYAxisMax(1.0);
        
        chart.getStyler().setPlotBackgroundColor(Colors.PANEL_BACKGROUND);
        chart.getStyler().setChartBackgroundColor(Colors.PANEL_BACKGROUND);
        chart.getStyler().setChartFontColor(Colors.TEXT);
        chart.getStyler().setLegendBackgroundColor(Colors.BACKGROUND);
        chart.getStyler().setAxisTickLabelsColor(Colors.TEXT);

        // Set consistent series colors matching the add order below
        Color[] seriesColors = new Color[] {
            Colors.EXPRESSION_COLORS.get(Expression.ANGER),
            Colors.EXPRESSION_COLORS.get(Expression.SURPRISE),
            Colors.EXPRESSION_COLORS.get(Expression.HAPPY),
            Colors.EXPRESSION_COLORS.get(Expression.NEUTRAL)
        };
        chart.getStyler().setSeriesColors(seriesColors);

        List<InputData> data = this.inputTable.getInputTable();
        if (data == null || data.isEmpty()) {
            return chart;
        }

        double[] frameData = IntStream.range(0, data.size()).mapToDouble(i -> i).toArray();

        double[] neutralData = data.stream().mapToDouble(InputData::getExpression_neutral_confidence).toArray();
        double[] happyData = data.stream().mapToDouble(InputData::getExpression_happy_confidence).toArray();
        double[] surpriseData = data.stream().mapToDouble(InputData::getExpression_surprise_confidence).toArray();
        double[] angerData = data.stream().mapToDouble(InputData::getExpression_anger_confidence).toArray();

        // Add series in consistent order: Anger, Surprise, Happy, Neutral
        chart.addSeries("anger", frameData, angerData);
        chart.addSeries("surprise", frameData, surpriseData);
        chart.addSeries("happy", frameData, happyData);
        chart.addSeries("neutral", frameData, neutralData);

        return chart;
    }
}
