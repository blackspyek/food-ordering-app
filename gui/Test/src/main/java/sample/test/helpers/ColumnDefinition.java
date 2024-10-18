package sample.test.helpers;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
public class ColumnDefinition<T, V> {
    private final String title;
    private final String property;

}
