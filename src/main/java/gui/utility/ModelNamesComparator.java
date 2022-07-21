package gui.utility;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Customizes model names in preferred order.
 */
public class ModelNamesComparator implements Comparator<String> {
  private final Map<String, Integer> priority;
  
  public ModelNamesComparator() {
    priority = new HashMap<>(12);
    setPriorities();
  }
  
  private void setPriorities() {
    priority.put("ТЭМ", 1);
    priority.put("ТЭМ2У", 2);
    priority.put("ТЭМ2М", 3);
    priority.put("ТЭМ2УМ", 4);
    priority.put("ТЭМ15", 5);
    priority.put("ТЭМ18", 6);
    priority.put("ТГМ4", 7);
    priority.put("ТГМ4А", 8);
    priority.put("ТГМ4(А)", 9);
    priority.put("ТГМ4Б", 10);
    priority.put("ТГМ4(Б)", 11);
    priority.put("ТГМ4Бл", 12);
  }

  @Override
  public int compare(final String modelName1, final String modelName2 ) {
    final int priority1 =
        priority.containsKey(modelName1) ? priority.get(modelName1) : Integer.MIN_VALUE;
    final int priority2 =
        priority.containsKey(modelName2) ? priority.get(modelName2) : Integer.MIN_VALUE;
    
    return priority1 - priority2;
  }

}
