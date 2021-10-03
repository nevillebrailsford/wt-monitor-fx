package com.brailsoft.weightmonitor.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.brailsoft.weightmonitor.model.History;
import com.brailsoft.weightmonitor.model.HistoryIO;
import com.brailsoft.weightmonitor.model.Observation;
import com.brailsoft.weightmonitor.statistics.StatisticsProvider;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

public class WeightMonitorController implements Initializable {

	@FXML
	private DatePicker datePicker;

	@FXML
	private TextField weightField;

	@FXML
	private Button addWeightButton;

	@FXML
	private TextArea weightFileTextArea;

	@FXML
	private ListView<Observation> weightsListView;

	@FXML
	private BorderPane borderPane;

	@FXML
	private MenuItem newMenuItem;

	@FXML
	private MenuItem openMenuItem;

	@FXML
	private MenuItem saveMenuItem;

	@FXML
	private TabPane tabPane;

	@FXML
	private Button deleteSelectionButton;

	@FXML
	private LineChart<String, Number> lineChart;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	@FXML
	private TextField earliestDate;

	@FXML
	private TextField latestDate;

	@FXML
	private TextField earliestWeight;

	@FXML
	private TextField latestWeight;

	@FXML
	private TextField minimumWeight;

	@FXML
	private TextField maximumWeight;

	@FXML
	private TextField averageWeight;

	@FXML
	private TextField numberObservations;

	private BooleanProperty canAddWeightProperty;
	private BooleanProperty canDeleteSelectionProperty;
	private String lastFile = "";
	private boolean dirty = false;
	private History history = History.getInstance();

	private int yearIndex = 0;

