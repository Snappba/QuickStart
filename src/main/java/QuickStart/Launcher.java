package QuickStart;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Launcher {
    // List of all available modes
    private List<Mode> modes;
    
    // The currently selected mode
    private Mode currentMode;
    
    // Version of the application
    private String appVersion;

    // Dark mode preference
    private boolean darkModeEnabled = true; 

    // Constructor: initialize the launcher with a version and empty mode list
    public Launcher(String version) {
        this.appVersion = version;
        this.modes = new ArrayList<>();
        this.currentMode = null;
    }

    // Getter and setter for dark mode
    public boolean isDarkModeEnabled() { return darkModeEnabled; }
    public void setDarkModeEnabled(boolean enabled) { this.darkModeEnabled = enabled; }

    // ---- File handling ----
    public void saveToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, writer); // save the whole Launcher object
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Launcher loadFromFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Launcher.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Launcher("1.0"); // fallback if reading fails
        }
    }

    // ---- Mode management ----
    public void addMode(Mode mode) {
        modes.add(mode);
    }

    public boolean removeMode(String name) {
        return modes.removeIf(m -> m.getName().equalsIgnoreCase(name));
    }

    public Mode getMode(String name) {
        for (Mode m : modes) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    public void runMode(String name) {
        Mode m = getMode(name);
        if (m == null) return;
        currentMode = m;
        for (CommandRunner c : m.getCommands()) {
            c.execute();
        }
    }

    public List<String> listModes() {
        List<String> list = new ArrayList<>();
        for (Mode m : modes) list.add(m.getName());
        return list;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public Mode getCurrentMode() {
        return currentMode;
    }
}
