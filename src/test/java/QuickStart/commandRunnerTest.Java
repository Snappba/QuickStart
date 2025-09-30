package QuickStart;

public class CommandRunnerTest {
    public static void main(String[] args) {
        // Create a command for a URL
        CommandRunner urlCmd = new CommandRunner("https://chat.openai.com", "url");

        // Validate the URL command (should return true)
        System.out.println("URL validation: " + urlCmd.validate());

        // Create a command for a fake app path
        CommandRunner fakeApp = new CommandRunner("C:\\fakepath\\app.exe", "app");

        // Validate the fake app command (should return false)
        System.out.println("Fake app validation: " + fakeApp.validate());

        // Execute the URL command (opens browser)
        System.out.println("URL execution result: " + urlCmd.execute());
    }
}
