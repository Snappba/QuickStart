package QuickStart;

import java.io.File;

public class CommandRunner {
    private String command;
    private String type;
    private boolean executionResult;

    public CommandRunner(String command, String type) {
        this.command = command;
        this.type = type.toLowerCase();
        this.executionResult = false;
    }

    public boolean validate() {
        System.out.println("[DEBUG] validate() called for type='" + type + "' command='" + command + "'");
        if (type.equals("url")) {
            executionResult = command.startsWith("http://") || command.startsWith("https://");
        } else if (type.equals("app")) {
            File f = new File(command);
            System.out.println("check file path: " + f.getAbsolutePath());
            System.out.println("file exists" + f.exists());
            System.out.println("is file" + f.isFile());
            executionResult = f.exists() && f.isFile();
        } else {
            executionResult = false;
        }
        return executionResult;
    }

    public boolean execute() {
        System.out.println("[DEBUG] Executing command: " + command + " (type: " + type + ")"); // DEBUG

        if (!validate()) {
            System.err.println("[DEBUG] Validation failed for: " + command); // DEBUG
            return false;
        }

        try {
            if (type.equals("url")) {
                System.out.println("[DEBUG] Launching URL: " + command); // DEBUG
                java.awt.Desktop.getDesktop().browse(new java.net.URI(command));
            } else if (type.equals("app")) {
                File exeFile = new File(command);
                if (!exeFile.exists()) {
                    System.err.println("[DEBUG] Executable not found: " + exeFile.getAbsolutePath()); // DEBUG
                    return false;
                }

                System.out.println("[DEBUG] Starting executable: " + exeFile.getAbsolutePath()); // DEBUG
                ProcessBuilder pb = new ProcessBuilder(exeFile.getAbsolutePath());
                pb.directory(exeFile.getParentFile());
                pb.inheritIO();
                Process process = pb.start();

                System.out.println("[DEBUG] Process started with PID (approx): " + process.pid()); // DEBUG
            } else {
                System.err.println("[DEBUG] Unknown command type: " + type); // DEBUG
                return false;
            }

            executionResult = true;
        } catch (Exception e) {
            executionResult = false;
            System.err.println("[DEBUG] Exception while executing command:");
            e.printStackTrace();
        }

        return executionResult;
    }

    @Override
    public String toString() {
        return command + " (" + type + ")";
    }

    public boolean getExecutionResult() { return executionResult; }
    public String getCommand() { return command; }
    public String getType() { return type; }
}
