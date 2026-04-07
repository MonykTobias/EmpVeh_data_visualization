package at.fhtw.view.DetailView.components;

import at.fhtw.model.Expression;
import java.awt.Color;
import java.util.Map;

public class Colors {
    public static final Color BACKGROUND = new Color(0x2E2E2E); // Dark Gray
    public static final Color PANEL_BACKGROUND = new Color(0x3C3C3C); // Lighter Gray
    public static final Color BORDER = new Color(0x505050); // Medium Gray
    public static final Color TEXT = new Color(0xE0E0E0); // Light Gray
    public static final Color ACCENT = new Color(0x00A8E8); // Bright Blue
    public static final Color SUCCESS = new Color(0x00C49A); // Green
    public static final Color SECONDARY = new Color(0xFF4500);
    public static final Color ERROR = new Color(0xFF6B6B); // Red

    public static final Map<Expression, Color> EXPRESSION_COLORS = Map.of(
            Expression.NEUTRAL, new Color(0x9E9E9E),   // Gray
            Expression.HAPPY, new Color(0xFFD700),    // Gold
            Expression.SURPRISE, new Color(0x00BFFF), // Deep Sky Blue
            Expression.ANGER, new Color(0xFF4500),    // Orange Red
            Expression.UNDEFINED, new Color(0x92E82F), // green
            Expression.NULL, new Color(0x00000) // black
    );
}
