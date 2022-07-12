package gui.eventlisteners;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class NewRecordAction extends AbstractAction {
  
  public NewRecordAction(final String name) {
    super();
    putValue(Action.NAME, name);
  }
  
  @Override
  public void actionPerformed(final ActionEvent e) {
    System.out.println("Creating new row for model named " + getValue(Action.NAME));
  }

}
