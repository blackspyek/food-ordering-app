package sample.test.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.test.helpers.ColumnDefinition;
import sample.test.model.MenuItem;
import sample.test.model.OrderItem;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TableUtils {

    public static <T> void initTable(
            TableView<T> tableView,
            List<T> data,
            Consumer<T> selectionConsumer,
            List<ColumnDefinition<T, ?>> columnDefinitions,
            BiConsumer<TableView<T>, List<T>> customInitializer) {

        tableView.getColumns().clear();

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setColumns(tableView, columnDefinitions);
        populateTable(tableView, data);

        if (selectionConsumer != null) {
            setListener(tableView, selectionConsumer);
        }

        if (customInitializer != null) {
            customInitializer.accept(tableView, data);
        }
    }

    public static <T> void setListener(TableView<T> tableView, Consumer<T> consumer) {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                consumer.accept(newSelection);
            }
        });
    }

    public static <T> void populateTable(TableView<T> tableView, List<T> data) {
        ObservableList<T> observableList = FXCollections.observableArrayList(data);
        tableView.setItems(observableList);
    }

    public static <T> void setColumns(TableView<T> tableView, List<ColumnDefinition<T, ?>> columnDefinitions) {
        for (ColumnDefinition<T, ?> columnDefinition : columnDefinitions) {
            TableColumn<T, Object> tableColumn = new TableColumn<>(columnDefinition.getTitle());
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(columnDefinition.getProperty()));
            tableView.getColumns().add(tableColumn);
        }
    }


}
