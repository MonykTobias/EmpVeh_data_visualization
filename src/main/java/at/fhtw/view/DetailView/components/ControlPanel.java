package at.fhtw.view.DetailView.components;

import at.fhtw.view.DetailView.DetailView;
import at.fhtw.view.DetailView.components.buttons.Buttons;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private final DetailView detailView;

    // SETTINGS
    private boolean playbackMode = false;
    private Timer playbackTimer;
    private int playBackSpeed = 15;

    public ControlPanel(DetailView detailView) {
        this.detailView = detailView;
        setBackground(Colors.PANEL_BACKGROUND);
        setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        initializeComponents();
    }

    private void initializeComponents() {
        JLabel label = new JLabel("Control Panel");
        label.setForeground(Colors.TEXT);
        add(label);

        JTextField idField = new JTextField("Id", 8);
        styleTextField(idField);

        JTextField playbackSpeed = new JTextField("Speed", 8);
        styleTextField(playbackSpeed);

        JButton loadButton = Buttons.createLoadFrameButton(detailView, idField, this);
        JButton prev = Buttons.createPreviousFrameButton(detailView);
        JButton play = Buttons.createPlaybackToggleButton(() -> {
            if (this.playbackMode) {
                pausePlaybackMode();
            } else {
                startPlaybackMode();
            }
        });
        JButton adjustSpeed = Buttons.createAdjustSpeedButton(playbackSpeed, speed -> {
            this.playBackSpeed = speed;
            if (playbackMode) {
                pausePlaybackMode();
                startPlaybackMode();
            }
        }, this);
        JButton next = Buttons.createNextFrameButton(detailView);
        JButton resetZoom = Buttons.createResetZoomButton(detailView::resetPlotZoom);
        JButton selectPlot = Buttons.selectPlot(detailView);

        add(resetZoom);
        add(idField);
        add(loadButton);
        add(playbackSpeed);
        add(adjustSpeed);
        add(prev);
        add(play);
        add(next);
        add(selectPlot);
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(Colors.BACKGROUND);
        textField.setForeground(Colors.TEXT);
        textField.setCaretColor(Colors.TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private void startPlaybackMode() {
        if (playbackMode) {
            return; 
        }
        this.playbackMode = true;

        int delay = 1000 / this.playBackSpeed;
        playbackTimer = new Timer(delay, e -> {
            int maxId = detailView.getData().getInputTable().size() - 1;
            if (detailView.getCurrentId() < maxId) {
                detailView.setCurrentId(detailView.getCurrentId() + 1);
                detailView.reload();
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
}
