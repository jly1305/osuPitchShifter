import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import java.io.File;
import java.io.IOException;

public class OsuPitchShifter extends Application {

    private Label statusLabel = new Label("Select a file to get started!");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create UI elements
        Button uploadButton = new Button("Select a file");
        Button daycoreButton = new Button("Daycore");
        Button nightcoreButton = new Button("Nightcore");

        // Disable action buttons until a file is selected
        daycoreButton.setDisable(true);
        nightcoreButton.setDisable(true);

        // Set up FileChooser for file selection
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.aac", "*.flac"));

        final File[] selectedFile = {null};

        // Upload button action
        uploadButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile[0] = file;
                statusLabel.setText("Selected file: " + file.getName());
                daycoreButton.setDisable(false);
                nightcoreButton.setDisable(false);
            } else {
                statusLabel.setText("No file selected.");
            }
        });

        // Daycore button action
        daycoreButton.setOnAction(e -> {
            if (selectedFile[0] != null) {
                processFile(selectedFile[0], "daycore");
            }
        });

        // Nightcore button action
        nightcoreButton.setOnAction(e -> {
            if (selectedFile[0] != null) {
                processFile(selectedFile[0], "nightcore");
            }
        });

        // Layout setup
        HBox row1 = new HBox(10, uploadButton);
        row1.setAlignment(Pos.CENTER);

        HBox row2 = new HBox(10, daycoreButton, nightcoreButton);
        row2.setAlignment(Pos.CENTER);

        GridPane layout = new GridPane();
        layout.setAlignment(Pos.CENTER);
        layout.setVgap(15);
        layout.add(row1, 0, 0);
        layout.add(row2, 0, 1);
        layout.add(statusLabel, 0, 2);

        // Scene setup
        Scene scene = new Scene(layout, 400, 200);
        primaryStage.setTitle("osu! Pitch Shifter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to process audio file using FFmpeg
    private void processFile(File file, String effect) {
        String pitchFactor = effect.equals("daycore") ? "0.8" : "1.25";
        String inputPath = file.getAbsolutePath();
        String outputPath = file.getParent() + "/processed_" + file.getName();

        // FFmpeg command to adjust pitch without changing speed
        String command = String.format(
            "ffmpeg -i \"%s\" -af \"rubberband=pitch=%s\" \"%s\"",
            inputPath, pitchFactor, outputPath
        );

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            Process process = processBuilder.start();
            process.waitFor();

            if (process.exitValue() == 0) {
                statusLabel.setText("File processed successfully: " + outputPath);
            } else {
                statusLabel.setText("Error processing file.");
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            statusLabel.setText("An error occurred: " + ex.getMessage());
        }
    }
}
