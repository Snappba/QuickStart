package QuickStart;

import java.io.File;

public class CommandRunner {
    // The command to run (either a URL or the path to an executable)
    private String command;
    
    // The type of command: "app" for executables, "url" for web links
    private String type;
    
    // Tracks whether the last execution was successful
    private boolean executionResult;

    // Constructor: initialize the command and its type
    public CommandRunner(String command, String type) {
        this.command = command;
        this.type = type.toLowerCase();
        this.executionResult = false; // default to false until executed
    }

    // Validate the command: check if it's a valid URL or existing file
    public boolean validate() {
        if (type.equals("url")) {
            // URL is valid if it starts with http:// or https://
            executionResult = command.startsWith("http://") || command.startsWith("https://");
        } else if (type.equals("app")) {
            // App is valid if the file exists and is a file (not a directory)
            File f = new File(command);
            executionResult = f.exists() && f.isFile();
        } else {
            // Invalid type
            executionResult = false;
        }
        return executionResult;
    }

    // Execute the command (either open the URL or launch the app)
    public boolean execute() {
        if (!validate()) return false; // don't execute invalid commands

        try {
            if (type.equals("url")) {
                // Open the URL in the default browser
                java.awt.Desktop.getDesktop().browse(new java.net.URI(command));
            } else if (type.equals("app")) {
                // Launch the application using ProcessBuilder
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.start();
            } else {
                return false; // unknown type
            }
            executionResult = true; // mark execution as successful
        } catch (Exception e) {
            executionResult = false; // mark execution as failed
            e.printStackTrace();
        }
        return executionResult;
    }

    // Getter for executionResult
    public boolean getExecutionResult() {
        return executionResult;
    }

    // Human-readable representation of the command
    @Override
    public String toString() {
        return command + " (" + type + ")";
    }

    // Getter for command string
    public String getCommand() { return command; }

    // Getter for type string
    public String getType() { return type; }
}
