package at.fhtw.view.DetailView.components;

import at.fhtw.view.DetailView.DetailView;
import at.fhtw.view.DetailView.components.buttons.Buttons;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    public ControlPanel(DetailView view) {
        this.setBackground(Color.decode("#9E9E9E"));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        this.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));

        JLabel label = new JLabel("Control Panel");
        label.setForeground(Color.WHITE);
        this.add(label);

        JTextField idField = new JTextField("Id",8);
        JButton loadButton = new JButton("Load Frame");
        loadButton.addActionListener(e -> {
            try{
                int id = Integer.parseInt(idField.getText());
                view.loadFrame(id);
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        });

        // Previouse Button
        JButton prev = new JButton("<");
        prev.addActionListener(e -> view.previousFrame());

        // Play Button
        JButton play = new JButton("Play/Pause");
        play.addActionListener(e -> view.togglePlayback());

        // Adjust speed of playback
        JTextField playbackSpeed = new JTextField("Speed",8);
        JButton adjustSpeed = new JButton("adjust Speed");
        adjustSpeed.addActionListener(e -> {
            try{
                int speed = Integer.parseInt(playbackSpeed.getText());
                view.setPlaybackSpeed(speed);
                JOptionPane.showMessageDialog(this, "Successfully changed playBackSpeed to " + speed + " fps ");
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        });

        // Next Button
        JButton next = new JButton(">");
        next.addActionListener(e -> view.nextFrame());

        // Plot selection Button
        JButton selectPlot = Buttons.selectPlot(view);

        this.add(idField);
        this.add(loadButton);
        this.add(playbackSpeed);
        this.add(adjustSpeed);
        this.add(prev);
        this.add(play);
        this.add(next);
        this.add(selectPlot);
    }
}
