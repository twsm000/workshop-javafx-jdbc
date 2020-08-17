package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

    private static final String DEFAULT_MESSAGE_EMPTY_FIELD = "Field can't be empty";

    private Department department;
    private DepartmentService service;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

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

    public void subscribeDataChangeListener(DataChangeListener listener) {
        this.dataChangeListeners.add(listener);
    }

    public void onButtonSaveAction(ActionEvent event) {
        validateDepartment();
        validateService();

        try {
            department = getFormData();
            service.saveOrUpdate(department);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        } catch (ValidationException e) {
            setErrorMessages(e.getErrors());
        } catch (DbException e) {
            Alerts.showAlert("Error saving department", null, e.getMessage(), AlertType.ERROR);
        }
    }

    private void validateDepartment() {
        if (Utils.isNull(department)) {
            throw new IllegalStateException("Department was not defined!");
        }
    }

    private void validateService() {
        if (Utils.isNull(service)) {
            throw new IllegalStateException("Service was not defined!");
        }
    }

    private Department getFormData() {
        validateData();

        Department department = new Department();
        department.setId(Utils.tryStrToInt(txtFieldId.getText()));
        department.setName(txtFieldName.getText());

        return department;
    }

    private void validateData() {
        ValidationException exception = new ValidationException("Validation error");
        if (Utils.isNullOrBlank(txtFieldName.getText())) {
            exception.addError("name", DEFAULT_MESSAGE_EMPTY_FIELD);
        }

        if (exception.hasErrors()) {
            throw exception;
        }
    }

    private void notifyDataChangeListeners() {
        for (DataChangeListener listener : dataChangeListeners) {
            listener.onDataChange();
        }
    }

    public void onButtonCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtFieldId);
        Constraints.setTextFieldMaxLength(txtFieldName, 30);
    }

    public void updateFormData() {
        validateDepartment();

        txtFieldId.setText(String.valueOf(department.getId()));
        txtFieldName.setText(department.getName());
    }

    public void setErrorMessages(Map<String, String> errors) {
        labelErrorName.setText(errors.get("name"));
    }
}
