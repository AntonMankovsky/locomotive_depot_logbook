package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dbapi.DbManager;
import gui.GuiManager;

public class DeleteRepairRecordsListener implements ActionListener {
  private final GuiManager guiManager;
  private final DbManager dbManager;
  
  public DeleteRepairRecordsListener(final GuiManager guiManager, final DbManager dbManager) {
    super();
    this.guiManager = guiManager;
    this.dbManager = dbManager;
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    System.out.println("Deleting rows");
  }

}
