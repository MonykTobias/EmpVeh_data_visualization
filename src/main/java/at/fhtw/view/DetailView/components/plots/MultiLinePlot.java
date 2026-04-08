package at.fhtw.view.DetailView.components.plots;

import at.fhtw.model.InputData;
import at.fhtw.model.InputDataTable;
import lombok.Getter;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

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

        applyCommonFormatting();

        return chart;
    }
}
