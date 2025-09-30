package QuickStart;

import java.util.ArrayList;
import java.util.List;

public class Mode {
    // Name of the mode (e.g., "School", "Gaming")
    private String name;
    
    // List of commands associated with this mode
    private List<CommandRunner> commands;

    // Constructor: initialize mode with a name and empty command list
    public Mode(String name) {
        this.name = name;
        this.commands = new ArrayList<>();
    }

    // Add a new command to this mode
    public void addCommand(CommandRunner cmd) {
        commands.add(cmd);
    }

    // Remove a command by name (case-insensitive), returns true if removed
    public boolean removeCommand(String name) {
        return commands.removeIf(c -> c.getCommand().equalsIgnoreCase(name));
    }

    // Return the list of all commands in this mode
    public List<CommandRunner> getCommands() {
        return commands;
    }

    // Return a string representation of the mode and its commands
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mode: ").append(name).append("\n");
        for (CommandRunner c : commands) {
            sb.append("  ").append(c.toString()).append("\n");
        }
        return sb.toString();
    }

    // Get the name of the mode
    public String getName() {
        return name;
    }
}
