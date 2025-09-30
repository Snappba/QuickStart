package QuickStart;

public class ModeTest {
    public static void main(String[] args) {
        // Create a mode called "School"
        Mode school = new Mode("School");

        // Add commands to the mode
        school.addCommand(new CommandRunner("https://chat.openai.com", "url"));
        school.addCommand(new CommandRunner("C:\\Program Files\\Spotify\\Spotify.exe", "app"));

        // Print all commands in the mode
        System.out.println("Commands in mode:\n" + school.toString());

        // Remove one command and print again
        school.removeCommand("https://chat.openai.com");
        System.out.println("After removal:\n" + school.toString());
    }
}
