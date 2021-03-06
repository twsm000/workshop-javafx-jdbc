package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

    private static final String FORM_PATH = "/gui/DepartmentForm.fxml";
    private DepartmentService service;
    private ObservableList<Department> obsList;

    @FXML
    private TableView<Department> tableViewDepartment;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;

    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private TableColumn<Department, Department> tableColumnEDIT;
    
    @FXML 
    private TableColumn<Department, Department> tableColumnREMOVE;

    @FXML
    private Button btnNew;

    public void setDepartmentService(DepartmentService service) {
        this.service = service;
    }

    @FXML
    public void onBtnNewAction(ActionEvent event) {
        Department entity = new Department();
        Stage stage = Utils.currentStage(event);
        createDialogForm(entity, FORM_PATH, stage);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView() {
        validateService();

        List<Department> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewDepartment.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }
    
    private void validateService() {
        if (Utils.isNull(service)) {
            throw new IllegalStateException("Service was not defined!");
        }        
    }

    private void createDialogForm(Department entity, String absolutName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
            Pane pane = loader.load();

            DepartmentFormController controller = loader.getController();
            controller.setDepartment(entity);
            controller.setService(new DepartmentService());
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter department data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
    }

    @Override
    public void onDataChange() {
        updateTableView();
    }

    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button btn = new Button("EDIT");

            @Override
            protected void updateItem(Department entity, boolean empty) {
                super.updateItem(entity, empty);

                if (Utils.isNull(entity)) {
                    setGraphic(null);
                    return;
                }

                setGraphic(btn);
                btn.setOnAction(event -> createDialogForm(entity, FORM_PATH, Utils.currentStage(event)));
            }
        });
    }
    
    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>(){
            private final Button btn = new Button("REMOVE");
            
            @Override
            protected void updateItem(Department department, boolean empty) {
                super.updateItem(department, empty);
                
                if (Utils.isNull(department)) {
                    setGraphic(null);
                    return;
                }
                
                setGraphic(btn);
                btn.setOnAction(event -> removeEntity(department));
            }
        });
    }
    
    private void removeEntity(Department entity) {
        String title = "DELETE CONFIRMATION";
        String content = "Are you sure to delete the department [" + entity.getId() + "]?";        
        Optional<ButtonType> result = Alerts.showConfirmation(title, content);        
        if (result.get() != ButtonType.OK) {
            return;
        }
        
        validateService();
        try {
            service.remove(entity);
            updateTableView();
        } catch (DbException e) {
            Alerts.showAlert("Error removing department", null, e.getMessage(), AlertType.ERROR);
        }
    }    
}
