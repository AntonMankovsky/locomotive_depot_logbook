package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dbapi.DbManager;

public class NewModelListener implements ActionListener {
  private final DbManager dbManager;
  
  public NewModelListener(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    System.out.println("Create new model");

  }

}
