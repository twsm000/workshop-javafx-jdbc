package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {

    private Seller entity;
    private SellerService service;
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

    public void setSeller(Seller entity) {
        this.entity = entity;
    }

    public void setService(SellerService service) {
        this.service = service;
    }
    
    public void subscribeDataChangeListener(DataChangeListener listener) {
        this.dataChangeListeners.add(listener);
    }

    public void onButtonSaveAction(ActionEvent event) {
        this.validateSeller();
        this.validateService();
        
        try {
            entity = getFormData();
            service.saveOrUpdate(entity);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        }
        catch (ValidationException e) {
            setErrorMessages(e.getErrors());
        }
        catch (DbException e) {
            Alerts.showAlert("Error saving seller", null, e.getMessage(), AlertType.ERROR);
        }
    }

    private void validateSeller() {
        if (Utils.isNull(entity)) {
            throw new IllegalStateException("Seller was not defined!");
        }        
    }
    
    private void validateService() {
        if (Utils.isNull(service)) {
            throw new IllegalStateException("Service was not defined!");
        }
    }

    private Seller getFormData() {
        this.validateData();

        Seller entity = new Seller();        
        entity.setId(Utils.tryStrToInt(txtFieldId.getText()));        
        entity.setName(txtFieldName.getText());
        return entity;
    }
    
    private void validateData() {
        ValidationException exception = new ValidationException("Validation error");
        if (Utils.IsNullOrBlank(txtFieldName.getText())) {
            exception.addError("Name", "Field can't be empty");
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
        this.validateSeller();
        txtFieldId.setText(String.valueOf(entity.getId()));
        txtFieldName.setText(entity.getName());
    }
    
    public void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();
        
        if (fields.contains("Name")) {
            labelErrorName.setText(errors.get("Name"));
        }
    }
}
