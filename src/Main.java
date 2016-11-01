import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

/**
 * Main Launcher class that begins the GUI application
 * Created by NikhilVerma on 21/10/16.
 */
public class Main extends Application{

    public static final double WIDTH = 1100;
    public static final double HEIGHT = 750;

    public static final String TITLE="Untitled";
    private static final String MAIN_VIEW_LOCATION="user-interface.fxml";
    private static final String STYLESHEET="style.css";

    File fileToOpen=null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getClassLoader().getResource(MAIN_VIEW_LOCATION));
        Parent root=fxmlLoader.load();
//        root.getStylesheets().add(STYLESHEET);
        Scene scene=new Scene(root,WIDTH,HEIGHT);
        if(fileToOpen==null){
            primaryStage.setTitle(TITLE);
        }else{
            primaryStage.setTitle(fileToOpen.toString());
        }
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setScene(scene);
        primaryStage.show();//layout containers wont be initialized until primary stage is shown
//        MainWindowController controller = (MainWindowController) fxmlLoader.getController();
//        controller.init(fileToOpen);
//        scene.setOnKeyPressed(controller::keyPressed);
    }
}
