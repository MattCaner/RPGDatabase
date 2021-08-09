package app;

import app.viewTool.ViewManager;
import app.viewTool.Views;
import app.psqlModule.PsqlModule;
import app.sessionController.SessionController;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.Scanner;

public class Main extends Application {

    String user;
    String base;
    String passwd;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try{

            File f = new File("settings.conf");
            Scanner s = new Scanner(f);

            String data = s.nextLine();
            String [] conf = data.split("=");
            base = conf[1];
            data = s.nextLine();
            conf = data.split("=");
            user = conf[1];            
            data = s.nextLine();
            conf = data.split("=");
            passwd = conf[1];

            s.close();


        }
        catch(Exception e){

            System.out.println("Settings file not found, loading default settings...");
            base = "balarama.db.elephantsql.com:5432/ooreisbx";
            user = "ooreisbx";
            passwd = "bBCm2HP9VP4DdFFVWuy6OVB33oX-pxbO";

        }

        try{


            SessionController.getInstance().dbaseconnector = new PsqlModule("jdbc:postgresql://"+base, user, passwd);            

            //change for localhost: 
            //SessionController.getInstance().dbaseconnector = new PsqlModule("jdbc:postgresql://localhost:5432/test", "postgres", "zaq12wsx");
            //SessionController.getInstance().dbaseconnector = new PsqlModule("jdbc:postgresql://balarama.db.elephantsql.com:5432/ooreisbx", "ooreisbx", "bBCm2HP9VP4DdFFVWuy6OVB33oX-pxbO");
            System.out.println("connected");
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

        ViewManager.init(primaryStage);
        ViewManager.registerScene(Views.Login);
        ViewManager.setScene(Views.Login);
        
    }
}