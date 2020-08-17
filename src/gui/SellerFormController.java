package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

    private Seller entity;
    private SellerService service;
    private DepartmentService departmentService;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField txtFieldId;

    @FXML
    private TextField txtFieldName;

    @FXML
    private TextField txtFieldEmail;

    @FXML
    private DatePicker dtpFieldBirthDate;

    @FXML
    private TextField txtFieldBaseSalary;

    @FXML
    private ComboBox<Department> cbxDepartment;
    private ObservableList<Department> obsListDepartment;

    @FXML
    private Label labelErrorName;

    @FXML
    private Label labelErrorEmail;

    @FXML
    private Label labelErrorBirthDate;

    @FXML
    private Label labelErrorBaseSalary;

    @FXML
    private Button btbSave;

    @FXML
    private Button btbCancel;

    public void setSeller(Seller entity) {
        this.entity = entity;
    }

    public void setServices(SellerService service, DepartmentService departmentService) {
        this.service = service;
        this.departmentService = departmentService;
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        this.dataChangeListeners.add(listener);
    }

    public void onButtonSaveAction(ActionEvent event) {
        this.validateSeller();
        this.validateServices();

        try {
            entity = getFormData();
            service.saveOrUpdate(entity);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        } catch (ValidationException e) {
            setErrorMessages(e.getErrors());
        } catch (DbException e) {
            Alerts.showAlert("Error saving seller", null, e.getMessage(), AlertType.ERROR);
        }
    }

    private void validateSeller() {
        if (Utils.isNull(entity)) {
            throw new IllegalStateException("Seller was not defined!");
        }
    }

    private void validateServices() {
        if (Utils.isNull(service)) {
            throw new IllegalStateException("Service was not defined!");
        } else if (Utils.isNull(departmentService)) {
            throw new IllegalStateException("Department was not defined!");
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
        Constraints.setTextFieldDouble(txtFieldBaseSalary);
        Constraints.setTextFieldMaxLength(txtFieldEmail, 60);
        Utils.formatDatePicker(dtpFieldBirthDate, "dd/MM/yyyy");
        initializeComboBoxDepartment();
    }

    public void updateFormData() {
        this.validateSeller();
        txtFieldId.setText(String.valueOf(entity.getId()));
        txtFieldName.setText(entity.getName());
        txtFieldEmail.setText(entity.getEmail());
        if (!Utils.isNull(entity.getBirthDate())) {
            dtpFieldBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
        }
        Locale.setDefault(Locale.US);
        txtFieldBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

        if (Utils.isNull(entity.getDepartment())) {
            cbxDepartment.getSelectionModel().selectFirst();
        } else {
            cbxDepartment.setValue(entity.getDepartment());
        }
    }

    public void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();

        if (fields.contains("Name")) {
            labelErrorName.setText(errors.get("Name"));
        }
    }

    public void loadAssociatedObjects() {
        this.validateServices();
        List<Department> list = departmentService.findAll();
        obsListDepartment = FXCollections.observableArrayList(list);
        cbxDepartment.setItems(obsListDepartment);
    }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };

        cbxDepartment.setCellFactory(factory);
        cbxDepartment.setButtonCell(factory.call(null));
    }
}
