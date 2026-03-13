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
public class StackedPlot extends Plot {

    public StackedPlot(InputDataTable table) {
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

        // Set consistent series colors
        Color[] seriesColors = new Color[] {
            Colors.EXPRESSION_COLORS.get(Expression.ANGER),
            Colors.EXPRESSION_COLORS.get(Expression.SURPRISE),
            Colors.EXPRESSION_COLORS.get(Expression.HAPPY),
            Colors.EXPRESSION_COLORS.get(Expression.NEUTRAL)
        };
        chart.getStyler().setSeriesColors(seriesColors);

        List<InputData> data = inputTable.getInputTable();
        if (data == null || data.isEmpty()) {
            return chart;
        }

        double[] frameData = IntStream.range(0, data.size()).mapToDouble(i -> i).toArray();

        int size = data.size();
        double[] neutralData = new double[size];
        double[] happyStacked = new double[size];
        double[] surpriseStacked = new double[size];
        double[] angerStacked = new double[size];

        for (int i = 0; i < size; i++) {
            InputData row = data.get(i);
            double neutral = row.getExpression_neutral_confidence();
            double happy = row.getExpression_happy_confidence();
            double surprise = row.getExpression_surprise_confidence();
            double anger = row.getExpression_anger_confidence();

            neutralData[i] = neutral;
            happyStacked[i] = neutral + happy;
            surpriseStacked[i] = happyStacked[i] + surprise;
            angerStacked[i] = surpriseStacked[i] + anger;
        }

        chart.addSeries("anger", frameData, angerStacked);
        chart.addSeries("surprise", frameData, surpriseStacked);
        chart.addSeries("happy", frameData, happyStacked);
        chart.addSeries("neutral", frameData, neutralData);

        return chart;
    }
}
