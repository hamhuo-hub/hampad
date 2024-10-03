package hampad;

import java.io.IOException;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class PadStarter {
    /**
     * Main method to start the application. It loads configuration from a YAML file and starts
     * the main application window.
     *
     * @param args Command line arguments (not used in this application)
     * @throws IOException if there is an issue loading the configuration file
     */
    public static void main(String[] args) throws IOException {
        // Load the YAML configuration file located in resources folder
        InputStream inputStream = PadStarter.class
                .getClassLoader()
                .getResourceAsStream("text_editor_config.yaml");
        Yaml yaml = new Yaml();
        // Parse the YAML file to a Map structure
        Map<String, Object> data = yaml.load(inputStream);

        // Extract the configuration data specific to the text editor
        Map<String, Object> textEditorConfig = (Map<String, Object>) data.get("text_editor");
        String defaultTextFormat = (String) textEditorConfig.get("default_text_format");
        String defaultFontColor = (String) textEditorConfig.get("default_font_color");
        int defaultFontSize = (Integer) textEditorConfig.get("default_font_size");

        // Print the loaded configuration to the console
        System.out.println("Default Text Format: " + defaultTextFormat);
        System.out.println("Default Font Color: " + defaultFontColor);
        System.out.println("Default Font Size: " + defaultFontSize);

        // Starting the main application window
        new PadService(); // Initialize the main service of the application
    }
}
