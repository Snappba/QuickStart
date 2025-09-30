package QuickStart;

public class LauncherTest {
    public static void main(String[] args) {
        // Create a Launcher instance with version "1.0"
        Launcher launcher = new Launcher("1.0");

        // Create a mode called "School" and add a URL command
        Mode school = new Mode("School");
        school.addCommand(new CommandRunner("https://chat.openai.com", "url"));

        // Add the mode to the launcher
        launcher.addMode(school);

        // Print all available mode names
        System.out.println("Available modes: " + launcher.listModes());

        // Run the "School" mode (opens all its commands)
        launcher.runMode("School");

        // Print the currently selected mode
        System.out.println("Current mode: " + launcher.getCurrentMode().getName());
    }
}
