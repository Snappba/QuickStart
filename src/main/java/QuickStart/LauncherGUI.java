package QuickStart;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;

public class LauncherGUI extends Application {

    private Launcher launcher;           // Your backend launcher
    private ComboBox<String> modeComboBox;
    private ListView<String> commandListView;
    private Label statusLabel;
    private final String configFilePath = "config/config.json"; // relative path to config file

    public static void main(String[] args) {
        launch(args);
    }

@Override
public void start(Stage primaryStage) {
    // Ensure config folder exists
    File configDir = new File("config");
    if (!configDir.exists()) {
        configDir.mkdirs();
    }

    // Attempt to load launcher from file
    launcher = Launcher.loadFromFile(configFilePath);

    // If loading failed, create a new Launcher
    if (launcher == null) {
        launcher = new Launcher("1.0");
    }

    // If no modes exist, add a sample/test mode
    if (launcher.listModes().isEmpty()) {
        Mode sampleMode = new Mode("Test");
        sampleMode.addCommand(new CommandRunner("https://chat.openai.com", "url"));
        launcher.addMode(sampleMode);
    }

    // ---- GUI Controls ----
    modeComboBox = new ComboBox<>();
    modeComboBox.getItems().addAll(launcher.listModes());
    modeComboBox.getSelectionModel().selectFirst();

    commandListView = new ListView<>();
    updateCommandList();

    Button runModeButton = new Button("Run Mode");
    Button addCommandButton = new Button("Add Command");
    Button removeCommandButton = new Button("Remove Command");
    Button addModeButton = new Button("Add Mode");
    Button removeModeButton = new Button("Remove Mode");
    CheckBox lightModeToggle = new CheckBox("Light Mode"); // toggle for light/dark

    statusLabel = new Label("Ready");

    // ---- Layout ----
    HBox topBox = new HBox(10, new Label("Select Mode:"), modeComboBox, addModeButton, removeModeButton,addCommandButton, removeCommandButton, lightModeToggle);
    HBox bottomBox = new HBox();
    VBox mainLayout = new VBox(10, topBox, commandListView, statusLabel, bottomBox);
    mainLayout.setPadding(new javafx.geometry.Insets(10));
    
    bottomBox.setSpacing(10);
    bottomBox.setPadding(new Insets(10));
    bottomBox.setAlignment(Pos.CENTER_RIGHT);
    bottomBox.getChildren().add(runModeButton);

    // Set initial style based on launcher preference
    if (launcher.isDarkModeEnabled()) {
        mainLayout.getStyleClass().add("dark");
        lightModeToggle.setSelected(false);
    } else {
        mainLayout.getStyleClass().add("light");
        lightModeToggle.setSelected(true);
    }

    Scene scene = new Scene(mainLayout, 700, 400);

    // ---- Load CSS ----
    URL cssResource = getClass().getResource("style.css");
    if (cssResource != null) {
        scene.getStylesheets().add(cssResource.toExternalForm());
    } else {
        System.err.println("ERROR: style.css not found!");
    }

    primaryStage.setScene(scene);
    primaryStage.setTitle("Startup Mode Launcher");
    primaryStage.show();

    // ---- Events ----
    
    // Add Mode button
    addModeButton.setOnAction(_ -> {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Mode");
        dialog.setHeaderText("Enter a name for the new mode");
        dialog.setContentText("Mode Name: ");

        dialog.showAndWait().ifPresent(name -> {
            if (name.trim().isEmpty()) {
                statusLabel.setText("Mode cannot be emtpy");;
                return;
            }

            if (launcher.getMode(name) !=null) {
                statusLabel.setText("Mode already Exists!");
            }

            Mode newMode = new Mode(name.trim());
            launcher.addMode(newMode);
            modeComboBox.getItems().add(name.trim());
            modeComboBox.getSelectionModel().select(name.trim());
            updateCommandList();
            statusLabel.setText("Added mode: " + name.trim());
            launcher.saveToFile(configFilePath); //always save immediately
        });
    });

    removeModeButton.setOnAction(_ -> {
        String selectedMode = modeComboBox.getValue();
        if (selectedMode == null) return;

        //prevent removal of the default mode
        if (selectedMode.equalsIgnoreCase("default")) {
            statusLabel.setText("Cannot remove the default mode!");
            return;
        }

        boolean removed = launcher.removeMode(selectedMode);
        if (removed) {
            modeComboBox.getItems().remove(selectedMode);

            //select first mode in the list if any remain (defaults)
            if (!modeComboBox.getItems().isEmpty()) {
                modeComboBox.getSelectionModel().selectFirst();
            }
            updateCommandList();
            statusLabel.setText("Removed mode: " + selectedMode);

            launcher.saveToFile(configFilePath); //save after removal
            } else {statusLabel.setText("Failed to remove mode: " + selectedMode);
        }
    });

    // Run Mode button
    runModeButton.setOnAction(_ -> {
        String selectedMode = modeComboBox.getValue();
        launcher.runMode(selectedMode);
        updateCommandList();
        statusLabel.setText("Ran mode: " + selectedMode);
    });

    // Add Command button
    addCommandButton.setOnAction(_ -> {
    Mode currentMode = launcher.getMode(modeComboBox.getValue());
    if (currentMode == null) return;

    // Custom dialog layout
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Add Command");
    dialog.setHeaderText("Enter URL or select a file (.exe)");

    // Buttons
    ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

    // URL TextField and Browse button
    TextField commandField = new TextField();
    commandField.setPromptText("Enter URL or choose a file");

    Button browseButton = new Button("Browse");
    browseButton.setOnAction(_ -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Executable File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Executable Files", "*.exe")
        );
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            commandField.setText(selectedFile.getAbsolutePath());
        }
    });

