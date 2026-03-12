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
        JButton loadButton = new JButton("Load Frame");
        styleButton(loadButton);
        
        loadButton.addActionListener(e -> {
            try {
                String text = idField.getText();
                if (text != null && !text.trim().isEmpty()) {
                    int id = Integer.parseInt(text.trim());
                    // Validate ID range before setting
                    int maxId = detailView.getData().getInputTable().size() - 1;
                    if (id >= 0 && id <= maxId) {
                        detailView.setCurrentId(id);
                        detailView.reload();
                    } else {
                         JOptionPane.showMessageDialog(this, "ID must be between 0 and " + maxId);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        });

        // Previous Button
        JButton prev = new JButton("<");
        styleButton(prev);
        prev.addActionListener(e -> {
            if (detailView.getCurrentId() > 0) {
                detailView.setCurrentId(detailView.getCurrentId() - 1);
                detailView.reload();
            }
        });

        // Play Button
        JButton play = new JButton("Play/Pause");
        styleButton(play);
        play.addActionListener(e -> {
            if (this.playbackMode) {
                pausePlaybackMode();
            } else {
                startPlaybackMode();
            }
        });

        // Adjust speed of playback
        JTextField playbackSpeed = new JTextField("Speed", 8);
        styleTextField(playbackSpeed);
        JButton adjustSpeed = new JButton("adjust Speed");
        styleButton(adjustSpeed);
        adjustSpeed.addActionListener(e -> {
            try {
                String text = playbackSpeed.getText();
                 if (text != null && !text.trim().isEmpty()) {
                    int speed = Integer.parseInt(text.trim());
                    if (speed > 0) {
                        this.playBackSpeed = speed;
                        JOptionPane.showMessageDialog(this, "Successfully changed playBackSpeed to " + this.playBackSpeed + " fps ");
                        // Restart if playing to apply new speed
                        if (playbackMode) {
                            pausePlaybackMode();
                            startPlaybackMode();
                        }
                    } else {
                         JOptionPane.showMessageDialog(this, "Speed must be > 0");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        });

        // Next Button
        JButton next = new JButton(">");
        styleButton(next);
        next.addActionListener(e -> {
            if (detailView.getCurrentId() < detailView.getData().getInputTable().size() - 1) {
                detailView.setCurrentId(detailView.getCurrentId() + 1);
                detailView.reload();
            }
        });

        // Plot selection Button
        JButton selectPlot = Buttons.selectPlot(detailView);
        styleButton(selectPlot);

        add(idField);
        add(loadButton);
        add(playbackSpeed);
        add(adjustSpeed);
        add(prev);
        add(play);
        add(next);
        add(selectPlot);
    }

    private void styleButton(JButton button) {
        button.setBackground(Colors.ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
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
