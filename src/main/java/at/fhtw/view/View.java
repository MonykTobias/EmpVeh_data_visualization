package at.fhtw.view;

import javax.swing.*;

public interface View {
    void load(JFrame frame);
    void close();
    String getTitle();
}
