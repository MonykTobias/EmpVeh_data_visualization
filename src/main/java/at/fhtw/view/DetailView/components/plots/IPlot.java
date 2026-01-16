package at.fhtw.view.DetailView.components.plots;

import at.fhtw.model.InputTable;
import org.knowm.xchart.XYChart;

public interface IPlot {
    XYChart getChart();
    void setMarker(int frameId);
}
