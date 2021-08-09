package app.controller;

import app.psqlModule.PsqlModule;

import app.viewTool.ViewManager;
import app.viewTool.ViewOnloadEvent;
import app.viewTool.Views;
import app.sessionController.SessionController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.sql.ResultSet;

public class LoginController implements ViewOnloadEvent {
    @FXML
    private Button loginBtn;

    @FXML
    private TextField userInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Label status;

    public void initialize() {

        loginBtn.setOnAction(e -> {
            login();
        });
    }

    public void login() {

        try{
            String user = userInput.getText();
            int id;

            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.czlowiek c JOIN projekt.passwd using(id_czlowiek) WHERE nick = '" + user + "';";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            if(rs.next()){

                String hash_input = PsqlModule.hashSHA(passwordInput.getText());
                String hash_correct = rs.getString("hash");

                //System.out.println("hash_input: " + hash_input);
                //System.out.println("hash_correct: "+ hash_correct);
                if(hash_input.equals(hash_correct)){
                    id = rs.getInt("id_czlowiek");
                    SessionController.getInstance().user_id = id;
                    ViewManager.registerDefaultViews();
                    ViewManager.setScene(Views.MainView);
                }
                else{
                    status.setText("Wpisane haslo jest niepoprawne!");
                }


            }
            else{
                System.out.println("No such user!");
                status.setText("Nie ma takiego uzytkownika!");
            }
            
            SessionController.getInstance().dbaseconnector.closeConnection();
        }
        catch (SQLException es){
            
            status.setText("Doszło do błędu: komunikat serwera: "+es);
            System.out.println("SQL Exception: "+es);
            es.printStackTrace();
        }
        catch (Exception e){
            System.out.println("Error: "+e);
            e.printStackTrace();
        }


    }

}
