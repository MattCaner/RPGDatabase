package app.viewTool;

import app.Main;

import app.exception.IViewControllerNotImplementedException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ViewManager {

    private static Stage primaryStage;
    private static BorderPane mainContainer;
    private static Map<String, Scene> allScene = new HashMap<>();
    private static Map<String, ViewOnloadEvent> allController = new HashMap<>();


    private static int sceneWidth = 1200;
    private static int sceneHeight = 800;

    private ViewManager() {}

    public static void init(Stage mainPrimaryStage) throws Exception {
        primaryStage = mainPrimaryStage;
        primaryStage.setWidth(sceneWidth);
        primaryStage.setHeight(sceneHeight);

        primaryStage.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            sceneWidth = newSceneWidth.intValue();
        });

        primaryStage.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            sceneHeight = newSceneHeight.intValue();
        });
        
    }

    public static void clearViewData() {
        allController.clear();
    }

    public static void registerDefaultViews() throws Exception {
        for (Views views : Views.values()) {
            registerScene(views);
        }
    }

    public static void setScene(Views view) {

        primaryStage.setScene(allScene.get(view.path));
        primaryStage.setTitle(view.title);
        primaryStage.show();
        primaryStage.setWidth(sceneWidth);
        primaryStage.setHeight(sceneHeight);

        allController.get(view.path).onLoad();
    }

    public static void setMainContainer(BorderPane _mainContainer) {
        mainContainer = _mainContainer;
    }

    public static void showViewInMainContainer(Views view) {
        allController.get(view.path).onLoad();
        mainContainer.setCenter(allScene.get(view.path).getRoot());
    }

    public static void registerScene(Views view) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL path = Main.class.getResource("/app/view/fxml/" + view.path + ".fxml");
            if (path == null) {
                throw new Exception(view.path + " not found");
            }
            loader.setLocation(path);
            BorderPane root = loader.load();
            Scene scene = new Scene(root);
            System.out.println(scene);
            scene.getStylesheets().add(
                    Main.class.getResource("/app/view/asset/style/style.css").toExternalForm()
            );

            if (!view.cssPath.equals("")) {
                //System.out.println("/app/view/asset/style/" + view.cssPath + ".css");
                scene.getStylesheets().add(
                        Main.class.getResource("/app/view/asset/style/" + view.cssPath + ".css")
                                .toExternalForm()
                );
            }
            allScene.put(view.path, scene);

            if (!(loader.getController() instanceof ViewOnloadEvent))
                throw new IViewControllerNotImplementedException(view.path);

            allController.put(view.path, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}