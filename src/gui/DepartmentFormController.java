package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {

    private Department entity;
    
    @FXML
    private TextField txtFieldId;
    
    @FXML
    private TextField txtFieldName;
    
    @FXML
    private Label labelErrorName;
    
    @FXML 
    private Button btbSave;

    @FXML 
    private Button btbCancel;
    
    public void setDepartment(Department entity) {
        this.entity = entity;
    }
    
    public void onButtonSaveAction() {
        System.out.println("onButtonSaveAction...");
    }
    
    public void onButtonCancelAction() {
        System.out.println("onButtonCancelAction...");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.initializeNodes();
    }
    
    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtFieldId);
        Constraints.setTextFieldMaxLength(txtFieldName, 30);
    }
    
    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Entity was not defined!");
        }
    
        txtFieldId.setText(String.valueOf(entity.getId()));
        txtFieldName.setText(entity.getName());
    }
}
