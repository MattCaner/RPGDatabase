package app.controller;


import app.psqlModule.PsqlModule;
import app.viewTool.ViewOnloadEvent;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import app.viewTool.ViewOnloadEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import app.viewTool.ViewManager;
import app.viewTool.ViewOnloadEvent;
import app.viewTool.Views;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import app.sessionController.SessionController;

public class SettingsController implements ViewOnloadEvent {

    @FXML 
    Label user_id_label;

    @FXML
    Label user_nick_label;

    @FXML
    Label user_group_label;
    
    @FXML
    Label user_name_label;

    @FXML
    Label user_sessions_number_label;

    @FXML
    Button changeNickButton;

    @FXML
    Button changePasswordButton;

    @FXML
    VBox optionsPanel; 

    @FXML
    Label generalStatus;

    Label newPassword;
    Label newPasswordAgain;
    TextField newPasswordTF;
    TextField newPasswordAgainTF;
    Button acceptPasswordChange;


    public void loadEmpty(){
        optionsPanel.getChildren().clear();
    }

    public void loadPasswdChange(){
        optionsPanel.getChildren().clear();
        newPassword = new Label();
        newPassword.setText("Wpisz nowe haslo:");
        optionsPanel.getChildren().add(newPassword);

        newPasswordTF = new TextField();
        optionsPanel.getChildren().add(newPasswordTF);

        newPasswordAgain = new Label();
        newPasswordAgain.setText("Wpisz nowe haslo ponownie:");
        optionsPanel.getChildren().add(newPasswordAgain);

        newPasswordAgainTF = new TextField();
        optionsPanel.getChildren().add(newPasswordAgainTF);

        acceptPasswordChange = new Button();
        acceptPasswordChange.setText("Zmien");
        optionsPanel.getChildren().add(acceptPasswordChange);

        
        acceptPasswordChange.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.tryPasswordChange();
        });

        
    }

    void loadNickChange(){
        optionsPanel.getChildren().clear();
        newPassword = new Label();
        newPassword.setText("Wpisz nowy nick:");
        optionsPanel.getChildren().add(newPassword);

        newPasswordTF = new TextField();
        optionsPanel.getChildren().add(newPasswordTF);

        newPasswordAgain = new Label();
        newPasswordAgain.setText("Wpisz nowy nick ponownie:");
        optionsPanel.getChildren().add(newPasswordAgain);

        newPasswordAgainTF = new TextField();
        optionsPanel.getChildren().add(newPasswordAgainTF);

        acceptPasswordChange = new Button();
        acceptPasswordChange.setText("Zmien");
        optionsPanel.getChildren().add(acceptPasswordChange);

        
        acceptPasswordChange.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.tryNickChange();
        });
    }

    void tryNickChange(){
        System.out.println(newPasswordTF.getText());
        System.out.println(newPasswordAgainTF.getText());

        if(newPasswordTF.getText().equals(newPasswordAgainTF.getText())){
            try{

                SessionController.getInstance().dbaseconnector.openConnection();

                String newNick = newPasswordTF.getText();
                
                PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("UPDATE projekt.czlowiek SET nick = ? WHERE id_czlowiek = ?");

                insert_st.setString(1,newNick);
                insert_st.setInt(2,SessionController.getInstance().user_id);

                int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
                SessionController.getInstance().dbaseconnector.closeConnection();

                generalStatus.setText("Nick zmieniony");

                SessionController.getInstance().dbaseconnector.closeConnection();

                user_nick_label.setText("Twoj nick: "+newNick);
                loadEmpty();
            }
            catch(SQLException es){
                System.out.println("SQL Exception: "+es);
                es.printStackTrace();
            }
            catch (Exception e){
                System.out.println("Error: "+e);
                e.printStackTrace();
            }
        }
        else{
            generalStatus.setText("Nicki nie sa identyczne");
        }
    }


    void tryPasswordChange(){

        System.out.println(newPasswordTF.getText());
        System.out.println(newPasswordAgainTF.getText());

        if(newPasswordTF.getText().equals(newPasswordAgainTF.getText())){
            try{

                SessionController.getInstance().dbaseconnector.openConnection();

                String hash = PsqlModule.hashSHA(newPasswordTF.getText());
                
                PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("UPDATE projekt.passwd SET hash = ? WHERE id_czlowiek = ?");

                insert_st.setString(1,hash);
                insert_st.setInt(2,SessionController.getInstance().user_id);

                int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
                SessionController.getInstance().dbaseconnector.closeConnection();
                System.out.println("Password updated: "+i);

                generalStatus.setText("Haslo zmienione");

                SessionController.getInstance().dbaseconnector.closeConnection();
                loadEmpty();
            }
            catch(SQLException es){
                System.out.println("SQL Exception: "+es);
                es.printStackTrace();
            }
            catch (Exception e){
                System.out.println("Error: "+e);
                e.printStackTrace();
            }
        }
        else{
            generalStatus.setText("Hasla nie sa identyczne");
        }
    }

    public void initialize() {

        int id = SessionController.getInstance().user_id;
        user_id_label.setText("Twoje id: "+id);
        
        generalStatus.setText("RPGDatabase v. 0.1");

        try{
            
            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.czlowiek WHERE id_czlowiek = " + id + ";";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            if(rs.next()){
                String name = rs.getString("imie");
                String nick = rs.getString("nick");

                user_name_label.setText("Twoje imie: "+name);
                user_nick_label.setText("Twoj nick: "+nick);
            }
            else{
                throw new Exception ("No results were returned.");
            }

            query = "SELECT * FROM projekt.gracz_grupa gg JOIN projekt.grupa g using(id_grupa) WHERE id_czlowiek = " + id + ";";

            String groups = "Twoje grupy:";
            rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            while(rs.next()){
                String name = rs.getString("nazwa");
                groups = ""+groups + name + " ";

            }
            if(groups.equals("Twoje grupy:")) groups = groups + " nie jestes przypisany do zadnej grupy.";
            user_name_label.setText(groups);      

            
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

        changePasswordButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.loadPasswdChange();
        });

        changeNickButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            this.loadNickChange();
        });

    }
}
