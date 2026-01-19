package at.fhtw.view.DetailView;

import at.fhtw.model.*;
import at.fhtw.view.DetailView.components.ControlPanel;
import at.fhtw.view.DetailView.components.DataPanel;
import at.fhtw.view.DetailView.components.ImagePanel;
import at.fhtw.view.DetailView.components.plots.MultiLinePlot;
import at.fhtw.view.DetailView.components.plots.IPlot;
import at.fhtw.view.View;
import lombok.Getter;
import lombok.Setter;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DetailView implements View{
    private final String FOLDERPATH;
    private int currentId = 0;
    @Getter
    private final InputDataTable data;
    @Getter
    private final ValidationTable validationTable;

    private DataPanel dataPanel;
    private ImagePanel imagePanel;
    private JPanel plotPanel;
    @Setter
    private IPlot plotter;
    private JPanel bottomPanel;

    // SETTINGS
    private boolean playbackMode = false;
    private Timer playbackTimer;
    private int playBackSpeed = 15;

    public DetailView(InputDataTable data, ValidationTable validationTable, String folderPath){
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

        /*
         * ############################
         * IMAGE PANEL
         * ############################
         * */
        imagePanel = new ImagePanel("Picture Area", Color.decode("#4CAF50"));
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        /*
         * ############################
         * DATA PANEL
         * ############################
         * */
        dataPanel = new DataPanel(data, validationTable);

        /*
         * ############################
         * HORIZONTAL SPLIT PANE
         * ############################
         * */
        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imagePanel, dataPanel);
        horizontalSplitPane.setResizeWeight(0.8);
        horizontalSplitPane.setBorder(null);

        /*
         * ############################
         * BOTTOM PANEL (PLOT AND CONTROLS)
         * ############################
         * */
        bottomPanel = new JPanel(new GridBagLayout());

        // Plot Panel
        this.plotter = new MultiLinePlot(this.data);
        setupPlotPanel();

        // Control Panel
        ControlPanel controlPanel = new ControlPanel(this);
        GridBagConstraints gbcControl = new GridBagConstraints();
        gbcControl.fill = GridBagConstraints.HORIZONTAL;
        gbcControl.gridx = 0;
        gbcControl.gridy = 1;
        gbcControl.weightx = 1.0;
        gbcControl.weighty = 0; // Control panel should not grow vertically
        bottomPanel.add(controlPanel, gbcControl);

        /*
         * ############################
         * SPLIT PANE
         * ############################
         * */
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplitPane, bottomPanel);
        splitPane.setResizeWeight(0.8); // Give 80% of the extra space to the top panel

        component.add(splitPane, BorderLayout.CENTER);

        // Set the initial divider location after the component is realized.
        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerLocation(0.8);
            horizontalSplitPane.setDividerLocation(0.8);
        });

        reload();
        return component;
    }

    private void setupPlotPanel() {
        if (plotPanel != null) {
            bottomPanel.remove(plotPanel);
        }
        // Mouse click on plot triggers change of frame
        XYChart chart = this.plotter.getChart();
        plotPanel = new XChartPanel<>(chart);
        plotPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                double xValue = chart.getChartXFromCoordinate(e.getX());
                int newId = (int) Math.round(xValue);
                int maxId = data.getInputTable().size() - 1;
                if (newId >= 0 && newId <= maxId) {
                    currentId = newId;
                    reload();
                }
            }
        });
        GridBagConstraints gbcPlot = new GridBagConstraints();
        gbcPlot.fill = GridBagConstraints.BOTH;
        gbcPlot.gridx = 0;
        gbcPlot.gridy = 0;
        gbcPlot.weightx = 1.0;
        gbcPlot.weighty = 1.0; // Plot takes available space in its container
        bottomPanel.add(plotPanel, gbcPlot);
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    private void startPlaybackMode() {
        if (playbackMode) {
            return; // Avoid starting multiple timers
        }
        this.playbackMode = true;

        int delay = 1000 / this.playBackSpeed;
        playbackTimer = new Timer(delay, e -> {
            if (currentId < data.getInputTable().size() - 1) {
                currentId++;
                reload();
            } else {
                pausePlaybackMode();
            }
        });
        playbackTimer.start();
    }

    private void pausePlaybackMode() {
        if (playbackTimer != null && playbackTimer.isRunning()) {
            playbackTimer.stop();
        }
        this.playbackMode = false;
    }

    private void reload() {
        loadPicture();
        dataPanel.updateData(currentId);
        plotter.setMarker(currentId);
        plotPanel.repaint();
    }

    @Override
    public String getTitle() {
        String TITLESTRING = "Bildanzeige";
        return TITLESTRING;
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
        setupPlotPanel();
        reload();
    }

    // --- Control Panel Interfacing Methods ---

    public void loadFrame(int id) {
        currentId = id;
        reload();
    }

    public void previousFrame() {
        if (currentId > 0) {
            currentId--;
            reload();
        }
    }

    public void nextFrame() {
        if (currentId < data.getInputTable().size() - 1) {
            currentId++;
            reload();
        }
    }

    public void togglePlayback() {
        if(this.playbackMode){
            pausePlaybackMode();
        }else{
            startPlaybackMode();
        }
    }

    public void setPlaybackSpeed(int speed) {
        this.playBackSpeed = speed;
        if(this.playbackMode) {
             pausePlaybackMode();
             startPlaybackMode();
        }
    }
}