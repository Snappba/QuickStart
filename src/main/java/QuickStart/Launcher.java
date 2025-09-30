package QuickStart;

import java.util.ArrayList;
import java.util.List;

public class Launcher {
    // List of all available modes
    private List<Mode> modes;
    
    // The currently selected mode
    private Mode currentMode;
    
    // Version of the application
    private String appVersion;

    // Constructor: initialize the launcher with a version and empty mode list
    public Launcher(String version) {
        this.appVersion = version;
        this.modes = new ArrayList<>();
        this.currentMode = null;
    }

    // Add a new mode to the list of modes
    public void addMode(Mode mode) {
        modes.add(mode);
    }

    // Remove a mode by name (case-insensitive), returns true if removed
    public boolean removeMode(String name) {
        return modes.removeIf(m -> m.getName().equalsIgnoreCase(name));
    }

    // Get a mode by name, returns null if not found
    public Mode getMode(String name) {
        for (Mode m : modes) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    // Run all commands in the specified mode
    public void runMode(String name) {
        Mode m = getMode(name);
        if (m == null) return; // Do nothing if mode not found
        currentMode = m; // Set as current mode
        for (CommandRunner c : m.getCommands()) {
            c.execute(); // Execute each command in the mode
        }
    }

    // Return a list of all mode names
    public List<String> listModes() {
        List<String> list = new ArrayList<>();
        for (Mode m : modes) list.add(m.getName());
        return list;
    }

    // Get the application version
    public String getAppVersion() {
        return appVersion;
    }

    // Get the currently selected mode
    public Mode getCurrentMode() {
        return currentMode;
    }
}
