package application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	static Model m;
	@Override
	public void start(Stage launcher) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Layout.fxml"));
			launcher.setScene(new Scene(root));
			launcher.setTitle("SenseHAT SCADA");
			launcher.initStyle(StageStyle.DECORATED);
			launcher.show();
			launcher.setOnCloseRequest(arg0 -> {
				m.shutdownAllServices();
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		m = new Model();
		launch(args);
	}
}
