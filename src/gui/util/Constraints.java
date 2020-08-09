package gui.util;

import javafx.scene.control.TextField;

public class Constraints {
    
    private static final String INTEGER_REGEX = "\\d*";
    private static final String DOUBLE_REGEX = "\\d*([\\.]\\d*)?";
    
    public static void setTextFieldInteger(TextField txt) {
        txt.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches(INTEGER_REGEX)) {
                txt.setText(oldValue);
            }
        });
    }
    
    public static void setTextFieldMaxLength(TextField txt, int max) {
        txt.textProperty().addListener((obs, oldValue, newValue) -> {
          if (newValue != null && newValue.length() > max) {
              txt.setText(oldValue);
          }
        });
    }

    public static void setTextFieldDouble(TextField txt) {
        txt.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches(DOUBLE_REGEX)) {
                txt.setText(oldValue);
            }
        });
    }
}