    HBox inputBox = new HBox(10, commandField, browseButton);
    dialog.getDialogPane().setContent(inputBox);

    // Convert result to string when Add pressed
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == addButtonType) {
            return commandField.getText().trim();
        }
        return null;
    });

    dialog.showAndWait().ifPresent(input -> {
        if (input.isEmpty()) {
            statusLabel.setText("Cannot add empty command.");
            return;
        }

        // Determine type: url or file
        String type = input.endsWith(".exe") ? "app" : "url";
        CommandRunner cmd = new CommandRunner(input, type);
        currentMode.addCommand(cmd);
        updateCommandList();
        statusLabel.setText("Added command: " + input);
        launcher.saveToFile(configFilePath);
    });
});

    // Remove Command Button
    removeCommandButton.setOnAction(_ -> {
        Mode currentMode = launcher.getMode(modeComboBox.getValue());
        if (currentMode == null) return; 

        String selectedCommand = commandListView.getSelectionModel().getSelectedItem();
        if (selectedCommand == null) {
            statusLabel.setText("No command selected to remove");
            return;
        }

        //find and remove the selected commmand from the mode
        boolean removed = currentMode.getCommands().removeIf(cmd -> cmd.toString().equals(selectedCommand));

        if (removed) {
            updateCommandList();
            statusLabel.setText("Removed command: " + selectedCommand);
            launcher.saveToFile(configFilePath); //save the change
        }
    });

    // Update command list when mode changes
    modeComboBox.setOnAction(_ -> updateCommandList());

    // Light/Dark mode toggle
    lightModeToggle.setOnAction(_ -> {
        if (lightModeToggle.isSelected()) {
            mainLayout.getStyleClass().remove("dark");
            mainLayout.getStyleClass().add("light");
            launcher.setDarkModeEnabled(false);
        } else {
            mainLayout.getStyleClass().remove("light");
            mainLayout.getStyleClass().add("dark");
            launcher.setDarkModeEnabled(true);
        }

        // Save preference immediately
        launcher.saveToFile(configFilePath);
    });

    // Save launcher on exit
    primaryStage.setOnCloseRequest(_ -> launcher.saveToFile(configFilePath));
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
