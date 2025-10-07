package QuickStart;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LauncherGUI extends Application {

    private Launcher launcher;           // Your backend launcher
    private ComboBox<String> modeComboBox;
    private ListView<String> commandListView;
    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize Launcher with version
        launcher = new Launcher("1.0");

        // Sample mode for demonstration
        Mode sampleMode = new Mode("School");
        sampleMode.addCommand(new CommandRunner("https://chat.openai.com", "url"));
        launcher.addMode(sampleMode);

        // ---- GUI Controls ----
        modeComboBox = new ComboBox<>();
        modeComboBox.getItems().addAll(launcher.listModes());
        modeComboBox.getSelectionModel().selectFirst();

        commandListView = new ListView<>();
        updateCommandList();

        Button runModeButton = new Button("Run Mode");
        Button addCommandButton = new Button("Add Command");

        statusLabel = new Label("Ready");

        // ---- Layout ----
        HBox topBox = new HBox(10, new Label("Select Mode:"), modeComboBox, runModeButton, addCommandButton);
        VBox mainLayout = new VBox(10, topBox, commandListView, statusLabel);
        mainLayout.setPadding(new javafx.geometry.Insets(10));

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Startup Mode Launcher");
        primaryStage.show();

        // ---- Events ----

        // Event 1: Run Mode button
        runModeButton.setOnAction(e -> {
            String selectedMode = modeComboBox.getValue();
            launcher.runMode(selectedMode);
            updateCommandList();
            statusLabel.setText("Ran mode: " + selectedMode);
        });

        // Event 2: Add Command button
        addCommandButton.setOnAction(e -> {
            Mode currentMode = launcher.getMode(modeComboBox.getValue());
            if (currentMode == null) return;

            // Simple input dialog for demonstration
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Command");
            dialog.setHeaderText("Enter command path or URL");
            dialog.setContentText("Command:");

            dialog.showAndWait().ifPresent(input -> {
                // Add command as URL type for simplicity
                CommandRunner cmd = new CommandRunner(input, "url");
                currentMode.addCommand(cmd);
                updateCommandList();
                statusLabel.setText("Added command: " + input);
            });
        });

        // Event: update command list when mode changes
        modeComboBox.setOnAction(e -> updateCommandList());
    }

    // Helper method to refresh the ListView of commands
    private void updateCommandList() {
        String selectedMode = modeComboBox.getValue();
        Mode mode = launcher.getMode(selectedMode);
        commandListView.getItems().clear();
        if (mode != null) {
            for (CommandRunner cmd : mode.getCommands()) {
                commandListView.getItems().add(cmd.toString());
            }
        }
    }
}
