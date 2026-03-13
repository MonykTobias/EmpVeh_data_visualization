package at.fhtw.view.DetailView.components;

import at.fhtw.model.Validation;
import at.fhtw.view.DetailView.DetailView;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ValidationTimelinePanel extends JPanel {

    private static final int PREFERRED_HEIGHT = 22;

    private final DetailView detailView;

    public ValidationTimelinePanel(DetailView detailView) {
        this.detailView = detailView;
        setPreferredSize(new Dimension(0, PREFERRED_HEIGHT));
        setMinimumSize(new Dimension(0, PREFERRED_HEIGHT));
        setBackground(Colors.PANEL_BACKGROUND);
        setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));
        setToolTipText("Validation timeline");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int frameCount = detailView.getData().getInputTable().size();
        if (frameCount <= 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        Map<Integer, Validation> validations = detailView.getValidationTable().getValidationTable();

        for (int frame = 0; frame < frameCount; frame++) {
            int x1 = (int) Math.floor((double) frame * width / frameCount);
            int x2 = (int) Math.floor((double) (frame + 1) * width / frameCount);
            int segmentWidth = Math.max(1, x2 - x1);

            Validation validation = validations.get(frame);
            boolean isValidated = validation != null && Boolean.TRUE.equals(validation.getValidated());

            g.setColor(isValidated ? Colors.SUCCESS : Colors.ERROR);
            g.fillRect(x1, 0, segmentWidth, height);
        }

        int currentId = detailView.getCurrentId();
        int markerX = (int) Math.round((double) currentId * width / Math.max(1, frameCount - 1));
        g.setColor(Color.BLUE);
        g.drawLine(markerX, 0, markerX, height);
    }
}