package anton.mankovsky.locomotivedepotlogbook;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import gui.GuiManager;

/**
 * Application entry point. 
 */
@SpringBootApplication(scanBasePackageClasses = {gui.GuiManager.class, dbapi.DbManager.class})
public class LocomotiveDepotLogbookApplication {
  private static final Logger logger = LogManager.getLogger();

  /**
   * Runs spring application. Initialize GuiManager bean.
   * <p>
   * Changes headless property to false cause the application is meant to run in environment that
   * supports GUI.
   */
	public static void main(String[] args) {
	   try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	      logger.info("Use system look and feel: success.");
	    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
	        | UnsupportedLookAndFeelException err) {
	      logger.warn("Unable to use system look and feel: " + err.getMessage());
	      err.printStackTrace();
	    }
	   
	  EventQueue.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        final SpringApplication springApplication = 
            new SpringApplication(LocomotiveDepotLogbookApplication.class);
        springApplication.setHeadless(false);
        final ApplicationContext applicationContext = springApplication.run(args);
        applicationContext.getBean(GuiManager.class);
      }
    });
	  
	}

}
