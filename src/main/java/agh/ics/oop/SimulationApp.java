package agh.ics.oop;

import agh.ics.oop.presenter.LauncherPresenter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class SimulationApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("launcher.fxml"));

        VBox viewRoot = loader.load();
        LauncherPresenter presenter = loader.getController();

        Scene scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Generator Symulacji");


        primaryStage.show();
    }
}