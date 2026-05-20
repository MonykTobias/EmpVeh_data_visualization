package at.fhtw.view.DetailView.components;

import at.fhtw.model.InputData;
import at.fhtw.model.Validation;
import at.fhtw.view.DetailView.DetailView;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ValidationTimelinePanel extends JPanel {

    private static final int PREFERRED_HEIGHT = 44;
    private static final int VALIDATION_BAR_HEIGHT = 16;
    private static final int TICK_TOP = 20;
    private static final int MIN_LABEL_SPACING = 74;

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

        List<InputData> inputRows = detailView.getData().getInputTable();
        if (inputRows == null) {
            return;
        }

        int frameCount = inputRows.size();
        if (frameCount <= 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        try {
            double visibleMin = detailView.getPlotPanel().getVisibleXMin();
            double visibleMax = detailView.getPlotPanel().getVisibleXMax();
            if (visibleMax <= visibleMin) {
                return;
            }

            if (!detailView.getPlotPanel().isPlotCoordinateReady()) {
                return;
            }

            int plotLeft = xForFrame(visibleMin);
            int plotRight = xForFrame(visibleMax);
            if (plotRight <= plotLeft) {
                plotLeft = 0;
                plotRight = getWidth();
            }

            paintValidationBands(g2, visibleMin, visibleMax);
            paintCurrentFrameMarker(g2, visibleMin, visibleMax);
            paintTimeTicks(g2, inputRows, visibleMin, visibleMax, plotLeft, plotRight);
        } finally {
            g2.dispose();
        }
    }

    private void paintValidationBands(Graphics2D g2, double visibleMin, double visibleMax) {
        Map<Integer, Validation> validations = detailView.getValidationTable().getValidationTable();
        int firstFrame = Math.max(0, (int) Math.floor(visibleMin - 0.5));
        int lastFrame = Math.min(
                detailView.getData().getInputTable().size() - 1,
                (int) Math.ceil(visibleMax + 0.5)
        );

        for (int frame = firstFrame; frame <= lastFrame; frame++) {
            double frameStart = Math.max(visibleMin, frame - 0.5);
            double frameEnd = Math.min(visibleMax, frame + 0.5);
            if (frameEnd <= frameStart) {
                continue;
            }

            int x1 = xForFrame(frameStart);
            int x2 = xForFrame(frameEnd);
            int segmentWidth = Math.max(1, x2 - x1);

            Validation validation = validations.get(frame);
            boolean isValidated = validation != null && Boolean.TRUE.equals(validation.getValidated());

            g2.setColor(isValidated ? Colors.SUCCESS : Colors.ERROR);
            g2.fillRect(x1, 0, segmentWidth, VALIDATION_BAR_HEIGHT);
        }
    }

    private void paintCurrentFrameMarker(Graphics2D g2, double visibleMin, double visibleMax) {
        int currentId = detailView.getCurrentId();
        if (currentId < visibleMin || currentId > visibleMax) {
            return;
        }

        int markerX = xForFrame(currentId);
        g2.setColor(Color.BLUE);
        g2.drawLine(markerX, 0, markerX, getHeight());
    }

    private void paintTimeTicks(
            Graphics2D g2,
            List<InputData> inputRows,
            double visibleMin,
            double visibleMax,
            int plotLeft,
            int plotRight
    ) {
        long visibleStartMillis = elapsedMillisAtFrame(inputRows, visibleMin);
        long visibleEndMillis = elapsedMillisAtFrame(inputRows, visibleMax);
        long durationMillis = Math.max(0, visibleEndMillis - visibleStartMillis);

        g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        FontMetrics metrics = g2.getFontMetrics();
        g2.setColor(Colors.BORDER);
        g2.drawLine(plotLeft, TICK_TOP, plotRight, TICK_TOP);

        drawTick(g2, visibleStartMillis, plotLeft, metrics, true);
        drawTick(g2, visibleEndMillis, plotRight, metrics, false);

        if (durationMillis <= 0) {
            return;
        }

        long stepMillis = chooseTickStepMillis(durationMillis, Math.max(1, plotRight - plotLeft));
        long firstTick = ((visibleStartMillis / stepMillis) + 1) * stepMillis;
        int previousLabelRight = plotLeft + metrics.stringWidth(formatTime(visibleStartMillis));
        int endLabelLeft = plotRight - metrics.stringWidth(formatTime(visibleEndMillis));

        for (long tickMillis = firstTick; tickMillis < visibleEndMillis; tickMillis += stepMillis) {
            double frame = frameAtElapsedMillis(inputRows, tickMillis);
            int x = xForFrame(frame);
            String label = formatTime(tickMillis);
            int labelWidth = metrics.stringWidth(label);
            int labelLeft = x - labelWidth / 2;
            int labelRight = x + labelWidth / 2;

            if (labelLeft - previousLabelRight < MIN_LABEL_SPACING
                    || endLabelLeft - labelRight < MIN_LABEL_SPACING) {
                continue;
            }

            drawTick(g2, tickMillis, x, metrics, null);
            previousLabelRight = labelRight;
        }
    }

    private void drawTick(
            Graphics2D g2,
            long elapsedMillis,
            int x,
            FontMetrics metrics,
            Boolean edgeLabel
    ) {
        String label = formatTime(elapsedMillis);
        int labelWidth = metrics.stringWidth(label);
        int textX = x - labelWidth / 2;

        if (Boolean.TRUE.equals(edgeLabel)) {
            textX = x;
        } else if (Boolean.FALSE.equals(edgeLabel)) {
            textX = x - labelWidth;
        }

        g2.setColor(Colors.BORDER);
        g2.drawLine(x, TICK_TOP - 4, x, TICK_TOP + 4);
        g2.setColor(Colors.TEXT);
        g2.drawString(label, textX, TICK_TOP + metrics.getAscent() + 3);
    }

    private int xForFrame(double frame) {
        return detailView.getPlotPanel().getScreenXForFrame(frame);
    }

    private long elapsedMillisAtFrame(List<InputData> inputRows, double frame) {
        int lastIndex = inputRows.size() - 1;
        if (lastIndex <= 0) {
            return 0;
        }

        int lowerIndex = Math.max(0, Math.min(lastIndex, (int) Math.floor(frame)));
        int upperIndex = Math.max(0, Math.min(lastIndex, (int) Math.ceil(frame)));

        long lowerMillis = elapsedMillisAtIndex(inputRows, lowerIndex);
        long upperMillis = elapsedMillisAtIndex(inputRows, upperIndex);

        if (lowerIndex == upperIndex) {
            return lowerMillis;
        }

        double ratio = frame - lowerIndex;
        return Math.round(lowerMillis + (upperMillis - lowerMillis) * ratio);
    }

    private long elapsedMillisAtIndex(List<InputData> inputRows, int index) {
        Date firstTimestamp = inputRows.get(0).getTime_stamp();
        Date timestamp = inputRows.get(index).getTime_stamp();
        if (firstTimestamp == null || timestamp == null) {
            return index * 1000L;
        }

        return Math.max(0, timestamp.getTime() - firstTimestamp.getTime());
    }

    private double frameAtElapsedMillis(List<InputData> inputRows, long elapsedMillis) {
        int lastIndex = inputRows.size() - 1;
        if (lastIndex <= 0) {
            return 0;
        }

        for (int i = 1; i <= lastIndex; i++) {
            long previousMillis = elapsedMillisAtIndex(inputRows, i - 1);
            long currentMillis = elapsedMillisAtIndex(inputRows, i);

            if (elapsedMillis <= currentMillis) {
                long span = currentMillis - previousMillis;
                if (span <= 0) {
                    return i;
                }

                double ratio = (double) (elapsedMillis - previousMillis) / span;
                return (i - 1) + ratio;
            }
        }

        return lastIndex;
    }

    private long chooseTickStepMillis(long durationMillis, int plotWidth) {
        long[] steps = {
                1_000L, 2_000L, 5_000L, 10_000L, 15_000L, 30_000L,
                60_000L, 120_000L, 300_000L, 600_000L, 900_000L, 1_800_000L
        };

        long targetTickCount = Math.max(2, plotWidth / MIN_LABEL_SPACING);
        long targetStep = Math.max(1_000L, durationMillis / targetTickCount);

        for (long step : steps) {
            if (step >= targetStep) {
                return step;
            }
        }

        return steps[steps.length - 1];
    }

    private String formatTime(long elapsedMillis) {
        long totalSeconds = Math.max(0, Math.round(elapsedMillis / 1000.0));
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
