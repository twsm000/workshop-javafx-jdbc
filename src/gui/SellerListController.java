package gui;

import java.net.URL;
import java.util.Date;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

    private static final String FORM_PATH = "/gui/SellerForm.fxml";
    private SellerService service;
    private ObservableList<Seller> obsList;

    @FXML
    private TableView<Seller> tableViewSeller;

    @FXML
    private TableColumn<Seller, Integer> tableColumnId;

    @FXML
    private TableColumn<Seller, String> tableColumnName;

    @FXML
    private TableColumn<Seller, String> tableColumnEmail;
    
    @FXML
    private TableColumn<Seller, Date> tableColumnBirthDate;
    
    @FXML 
    private TableColumn<Seller, Double> tableColumnBaseSalary;
    
    @FXML
    private TableColumn<Seller, Seller> tableColumnEDIT;
    
    @FXML 
    private TableColumn<Seller, Seller> tableColumnREMOVE;

    @FXML
    private Button btnNew;

    public void setSellerService(SellerService service) {
        this.service = service;
    }

    @FXML
    public void onBtnNewAction(ActionEvent event) {
        Seller entity = new Seller();
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
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        Utils.formatTableColumnData(tableColumnBirthDate, "dd/MM/yyyy");
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView() {
        validateService();

        List<Seller> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewSeller.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }
    
    private void validateService() {
        if (Utils.isNull(service)) {
            throw new IllegalStateException("Service was not defined!");
        }        
    }

    private void createDialogForm(Seller entity, String absolutName, Stage parentStage) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
//            Pane pane = loader.load();
//
//            SellerFormController controller = loader.getController();
//            controller.setSeller(entity);
//            controller.setService(new SellerService());
//            controller.subscribeDataChangeListener(this);
//            controller.updateFormData();
//
//            Stage dialogStage = new Stage();
//            dialogStage.setTitle("Enter seller data");
//            dialogStage.setScene(new Scene(pane));
//            dialogStage.setResizable(false);
//            dialogStage.initOwner(parentStage);
//            dialogStage.initModality(Modality.WINDOW_MODAL);
//            dialogStage.showAndWait();
//        } catch (IOException e) {
//            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
//        }
    }

    @Override
    public void onDataChange() {
        updateTableView();
    }

    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button btn = new Button("EDIT");

            @Override
            protected void updateItem(Seller entity, boolean empty) {
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
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>(){
            private final Button btn = new Button("REMOVE");
            
            @Override
            protected void updateItem(Seller entity, boolean empty) {
                super.updateItem(entity, empty);
                
                if (Utils.isNull(entity)) {
                    setGraphic(null);
                    return;
                }
                
                setGraphic(btn);
                btn.setOnAction(event -> removeEntity(entity));
            }
        });
    }
    
    private void removeEntity(Seller entity) {
        String title = "DELETE CONFIRMATION";
        String content = "Are you sure to delete the seller [" + entity.getId() + "]?";        
        Optional<ButtonType> result = Alerts.showConfirmation(title, content);        
        if (result.get() != ButtonType.OK) {
            return;
        }
        
        validateService();
        try {
            service.remove(entity);
            updateTableView();
        } catch (DbException e) {
            Alerts.showAlert("Error removing seller", null, e.getMessage(), AlertType.ERROR);
        }
    }    
}
