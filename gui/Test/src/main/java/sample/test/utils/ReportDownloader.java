package sample.test.utils;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.scene.layout.Pane;
import sample.test.service.JwtTokenService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;

public class ReportDownloader {
    private static final String BASE_URL = "http://localhost:8080/api/reports";
    private final AnchorPane parentPane;

    public ReportDownloader(AnchorPane parentPane) {
        this.parentPane = parentPane;
    }


    /**
     * Handles the report download action based on the button clicked.
     * This method retrieves the report type from the clicked button,
     * validates it, constructs the appropriate URL, and sends an HTTP
     * GET request to download the report. It handles errors and
     * manages the response accordingly.
     *
     * @param event The action event from the button click
     */
    public void downloadReport(ActionEvent event) {
        String reportType = getReportType((Button) event.getSource());
        if (isTypeValid(reportType)) return;

        String endPointUrl = createUrl(reportType);
        String token = getJwtToken();

        try {
            HttpResponse<String> response = HttpUtils.sendHttpRequest(endPointUrl, "GET", token, null);
            handleResponse(response);
        } catch (Exception e) {
            handleDownloadError(e);
        }
    }

    /**
     * Processes the HTTP response received from the report download request.
     * If the response indicates success, it saves the report to a file.
     * Otherwise, it displays an error message indicating the failure.
     *
     * @param response The HTTP response object received from the server
     * @throws IOException If an I/O error occurs while saving the report
     */
    private void handleResponse(HttpResponse<String> response) throws IOException {
        if (HttpUtils.checkIfResponseWasGood(response)) {
            saveReportToFile(response.body());
        } else {
            showError("Download Failed",
                    String.format("Server returned status code: %d", response.statusCode()));
        }
    }

    /**
     * Retrieves the JSON Web Token (JWT) used for authorization in the HTTP request.
     *
     * @return The JWT token as a String
     */
    private static String getJwtToken() {
        return JwtTokenService.getInstance().getJwtToken();
    }

    /**
     * Constructs the endpoint URL for downloading the specified report type.
     *
     * @param reportType The type of report to be downloaded
     * @return The constructed URL as a String
     */
    private static String createUrl(String reportType) {
        return String.format("%s/%s/json", BASE_URL, reportType);
    }

    /**
     * Validates the report type to ensure it is not null.
     * Displays an error message if the report type is invalid.
     *
     * @param reportType The report type to validate
     * @return True if the report type is invalid, false otherwise
     */
    private boolean isTypeValid(String reportType) {
        if (reportType == null) {
            showError("Invalid Report Type", "Could not determine the report type.");
            return true;
        }
        return false;
    }

    /**
     * Determines the report type based on the button ID
     * @param button The clicked button
     * @return The report type or null if invalid
     */
    private String getReportType(Button button) {
        return switch (button.getId()) {
            case "dailyButton" -> "daily";
            case "weeklyButton" -> "weekly";
            default -> null;
        };
    }

    /**
     * Saves the JSON report to a file selected by the user
     * @param jsonResponse The JSON content to save
     */
    private void saveReportToFile(String jsonResponse) {
        File file = saveFileDialog();
        saveIfFileCreatedSuccessfully(jsonResponse, file);
    }

    private void saveIfFileCreatedSuccessfully(String jsonResponse, File file) {
        if (file != null) {
            file = ensureJsonExtension(file);
            writeToFile(file, jsonResponse);
        } else {
            showError("Save Error", "Failed to save the report: No file selected");
        }
    }

    private File saveFileDialog() {
        FileChooser fileChooser = createFileChooser();
        return fileChooser.showSaveDialog(parentPane.getScene().getWindow());
    }

    /**
     * Creates and configures the FileChooser
     * @return Configured FileChooser instance
     */
    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        return fileChooser;
    }

    /**
     * Ensures the file has a .json extension
     * @param file The file to check
     * @return File with .json extension
     */
    private File ensureJsonExtension(File file) {
        if (!file.getName().endsWith(".json")) {
            return new File(file.getAbsolutePath() + ".json");
        }
        return file;
    }

    /**
     * Writes the JSON content to the specified file
     * @param file The file to write to
     * @param content The JSON content
     */
    private void writeToFile(File file, String content) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = content.getBytes();
            fileOutputStream.write(buffer);
            showSuccess("Report saved successfully to:\n" + file.getAbsolutePath());
        } catch (IOException e) {
            showError("Save Error", "Failed to save the report: " + e.getMessage());
        }
    }

    /**
     * Handles errors during the download process
     * @param e The exception that occurred
     */
    private void handleDownloadError(Exception e) {
        showError("Download Error", "Failed to download the report: " + e.getMessage());
    }

    /**
     * Shows an error alert to the user
     * @param title The error title
     * @param content The error message
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows a success alert to the user
     * @param message The success message
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Report Downloaded");
        alert.setContentText(message);
        alert.showAndWait();
    }
}