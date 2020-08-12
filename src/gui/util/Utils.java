package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

    public static Stage currentStage(ActionEvent event) {
        Node node = (Node) event.getSource();
        return (Stage) node.getScene().getWindow();
    }

    public static Integer tryStrToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public static boolean isNull(Object obj) {
        return obj == null;
    }        

    public static boolean IsNullOrBlank(String value) {
        return isNull(value) || value.isBlank();
    }
    
    public static boolean IsNullOrEmpty(String value) {
        return isNull(value) || value.isEmpty();
    }

}
