package sample.test.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.test.helpers.ColumnDefinition;

import java.util.List;

public class TableUtils {

    public static <T> void setTableColumns(TableView<T> tableView, List<ColumnDefinition<T, ?>> columnDefinitions) {
        tableView.getColumns().clear();
        for (ColumnDefinition<T, ?> def : columnDefinitions) {
            TableColumn<T, ?> column = new TableColumn<>(def.getTitle());
            column.setCellValueFactory(new PropertyValueFactory<>(def.getProperty()));
            tableView.getColumns().add(column);
        }

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public static <T> void populateTableView(TableView<T> tableView, List<T> data) {
        if (data != null) {
            ObservableList<T> observableData = FXCollections.observableArrayList(data);
            tableView.setItems(observableData);
        } else {
            System.out.println("No data found or failed to load.");
        }
    }

    public static <T> void setTableListener(TableView<T> tableView, ChangeListener<T> listener) {
        tableView.getSelectionModel().selectedItemProperty().addListener(listener);
    }
}
