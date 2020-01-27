package windows;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

class HBoxCell extends HBox {

    HBoxCell(String labelText, Button button) {
        super();
        Label label = new Label();

        label.setText(labelText);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        this.getChildren().addAll(label, button);
    }
}
