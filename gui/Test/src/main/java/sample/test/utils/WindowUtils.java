package sample.test.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import sample.test.Main;

import java.io.IOException;
import java.util.function.Consumer;

public class WindowUtils {

    /**
     * Loads a view into a new Stage, with options for modals and owners.
     *
     * @param viewName The FXML file name for the view.
     * @param title The title of the new window.
     * @param isModal Whether the window should be modal.
     * @param owner The owner window (if any).
     * @param blockCaller Whether to block the caller (true for showAndWait, false for show).
     * @throws IOException If the FXML file cannot be loaded.
     */
    public static void loadView(String viewName, String title, boolean isModal, Window owner, boolean blockCaller) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(viewName));
        Parent viewRoot = loader.load();

        Stage viewStage = new Stage();
        viewStage.setTitle(title);
        viewStage.setScene(new Scene(viewRoot));

        if (isModal) {
            viewStage.initModality(Modality.WINDOW_MODAL);
            if (owner != null) {
                viewStage.initOwner(owner);
            }
        }

        if (blockCaller) {
            viewStage.showAndWait();
        } else {
            viewStage.show();
        }
    }

    public static <T> void loadViewWithControllerAndData(String viewName, String title, boolean isModal, Window owner, boolean blockCaller, Consumer<T> controllerConsumer) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(viewName));
        Parent viewRoot = loader.load();

        T controller = loader.getController();

        controllerConsumer.accept(controller);

        Stage viewStage = new Stage();
        viewStage.setTitle(title);
        viewStage.setScene(new Scene(viewRoot));

        if (isModal) {
            viewStage.initModality(Modality.WINDOW_MODAL);
            if (owner != null) {
                viewStage.initOwner(owner);
            }
        }

        if (blockCaller) {
            viewStage.showAndWait();
        } else {
            viewStage.show();
        }
    }
}
