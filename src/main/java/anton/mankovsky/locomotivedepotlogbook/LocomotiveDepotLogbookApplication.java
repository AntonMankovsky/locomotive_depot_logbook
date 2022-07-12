package anton.mankovsky.locomotivedepotlogbook;

import java.awt.EventQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import gui.GuiManager;

/**
 * Application entry point. 
 */
@SpringBootApplication(scanBasePackageClasses = {gui.GuiManager.class, dbapi.DbManager.class})
public class LocomotiveDepotLogbookApplication {

  /**
   * Runs spring application. Initialize GuiManager bean.
   * <p>
   * Changes headless property to false cause the application is meant to run in environment that
   * supports GUI.
   */
	public static void main(String[] args) {
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
