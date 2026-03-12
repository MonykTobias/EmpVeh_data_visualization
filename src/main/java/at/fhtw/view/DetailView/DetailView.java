package at.fhtw.view.DetailView;

import at.fhtw.model.InputDataTable;
import at.fhtw.model.ValidationTable;
import at.fhtw.view.DetailView.components.ControlPanel;
import at.fhtw.view.DetailView.components.DataPanel;
import at.fhtw.view.DetailView.components.ImagePanel;
import at.fhtw.view.DetailView.components.plots.IPlot;
import at.fhtw.view.DetailView.components.plots.MultiLinePlot;
import at.fhtw.view.DetailView.components.plots.PlotPanel;
import at.fhtw.view.View;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class DetailView implements View {
    private final String FOLDERPATH;
    private int currentId = 0;
    private final InputDataTable data;
    private final ValidationTable validationTable;

    private DataPanel dataPanel;
    private ImagePanel imagePanel;
    private PlotPanel plotPanel;
    private IPlot plotter;
    private JPanel bottomPanel;

    public DetailView(InputDataTable data, ValidationTable validationTable, String folderPath) {
        this.data = data;
        this.FOLDERPATH = folderPath;
        this.validationTable = validationTable;
    }

    @Override
    public JComponent load() {
        JComponent component = new JComponent() {
            @Override
            public void setInheritsPopupMenu(boolean value) {
                super.setInheritsPopupMenu(value);
            }
        };
        component.setLayout(new BorderLayout());

        imagePanel = new ImagePanel("Picture Area", Color.decode("#4CAF50"));
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        dataPanel = new DataPanel(this);

        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imagePanel, dataPanel);
        horizontalSplitPane.setResizeWeight(0.8);
        horizontalSplitPane.setBorder(null);

        bottomPanel = new JPanel(new GridBagLayout());

        plotPanel = new PlotPanel(this);
        this.plotter = new MultiLinePlot(this.data);
        plotPanel.setPlotter(this.plotter);
        
        GridBagConstraints gbcPlot = new GridBagConstraints();
        gbcPlot.fill = GridBagConstraints.BOTH;
        gbcPlot.gridx = 0;
        gbcPlot.gridy = 0;
        gbcPlot.weightx = 1.0;
        gbcPlot.weighty = 1.0;
        bottomPanel.add(plotPanel, gbcPlot);

        ControlPanel controlPanel = new ControlPanel(this);
        GridBagConstraints gbcControl = new GridBagConstraints();
        gbcControl.fill = GridBagConstraints.HORIZONTAL;
        gbcControl.gridx = 0;
        gbcControl.gridy = 1;
        gbcControl.weightx = 1.0;
        gbcControl.weighty = 0;
        bottomPanel.add(controlPanel, gbcControl);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplitPane, bottomPanel);
        splitPane.setResizeWeight(0.8);

        component.add(splitPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerLocation(0.8);
            horizontalSplitPane.setDividerLocation(0.8);
        });

        reload();
        return component;
    }

    public void reload() {
        loadPicture();
        dataPanel.loadData();
        plotPanel.updateMarker();
    }

    @Override
    public String getTitle() {
        return "Bildanzeige";
    }

    private void loadPicture() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::loadPicture);
            return;
        }

        if (imagePanel == null) {
            System.err.println("Error: Image Panel is not initialized.");
            return;
        }

        String imagePath = String.format("%s%sframe_%06d.jpg", FOLDERPATH, File.separator, currentId);

        try {
            BufferedImage originalImage;

            try {
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    throw new IOException("File not found at: " + imagePath);
                }
                originalImage = ImageIO.read(imageFile);

            } catch (IOException e) {
                throw new IOException("Failed to load or read image file: " + imagePath, e);
            }

            if (originalImage == null) {
                throw new IOException("ImageIO could not decode the image file: " + imagePath);
            }

            imagePanel.setImage(originalImage);
            imagePanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            imagePanel.setErrorText("Error loading image: " + e.getMessage());
            imagePanel.repaint();
        }
    }

    public void refreshPlotLayout() {
        plotPanel.setupPanel();
        reload();
    }

    public void setPlotter(IPlot plotter) {
        this.plotter = plotter;
        if (plotPanel != null) {
            plotPanel.setPlotter(plotter);
        }
    }
}