	MapChangeListener<? super Long, ? super Observation> listener = new MapChangeListener<>() {

		@Override
		public void onChanged(Change<? extends Long, ? extends Observation> change) {
			if (change.wasRemoved()) {
				removeFromWeightsListView(change.getValueRemoved());
			}
			if (change.wasAdded()) {
				addToWeightsListView(change.getValueAdded());
			}
			loadChart();
			loadStatistics();
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		datePicker.setConverter(new StringConverter<LocalDate>() {
			String pattern = "yyyy/MM/dd";
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

			{
				datePicker.setPromptText(pattern.toLowerCase());
				datePicker.setShowWeekNumbers(true);
			}

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});

		canAddWeightProperty = new SimpleBooleanProperty(this, "canAddWeight", false);
		addWeightButton.disableProperty().bind(canAddWeightProperty.not());

		canDeleteSelectionProperty = new SimpleBooleanProperty(this, "canDeleteSelction", false);
		deleteSelectionButton.disableProperty().bind(canDeleteSelectionProperty.not());

		weightsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		weightsListView.getSelectionModel().selectedItemProperty().addListener((change, oldValue, newValue) -> {
			if (newValue == null) {
				disableDelete();
			} else {
				populateEditFields();
				enableDelete();
			}
		});

		history.addListener(listener);

		loadData();

	}

	protected void removeFromWeightsListView(Observation valueRemoved) {
		weightsListView.getItems().remove(valueRemoved);
	}

	protected void addToWeightsListView(Observation valueAdded) {
		int index = 0;
		if (!weightsListView.getItems().isEmpty()) {
			index = findObservation(valueAdded);
		}
		weightsListView.getItems().add(index, valueAdded);
		weightsListView.scrollTo(index);
//		weightsListView.getSelectionModel().select(index);
	}

	private int findObservation(Observation observationToFind) {
		int i;
		for (i = weightsListView.getItems().size() - 1; i >= 0; i--) {
			if (weightsListView.getItems().get(i).compareTo(observationToFind) < 0) {
				break;
			}
		}
		return i + 1;
	}

	private void enableDelete() {
		canDeleteSelectionProperty.set(true);
	}

	private void disableDelete() {
		canDeleteSelectionProperty.set(false);
	}

	@FXML
	private void datePickerAction(javafx.event.ActionEvent e) {
		shouldAddButtonBeEnabled();
	}

	@FXML
	private void weightFieldAction(javafx.event.ActionEvent e) {
		shouldAddButtonBeEnabled();
	}

	@FXML
	public void addWeightButtonAction(javafx.event.ActionEvent e) {
		int selectedIndex = weightsListView.getSelectionModel().getSelectedIndex();
		dirty = true;
		String date = datePicker.getConverter().toString(datePicker.getValue());
		String reading = weightField.getText();
		history.addObservation(new Observation(date, reading));
		weightField.setText("");
		canAddWeightProperty.set(false);
		if (selectedIndex >= 0) {
			weightsListView.getSelectionModel().clearAndSelect(selectedIndex);
		}
	}

	@FXML
	public void deleteSelectionButtonAction(ActionEvent event) {
		dirty = true;
		var item = weightsListView.getSelectionModel().getSelectedItem();
		history.removeObservation(item);
		canDeleteSelectionProperty.set(false);
	}

	private void shouldAddButtonBeEnabled() {
		if (weightField.getText().isEmpty() || datePicker.getValue() == null) {
			canAddWeightProperty.set(false);
		} else {
			canAddWeightProperty.set(true);
		}
	}

	private void loadData() {
		try (BufferedReader inputFile = new BufferedReader(new FileReader("weight.ini"));) {
			lastFile = inputFile.readLine();
			inputFile.close();
		} catch (Exception e) {
			lastFile = "";
		}
		initializeGUI();
		if (!lastFile.equals("")) {
			HistoryIO.getInstance().loadWeightFile(lastFile);
		}
		weightFileTextArea.setText(lastFile.isBlank() ? "New File" : lastFile);
		weightsListView.getSelectionModel().clearSelection();
	}

	private void initializeGUI() {
		history.clear();
		tabPane.getSelectionModel().select(0);
		weightFileTextArea.setText("New File");
		datePicker.setValue(LocalDate.now());
		weightField.setText("");
	}

	public boolean shutdown() {
		if (dirty && !lastFile.isBlank()) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save your changes before exiting?",
					ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
			alert.setTitle("Exit application");
			alert.setHeaderText("Unsaved changes");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.YES) {
				saveWeightFile(new File(lastFile));
			} else if (result.get() == ButtonType.CANCEL) {
				return false;
			}
		}
		return true;
	}

	public void saveIniFile() {
		try (PrintWriter outputFile = new PrintWriter(new BufferedWriter(new FileWriter("weight.ini")))) {
			outputFile.println(lastFile);
			outputFile.flush();
		} catch (Exception e) {
		}

	}

	@FXML
	public void newMenuItemActionPerformed(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("New File");
		alert.setContentText("Are you sure you want to start a new weight file?");
		alert.setHeaderText("New file confirmation");
		((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
		((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			initializeGUI();
			lastFile = "";
			weightFileTextArea.setText("New File");
		}
	}

	@FXML
	public void openMenuItemActionPerformed(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Open File");
		alert.setContentText("Are you sure you want to open an existing weight file?");
		alert.setHeaderText("Open file confirmation");
		((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
		((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			FileChooser fileChooser = new FileChooser();
			String dir = lastFile.isEmpty() ? "/Users/" + System.getProperty("user.name")
					: lastFile.substring(0, lastFile.lastIndexOf(File.separator));
			fileChooser.setInitialDirectory(new File(dir));
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Weight Files", "*.wgt"));
			File selectedFile = fileChooser.showOpenDialog(((Node) borderPane).getScene().getWindow());
			if (selectedFile != null) {
				initializeGUI();
				lastFile = selectedFile.getAbsolutePath();
				HistoryIO.getInstance().loadWeightFile(lastFile);
				weightFileTextArea.setText(lastFile);
				dirty = false;
			}
		}
	}

	@FXML
	public void saveMenuItemActionPerformed(ActionEvent event) {
		if (weightsListView.getItems().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Save File");
			alert.setContentText("You need to enter at least one weight value");
			alert.setHeaderText("No weight values found");
			alert.showAndWait();
			return;
		}
		FileChooser fileChooser = new FileChooser();
		String dir = lastFile.isEmpty() ? "/Users/" : lastFile.substring(0, lastFile.lastIndexOf(File.separator));
		fileChooser.setInitialDirectory(new File(dir));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Weight Files", "*.wgt"));
		File selectedFile = fileChooser.showSaveDialog(((Node) borderPane).getScene().getWindow());
		if (selectedFile != null) {
			if (selectedFile.exists()) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("Save File");
				alert.setContentText(selectedFile.toString() + " already exists. Overwrite?");
				alert.setHeaderText("Confirm Save");
				((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
				((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.CANCEL) {
					return;
				}
			}
			saveWeightFile(selectedFile);
		}
	}

	public void saveWeightFile(File selectedFile) {
		String filename = selectedFile.toString();
		int dotLocation = filename.lastIndexOf(".");
		if (dotLocation == -1) {
			filename = filename + ".wgt";
		} else {
			filename = filename.substring(0, dotLocation) + ".wgt";
		}
		lastFile = filename;
		weightFileTextArea.setText(lastFile);
		HistoryIO.getInstance().saveWeightFile(lastFile);
		dirty = false;
	}

	private void populateEditFields() {
		Observation o = weightsListView.getSelectionModel().getSelectedItem();
		String pattern = "yyyy/MM/dd";
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
		datePicker.setValue(LocalDate.parse(o.getDate(), dateFormatter));
		weightField.setText(o.getWeight());
		shouldAddButtonBeEnabled();
	}

	private void loadChart() {
		lineChart.getData().clear();

		List<String> years = history.getYears();

		if (years.size() < 1) {
			lineChart.setTitle("No data");
			return;
		} else {
			lineChart.setTitle("Weight by month");
		}

		for (yearIndex = 0; yearIndex < years.size(); yearIndex++) {
			Map<String, Double> monthlyAverages = StatisticsProvider
					.getHistoryAveragesByMonthForYear(years.get(yearIndex));
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName(years.get(yearIndex));
			for (int i = 0; i < Observation.months.length; i++) {
				String s = Observation.months[i];
				Double average = monthlyAverages.get(s);
				series.getData().add(new XYChart.Data<String, Number>(s, Double.valueOf(average)));
			}
			lineChart.getData().add(series);
		}
	}

	private void loadStatistics() {

		double lowerBound = StatisticsProvider.getMinimumOrigin();
		double upperBound = StatisticsProvider.getMaximumOrigin();

		yAxis.setLowerBound(lowerBound);
		yAxis.setUpperBound(upperBound);

		earliestDate.setText(StatisticsProvider.getFirstRecordedDate());
		latestDate.setText(StatisticsProvider.getLastRecordedDate());

		earliestWeight.setText(StatisticsProvider.getFirstRecordedWeight());
		latestWeight.setText(StatisticsProvider.getLastRecordedWeight());

		minimumWeight.setText(Double.toString(StatisticsProvider.getMinimumWeight()));
		maximumWeight.setText(Double.toString(StatisticsProvider.getMaximumWeight()));

		DecimalFormat formatter = new DecimalFormat("#0.00");

		averageWeight.setText(formatter.format(StatisticsProvider.getAverageWeight()));
		numberObservations.setText(Integer.toString(StatisticsProvider.getNumberOfObservations()));

	}

}
