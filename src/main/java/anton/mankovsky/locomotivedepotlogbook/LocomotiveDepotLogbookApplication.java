package anton.mankovsky.locomotivedepotlogbook;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;

//import com.formdev.flatlaf.FlatDarkLaf;
//import com.formdev.flatlaf.FlatLightLaf;

import gui.GuiManager;

/**
 * Application entry point. 
 * <p>
 * Custom FlatLaf theme by JFormDesigner open source project: 
 * https://github.com/JFormDesigner/FlatLaf
 */
@SpringBootApplication(scanBasePackageClasses = {gui.GuiManager.class, dbapi.DbManager.class})
public class LocomotiveDepotLogbookApplication {
  private static final Logger logger = LogManager.getLogger();
  private static final Path UI_CONFIG_PATH = Paths.get(".", "UITheme.txt");
  private static String uiTheme;
  /**
   * Runs spring application. Initialize GuiManager bean.
   * <p>
   * Changes headless property to false cause the application is meant to run in environment that
   * supports GUI.
   */
	public static void main(String[] args) {
	  defineUiTheme();
	  setLookAndFeel();
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
	
	private static void setLookAndFeel() {
	  switch (uiTheme) {
    case "light":
      FlatIntelliJLaf.setup();
      break;
    case "dark":
      FlatDarkLaf.setup();
      break;
    default:
      try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (ClassNotFoundException | InstantiationException
              | IllegalAccessException | UnsupportedLookAndFeelException exception) {
        logger.fatal("Unable to set any look and feel theme.");
        System.exit(0);
      }
      break;
    }
	}
	
	private static void defineUiTheme() {
	  String firstLine = "default";
	  try (final BufferedReader reader =
	      Files.newBufferedReader(UI_CONFIG_PATH, Charset.forName("UTF-8"))) {
      firstLine = reader.readLine().trim();
    } catch (final NoSuchFileException noFileException) {
      logger.warn(
          "File \"UITheme.txt\" does not exist! Application will use default color theme."
          + noFileException.getMessage());
    } catch (final IOException ioException) {
      logger.warn(
          "Failed to read \"UITheme.txt\" file. Application will use default color theme."
          + ioException.getMessage());
    } catch (final NullPointerException npe) {
      logger.warn(
          "File \"UITheme.txt\" is empty. Application will use default color theme."
          + npe.getMessage());
    }
	  
	  switch (firstLine) {
    case "uitheme=light":
      uiTheme = "light";
      break;
    case "uitheme=dark":
      uiTheme = "dark";
      break;
    default:
      uiTheme = "default";
      break;
    }
	}
	
	 /**
   * Provides information of user interface theme (light or dark).
   * @return string representing chosen user interface theme
   */
  public static String getUiTheme() {
    return uiTheme;
  }
  
  /**
   * Returns path to user interface configuration file.
   * @return path to file with color theme configuration
   */
  public static Path getUiConfigPath() {
    return UI_CONFIG_PATH;
  }
	
}
