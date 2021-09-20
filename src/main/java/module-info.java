module com.brailsoft.weight.monitor {
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;

	opens com.brailsoft.weightmonitor.launcher to javafx.fxml;
	opens com.brailsoft.weightmonitor.controller to javafx.fxml;
	opens com.brailsoft.weightmonitor.model;

	exports com.brailsoft.weightmonitor.launcher;
}
