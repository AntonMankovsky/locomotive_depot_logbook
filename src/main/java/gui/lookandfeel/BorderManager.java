package gui.lookandfeel;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import anton.mankovsky.locomotivedepotlogbook.LocomotiveDepotLogbookApplication;

public final class BorderManager {
  private static BorderManager borderManager;
  private final Border headerBorder;
  private final Border defaultFocusBorder;
  private final Border defaultNoFocusBorder;
  private final Border noBottomLineBorder;
  private final Border noTopLineBorder;

  /**
   * Returns {@code BorderManager} singleton instance. 
   * <p>
   * BorderManager provides borders for GUI components based on application color theme.
   * @return BorderManager with methods for obtain GUI components borders corresponding to
   * application color theme.
   */
  public static BorderManager getBorderManager() {
    if (borderManager == null) {
      borderManager = new BorderManager();
    }
    return borderManager;
  }
  
  private BorderManager() {
    super();
    final String theme = LocomotiveDepotLogbookApplication.getUiTheme() == null ? "default"
                       : LocomotiveDepotLogbookApplication.getUiTheme();
    final Color color;
    switch (theme) {
    case "dark":
      color = Color.BLACK;
      headerBorder = BorderFactory.createMatteBorder(0, 0, 1, 1, color);
      defaultFocusBorder = BorderFactory.createLineBorder(color, 1);
      defaultNoFocusBorder = BorderFactory.createLineBorder(color, 1);
      noBottomLineBorder = BorderFactory.createMatteBorder(1, 1, 0, 1, color);
      noTopLineBorder = BorderFactory.createMatteBorder(0, 1, 1, 1, color);
      break;
    case "light":
      color = Color.BLACK;
      headerBorder = BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLUE);
      defaultFocusBorder = BorderFactory.createLineBorder(color, 2);
      defaultNoFocusBorder = BorderFactory.createLineBorder(color, 1);
      noBottomLineBorder = BorderFactory.createMatteBorder(1, 1, 0, 1, color);
      noTopLineBorder = BorderFactory.createMatteBorder(0, 1, 1, 1, color);
      break;
    default:
      color = Color.LIGHT_GRAY;
      headerBorder = BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK);
      defaultFocusBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
      defaultNoFocusBorder = BorderFactory.createLineBorder(color, 1);
      noBottomLineBorder = BorderFactory.createMatteBorder(1, 1, 0, 1, color);
      noTopLineBorder = BorderFactory.createMatteBorder(0, 1, 1, 1, color);
      break;

    }
  }

  public Border getDefaultFocusBorder() {
    return defaultFocusBorder;
  }

  public Border getDefaultNoFocusBorder() {
    return defaultNoFocusBorder;
  }

  public Border getNoBottomLineBorder() {
    return noBottomLineBorder;
  }

  public Border getNoTopLineBorder() {
    return noTopLineBorder;
  }

  public Border getHeaderBorder() {
    return headerBorder;
  }
  
}
