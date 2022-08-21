package gui.lookandfeel;

import java.awt.Color;

import anton.mankovsky.locomotivedepotlogbook.LocomotiveDepotLogbookApplication;

public final class ColorManager {
  private static ColorManager colorManager;
  private final Color recordsTableHeaderDefaultColor;
  private final Color recordsTableHeaderMediumRepairColor;
  private final Color recordsTableHeaderOverhaulColor;
  private final Color recordsTableModelNameColor;
  private final Color recordsTableLocoNumberColor;
  private final Color recordsTablePrimaryRowColor;
  private final Color recordsTableSecondaryRowColor;
  private final Color recordsTableNextOverhaulWithValueColor;
  private final Color recordsTableLastRepairColor;
  private final Color recordsTableRequiredRepairColor;
  private final Color recordsTableOverdueRequiredRepairTypeColor;
  private final Color recordsTableNotesColor;
  private final Color recordsArchiveTablePrimaryRowColor;
  private final Color recordsArchiveTableSecondaryRowColor;
  private final Color modelsTablePrimaryRowColor;
  private final Color modelsTableSecondaryRowColor;
  
  /**
   * Returns {@code ColorManager} singleton instance. 
   * <p>
   * ColorManager provides colors for GUI components based on application color theme.
   * @return ColorManager with methods for obtain GUI components colors corresponding to
   * application color theme.
   */
  public static ColorManager getColorManager() {
    if (colorManager == null) {
      colorManager = new ColorManager();
    }
    return colorManager;
  }
  
  private ColorManager() {
    super();
    final String theme = LocomotiveDepotLogbookApplication.getUiTheme() == null ? "default"
                       : LocomotiveDepotLogbookApplication.getUiTheme();
    switch (theme) {
    default:
      // fall through
    case "light":
      recordsTableHeaderDefaultColor = new Color(255, 255, 255);
      recordsTableHeaderMediumRepairColor = new Color(100, 150, 250);
      recordsTableHeaderOverhaulColor = new Color(255, 150, 100);
      recordsTableModelNameColor = new Color(68, 203, 199);
      recordsTableLocoNumberColor = recordsTableModelNameColor;
      recordsTablePrimaryRowColor = new Color(220, 220, 220);
      recordsTableSecondaryRowColor = new Color(245, 235, 165);
      recordsTableNextOverhaulWithValueColor = new Color(255, 200, 0);
      recordsTableLastRepairColor = recordsTablePrimaryRowColor;
      recordsTableRequiredRepairColor = recordsTableSecondaryRowColor;
      recordsTableOverdueRequiredRepairTypeColor = new Color(210, 130, 25);
      recordsTableNotesColor = new Color(255, 255, 255);
      
      recordsArchiveTablePrimaryRowColor = recordsTablePrimaryRowColor;
      recordsArchiveTableSecondaryRowColor = recordsTableSecondaryRowColor;
      
      modelsTablePrimaryRowColor = recordsTableSecondaryRowColor;
      modelsTableSecondaryRowColor = recordsTablePrimaryRowColor;
      break;
    case "dark":
      recordsTableHeaderDefaultColor = new Color(25, 25, 25);
      recordsTableHeaderMediumRepairColor = new Color(0, 0, 50);
      recordsTableHeaderOverhaulColor = new Color(50, 0, 0);
      recordsTableModelNameColor = new Color(90, 80, 110);
      recordsTableLocoNumberColor = recordsTableModelNameColor;
      recordsTablePrimaryRowColor = new Color(40, 40, 70);
      recordsTableSecondaryRowColor = new Color(40, 40, 40);
      recordsTableNextOverhaulWithValueColor = new Color(90, 50, 50);
      recordsTableLastRepairColor = recordsTablePrimaryRowColor;
      recordsTableRequiredRepairColor = recordsTableSecondaryRowColor;
      recordsTableOverdueRequiredRepairTypeColor = new Color(75, 35, 35);
      recordsTableNotesColor = new Color(85, 95, 85);
      
      recordsArchiveTablePrimaryRowColor = recordsTablePrimaryRowColor;
      recordsArchiveTableSecondaryRowColor = recordsTableSecondaryRowColor;
      
      modelsTablePrimaryRowColor = recordsTablePrimaryRowColor;
      modelsTableSecondaryRowColor = recordsTableSecondaryRowColor;
      break;
    }
  }
  
  public Color getRecordsTableHeaderMediumRepairColor() {
    return recordsTableHeaderMediumRepairColor;
  }

  public Color getRecordsTableHeaderOverhaulColor() {
    return recordsTableHeaderOverhaulColor;
  }

  public Color getRecordsTableModelNameColor() {
    return recordsTableModelNameColor;
  }

  public Color getRecordsTableLocoNumberColor() {
    return recordsTableLocoNumberColor;
  }

  public Color getRecordsTablePrimaryRowColor() {
    return recordsTablePrimaryRowColor;
  }

  public Color getRecordsTableSecondaryRowColor() {
    return recordsTableSecondaryRowColor;
  }

  public Color getRecordsTableNextOverhaulWithValueColor() {
    return recordsTableNextOverhaulWithValueColor;
  }

  public Color getRecordsTableLastRepairColor() {
    return recordsTableLastRepairColor;
  }

  public Color getRecordsTableRequiredRepairColor() {
    return recordsTableRequiredRepairColor;
  }

  public Color getRecordsTableOverdueRequiredRepairTypeColor() {
    return recordsTableOverdueRequiredRepairTypeColor;
  }

  public Color getRecordsTableNotesColor() {
    return recordsTableNotesColor;
  }

  public Color getRecordsArchiveTablePrimaryRowColor() {
    return recordsArchiveTablePrimaryRowColor;
  }

  public Color getRecordsArchiveTableSecondaryRowColor() {
    return recordsArchiveTableSecondaryRowColor;
  }

  public Color getModelsTablePrimaryRowColor() {
    return modelsTablePrimaryRowColor;
  }

  public Color getModelsTableSecondaryRowColor() {
    return modelsTableSecondaryRowColor;
  }

  public Color getRecordsTableHeaderDefaultColor() {
    return recordsTableHeaderDefaultColor;
  }
  
  
}
