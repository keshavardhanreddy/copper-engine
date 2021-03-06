/*
 * Copyright 2002-2014 SCOOP Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.copperengine.monitoring.client.ui.workflowinstance.result;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DateStringConverter;

import org.copperengine.monitoring.client.adapter.GuiCopperDataProvider;
import org.copperengine.monitoring.client.form.FxmlForm;
import org.copperengine.monitoring.client.form.filter.FilterAbleForm;
import org.copperengine.monitoring.client.form.filter.FilterResultController;
import org.copperengine.monitoring.client.form.filter.FilterResultControllerBase;
import org.copperengine.monitoring.client.form.issuereporting.IssueReporter;
import org.copperengine.monitoring.client.ui.audittrail.filter.AuditTrailFilterModel;
import org.copperengine.monitoring.client.ui.audittrail.result.AuditTrailResultModel;
import org.copperengine.monitoring.client.ui.workflowinstance.filter.WorkflowInstanceFilterModel;
import org.copperengine.monitoring.client.ui.workflowinstancedetail.filter.WorkflowInstanceDetailFilterModel;
import org.copperengine.monitoring.client.ui.workflowinstancedetail.result.WorkflowInstanceDetailResultModel;
import org.copperengine.monitoring.client.util.TableColumnHelper;
import org.copperengine.monitoring.core.model.WorkflowInstanceState;

public class WorkflowInstanceResultController extends FilterResultControllerBase<WorkflowInstanceFilterModel, WorkflowInstanceResultModel> implements
        Initializable {

    private final GuiCopperDataProvider copperDataProvider;
    private final WorkflowInstanceDependencyFactory navigation;
    private final IssueReporter issueReporter;

    public WorkflowInstanceResultController(GuiCopperDataProvider copperDataProvider, WorkflowInstanceDependencyFactory navigation, IssueReporter issueReporter) {
        super();
        this.copperDataProvider = copperDataProvider;
        this.navigation = navigation;
        this.issueReporter = issueReporter;
    }

    @FXML
    // fx:id="idColumn"
    private TableColumn<WorkflowInstanceResultModel, String> idColumn; // Value injected by FXMLLoader

    @FXML
    // fx:id="prioritynColumn"
    private TableColumn<WorkflowInstanceResultModel, String> prioritynColumn; // Value injected by FXMLLoader

    @FXML
    // fx:id="processorPoolColumn"
    private TableColumn<WorkflowInstanceResultModel, String> processorPoolColumn; // Value injected by FXMLLoader

    @FXML
    // fx:id="resultTable"
    private TableView<WorkflowInstanceResultModel> resultTable; // Value injected by FXMLLoader

    @FXML
    // fx:id="stateColumn"
    private TableColumn<WorkflowInstanceResultModel, WorkflowInstanceState> stateColumn; // Value injected by FXMLLoader

    @FXML
    // fx:id="timeoutColumn"
    private TableColumn<WorkflowInstanceResultModel, Date> timeoutColumn; // Value injected by FXMLLoader

    @FXML
    // fx:id="timeoutColumn"
    private TableColumn<WorkflowInstanceResultModel, Date> lastActivityTimestamp;

    @FXML
    // fx:id="timeoutColumn"
    private TableColumn<WorkflowInstanceResultModel, String> overallLifetimeInMs;

    @FXML
    // fx:id="timeoutColumn"
    private TableColumn<WorkflowInstanceResultModel, Date> startTime;

    @FXML
    // fx:id="timeoutColumn"
    private TableColumn<WorkflowInstanceResultModel, Date> finishTime;

    @FXML
    // fx:id="timeoutColumn"
    private TableColumn<WorkflowInstanceResultModel, Date> lastErrorTime;

    @FXML
    // fx:id="timeoutColumn"
    private TableColumn<WorkflowInstanceResultModel, String> errorInfos;

    @FXML
    // fx:id="timeoutColumn"
    private BorderPane detailPane;

    @FXML
    // fx:id="timeoutColumn"
    private StackPane stackDetailPane;

    @FXML
    // fx:id="errorInfo"
    private TextArea errorInfo; // Value injected by FXMLLoader

    @FXML
    // fx:id="tableBorderPane"
    private BorderPane tableBorderPane; // Value injected by FXMLLoader

    private FxmlForm<FilterResultController<WorkflowInstanceDetailFilterModel, WorkflowInstanceDetailResultModel>> detailForm;
    private DetailLoadService service;

    @Override
    // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert detailPane != null : "fx:id=\"detailPane\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert errorInfos != null : "fx:id=\"errorInfos\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert finishTime != null : "fx:id=\"finishTime\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert idColumn != null : "fx:id=\"idColumn\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert lastActivityTimestamp != null : "fx:id=\"lastActivityTimestamp\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert lastErrorTime != null : "fx:id=\"lastErrorTime\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert overallLifetimeInMs != null : "fx:id=\"overallLifetimeInMs\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert prioritynColumn != null : "fx:id=\"prioritynColumn\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert processorPoolColumn != null : "fx:id=\"processorPoolColumn\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert resultTable != null : "fx:id=\"resultTable\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert stackDetailPane != null : "fx:id=\"stackDetailPane\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert startTime != null : "fx:id=\"startTime\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert stateColumn != null : "fx:id=\"stateColumn\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert timeoutColumn != null : "fx:id=\"timeoutColumn\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert errorInfo != null : "fx:id=\"errorInfo\" was not injected: check your FXML file 'WorkflowInstanceResult.fxml'.";
        assert tableBorderPane != null;
        tableBorderPane.setBottom(createTableControls(resultTable));

        idColumn.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(
                    CellDataFeatures<WorkflowInstanceResultModel, String> p) {
                return p.getValue().id;
            }
        });

        prioritynColumn.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(
                    CellDataFeatures<WorkflowInstanceResultModel, String> p) {
                return p.getValue().priority.asString();
            }
        });

        processorPoolColumn.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(
                    CellDataFeatures<WorkflowInstanceResultModel, String> p) {
                return p.getValue().processorPoolId;
            }
        });

        stateColumn.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, WorkflowInstanceState>, ObservableValue<WorkflowInstanceState>>() {
            @Override
            public ObservableValue<WorkflowInstanceState> call(
                    CellDataFeatures<WorkflowInstanceResultModel, WorkflowInstanceState> p) {
                return p.getValue().state;
            }
        });

        timeoutColumn.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(
                    CellDataFeatures<WorkflowInstanceResultModel, Date> p) {
                return p.getValue().timeout;
            }
        });
        TableColumnHelper.setConverterCellFactory(timeoutColumn, new DateStringConverter("dd.MM.yyyy HH:mm:ss"));

        lastActivityTimestamp.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(
                    CellDataFeatures<WorkflowInstanceResultModel, Date> p) {
                return p.getValue().lastActivityTimestamp;
            }
        });
        TableColumnHelper.setConverterCellFactory(lastActivityTimestamp, new DateStringConverter("dd.MM.yyyy HH:mm:ss"));
        overallLifetimeInMs.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(
                    CellDataFeatures<WorkflowInstanceResultModel, String> p) {
                return p.getValue().overallLifetimeInMs.asString();
            }
        });
        startTime.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(
                    CellDataFeatures<WorkflowInstanceResultModel, Date> p) {
                return p.getValue().startTime;
            }
        });
        TableColumnHelper.setConverterCellFactory(startTime, new DateStringConverter("dd.MM.yyyy HH:mm:ss"));
        finishTime.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(
                    CellDataFeatures<WorkflowInstanceResultModel, Date> p) {
                return p.getValue().finishTime;
            }
        });
        TableColumnHelper.setConverterCellFactory(finishTime, new DateStringConverter("dd.MM.yyyy HH:mm:ss"));
        lastErrorTime.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(
                    CellDataFeatures<WorkflowInstanceResultModel, Date> p) {
                return p.getValue().lastErrorTime;
            }
        });
        TableColumnHelper.setConverterCellFactory(lastErrorTime, new DateStringConverter("dd.MM.yyyy HH:mm:ss"));
        errorInfos.setCellValueFactory(new Callback<CellDataFeatures<WorkflowInstanceResultModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(
                    CellDataFeatures<WorkflowInstanceResultModel, String> p) {
                return p.getValue().errorInfos;
            }
        });
        errorInfos.setCellFactory(new Callback<TableColumn<WorkflowInstanceResultModel, String>, TableCell<WorkflowInstanceResultModel, String>>() {
            @Override
            public TableCell<WorkflowInstanceResultModel, String> call(TableColumn<WorkflowInstanceResultModel, String> param) {
                return new TextFieldTableCell<WorkflowInstanceResultModel, String>(new StringConverter<String>() {
                    @Override
                    public String fromString(String string) {
                        return string;
                    }

                    @Override
                    public String toString(String object) {
                        return abbreviate(object, 40);
                    }
                });
            }
        });

        resultTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2 && !resultTable.getSelectionModel().isEmpty()) {
                        navigation.createWorkflowInstanceDetailForm(resultTable.getSelectionModel().getSelectedItem().id.get(), usedFilter.selectedEngine.get()).show();
                    }
                    if (mouseEvent.getClickCount() == 1 && !resultTable.getSelectionModel().isEmpty()) {
                        showDetails(resultTable.getSelectionModel().getSelectedItem());
                    }

                }
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem detailMenuItem = new MenuItem("Details in new tab");
        detailMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                navigation.createWorkflowInstanceDetailForm(resultTable.getSelectionModel().getSelectedItem().id.get(), usedFilter.selectedEngine.get()).show();

            }
        });
        detailMenuItem.disableProperty().bind(resultTable.getSelectionModel().selectedItemProperty().isNull());
        contextMenu.getItems().add(detailMenuItem);
        MenuItem audittrailMenuItem = new MenuItem("AuditTrail");
        audittrailMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FilterAbleForm<AuditTrailFilterModel, AuditTrailResultModel> audittrailForm = navigation.createAuditTrailForm();
                audittrailForm.getFilter().workflowInstanceId.set(resultTable.getSelectionModel().getSelectedItem().id.get());
                audittrailForm.show();
            }
        });
        audittrailMenuItem.disableProperty().bind(resultTable.getSelectionModel().selectedItemProperty().isNull());
        contextMenu.getItems().add(audittrailMenuItem);

        resultTable.setContextMenu(contextMenu);

        idColumn.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.11));
        prioritynColumn.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.07));
        processorPoolColumn.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.09));
        stateColumn.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.07));
        timeoutColumn.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.09));
        lastActivityTimestamp.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.11));
        overallLifetimeInMs.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.09));
        startTime.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.09));
        finishTime.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.09));
        lastErrorTime.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.09));
        errorInfos.prefWidthProperty().bind(resultTable.widthProperty().subtract(3).multiply(0.1));

        detailForm = navigation.createWorkflowinstanceDetailResultForm(detailPane);
        detailForm.show();

        errorInfo.getStyleClass().add("consoleFont");
        errorInfo.setWrapText(false);
    }

    public static String abbreviate(String str, int maxWidth) {
        if (null == str) {
            return null;
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        return str.substring(0, maxWidth) + "...";
    }

    @Override
    public URL getFxmlResource() {
        return getClass().getResource("WorkflowInstanceResult.fxml");
    }

    @Override
    public void showFilteredResult(List<WorkflowInstanceResultModel> filteredResult, WorkflowInstanceFilterModel usedFilter) {
        ObservableList<WorkflowInstanceResultModel> content = FXCollections.observableList(new ArrayList<WorkflowInstanceResultModel>());
        content.addAll(filteredResult);
        setOriginalItems(resultTable, content);
        this.usedFilter = usedFilter;
    }

    WorkflowInstanceFilterModel usedFilter;

    @Override
    public List<WorkflowInstanceResultModel> applyFilterInBackgroundThread(WorkflowInstanceFilterModel filter) {
        return copperDataProvider.getWorkflowInstanceList(filter, filter.maxCountFilterModel.getMaxCount());
    }

    @Override
    public boolean supportsClear() {
        return true;
    }

    @Override
    public void clear() {
        resultTable.getItems().clear();
        errorInfo.clear();
        detailForm.getController().clear();
    }

    private void showDetails(final WorkflowInstanceResultModel workflowInstanceResultModel) {
        if (workflowInstanceResultModel != null) {
            errorInfo.setText(workflowInstanceResultModel.errorInfos.get());

            if (service == null) {
                service = new DetailLoadService(usedFilter, workflowInstanceResultModel, stackDetailPane, detailForm, issueReporter);
            }

            if (!service.isRunning()) {
                service.reset();
                service.setWorkflowInstanceResultModel(workflowInstanceResultModel);
                service.start();
            }
        } else {
            errorInfo.clear();
        }
    }

}
