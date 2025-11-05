package at.fhtw.view;

import javax.swing.*;
import java.awt.*;


public class MainFrame {

    private View view;
    private JFrame frame;

    public MainFrame(View view){
        this.view = view;
        this.frame = new JFrame(view.getTitle());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // TODO: Maybe add nav here
        view.load(frame);

        frame.setSize(900, 1600);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // needs to be used when new view is loaded
    public void replaceView(View view){
        this.view = view;
        clearPanel();
        view.load(frame);
    }

    private void clearPanel(){
        frame.getContentPane().removeAll();
    }
}
