package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

    private Department department;
    private DepartmentService service;

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

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setService(DepartmentService service) {
        this.service = service;
    }

    public void onButtonSaveAction(ActionEvent event) {
        this.validateDepartment();
        this.validateService();
        
        try {
            department = this.getFormData();
            service.saveOrUpdate(department);
            Utils.currentStage(event).close();
        } catch (DbException e) {
            Alerts.showAlert("Error saving department", null, e.getMessage(), AlertType.ERROR);
        }
    }
    
    private void validateDepartment() {
        if (department == null) {
            throw new IllegalStateException("Department was not defined!");
        }        
    }
    
    private void validateService() {
        if (service == null) {
            throw new IllegalStateException("Service was not defined!");
        }
    }

    private Department getFormData() {
        Department department = new Department();
        department.setId(Utils.tryStrToInt(txtFieldId.getText()));
        department.setName(txtFieldName.getText());
        return department;
    }

    public void onButtonCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
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
        this.validateDepartment();
        txtFieldId.setText(String.valueOf(department.getId()));
        txtFieldName.setText(department.getName());
    }
}
