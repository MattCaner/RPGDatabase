package app.controller;

import app.viewTool.ViewManager;
import app.viewTool.ViewOnloadEvent;
import app.viewTool.Views;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;

import app.sessionController.SessionController;

import app.psqlModule.PsqlModule;

public class MainViewController implements ViewOnloadEvent {

    @FXML
    private BorderPane mainContainer;
    @FXML
    private Pane menuBtnAdmin;
    @FXML
    private Pane menuBtnGM;
    @FXML
    private Pane menuBtnWB;
    @FXML
    private Pane menuBtnPlayer;
    @FXML
    private Pane menuBtnSettings;
    @FXML
    private VBox Btns;

    @FXML
    private Pane menuBtnLogout;

    public void initialize() {
        ViewManager.setMainContainer(mainContainer);
        initMenu();
    }

    private void initMenu() {

        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.czlowiek_uprawnienia WHERE id_czlowiek = '" + SessionController.getInstance().user_id + "';";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);




            while(rs.next()){
               // System.out.println("checking record...");
                int auth = rs.getInt("id");

                switch(auth){
                    case 0:
                        SessionController.getInstance().adminAcc = true;
                    break;
                    case 1:
                        SessionController.getInstance().wbAcc = true;
                    break;
                    case 2:
                        SessionController.getInstance().mgAcc = true;
                    break;
                    case 3:
                        SessionController.getInstance().playerAcc = true;
                    break;
                }
            }
            
            SessionController.getInstance().dbaseconnector.closeConnection();
        }
        catch (SQLException es){
            System.out.println("SQL Exception: "+es);
            es.printStackTrace();
        }
        catch (Exception e){
            System.out.println("Error: "+e);
            e.printStackTrace();
        }

        if(SessionController.getInstance().adminAcc == true){
            menuBtnAdmin.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                ViewManager.showViewInMainContainer(Views.Admin);
            });
        }else{
            Btns.getChildren().remove(menuBtnAdmin);
        }

        if(SessionController.getInstance().wbAcc == true){
            menuBtnWB.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                ViewManager.showViewInMainContainer(Views.WB);
            });

        }else{
            Btns.getChildren().remove(menuBtnWB);
        }


        if(SessionController.getInstance().playerAcc == true){
            menuBtnPlayer.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                ViewManager.showViewInMainContainer(Views.Player);
            });
        }else{
            Btns.getChildren().remove(menuBtnPlayer);
        }

        if(SessionController.getInstance().mgAcc == true){
            menuBtnGM.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                ViewManager.showViewInMainContainer(Views.GM);
            });
        }else{
            Btns.getChildren().remove(menuBtnGM);
        }


        menuBtnSettings.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            ViewManager.showViewInMainContainer(Views.Settings);
        });



        menuBtnLogout.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            SessionController.logout();
        });


        
    }
}
