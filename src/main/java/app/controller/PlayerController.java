package app.controller;

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

public class PlayerController implements ViewOnloadEvent {

    private int heroid = -1;

    @FXML
    VBox utilityButtons;

    @FXML
    VBox heroList;

    @FXML
    VBox heroDesc;

    @FXML
    VBox heroStats;

    @FXML
    Label status;

    @FXML
    TextField heroStats_nameInput;
    @FXML
    TextField heroStats_levelInput;
    @FXML
    TextField heroStats_classInput;
    @FXML
    TextField heroStats_charInput;
    @FXML
    TextField heroStats_acInput;
    @FXML
    TextField heroStats_pwmaxInput;
    @FXML
    TextField heroStats_siaInput;
    @FXML
    TextField heroStats_zrcznoInput;
    @FXML
    TextField heroStats_mdroInput;
    @FXML
    TextField heroStats_inteligencjaInput;
    @FXML
    TextField heroStats_charyzmaInput;
    @FXML
    TextField heroStats_kondycjaInput;
    @FXML
    TextField heroStats_slotsInput;

    @FXML
    Button removeHeroBtn;

    @FXML 
    ComboBox heroStats_raceInput;

    @FXML
    VBox heroItems;


    Button acceptAndAdd;

    @FXML
    Button modifyBtn;

    Label nrCampaignInfo;
    TextField nrCampaign;

    TextField [] inputs;

    boolean inputNewPlayerMode = false;

    public void buildHeroList(){
        //System.out.println("building hero list");
        heroList.getChildren().clear();
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.pc WHERE czlowiek_id = '" + SessionController.getInstance().user_id + "';";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            while(rs.next()){
                //System.out.println("checking record...");
                int id_hero = rs.getInt("id_postac");
                String name_hero = rs.getString("imie");
                Button btn = new Button();
                btn.setText(name_hero);
                btn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.showHero(id_hero);
                });
                heroList.getChildren().add(btn);
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
    }

    public void modifyValues(){
        System.out.println("Adding new hero");

        try{

            String s = (String)heroStats_raceInput.getValue();

            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("UPDATE projekt.pc SET imie = ?,klasa = ?,poziom = ?,sia = ?,zrczno = ?,mdro = ?,inteligencja =?,charyzma = ?,kondycja = ?,charakter = ?,pw_max = ?,kp = ?,komrki_zakl = ? WHERE id_postac = ?");

            insert_st.setString(1,heroStats_nameInput.getText());
            insert_st.setString(2,heroStats_classInput.getText());
            insert_st.setInt(3,Integer.parseInt(heroStats_levelInput.getText()));
            insert_st.setInt(4,Integer.parseInt(heroStats_siaInput.getText()));
            insert_st.setInt(5,Integer.parseInt(heroStats_zrcznoInput.getText()));
            insert_st.setInt(6,Integer.parseInt(heroStats_mdroInput.getText()));
            insert_st.setInt(7,Integer.parseInt(heroStats_inteligencjaInput.getText()));
            insert_st.setInt(8,Integer.parseInt(heroStats_charyzmaInput.getText()));
            insert_st.setInt(9,Integer.parseInt(heroStats_kondycjaInput.getText()));
            insert_st.setString(10,heroStats_charInput.getText());
            insert_st.setInt(11,Integer.parseInt(heroStats_pwmaxInput.getText()));
            insert_st.setInt(12,Integer.parseInt(heroStats_acInput.getText()));
            insert_st.setString(13,heroStats_slotsInput.getText());
            insert_st.setInt(14,heroid);

            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();
            System.out.println("Hero updated, "+i);
        }
        catch(SQLException es){
            System.out.println("SQL Exception: "+es);
            es.printStackTrace();
        }
        catch (Exception e){
            System.out.println("Error: "+e);
            e.printStackTrace();
        }

        buildHeroList();

    }

    public void removeHero(int id){
        try{
            
            SessionController.getInstance().dbaseconnector.openConnection();
            PreparedStatement delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.przedmiot_postac WHERE id_postac = ?");
            delete_st.setInt(1,heroid);
            SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);

            delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.udzial_w_sesji WHERE id_postac = ?");
            delete_st.setInt(1,heroid);
            SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);            

            delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.pc WHERE id_postac = ?");
            delete_st.setInt(1,heroid);
            SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);

            SessionController.getInstance().dbaseconnector.closeConnection();

            heroItems.getChildren().clear();
            showItems(heroid);
            heroDesc.setVisible(false);
            buildHeroList();
            
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

    public void showHero(int id){

        heroid = id;

        switchToLookupMode();
        
        try{

            showItems(id);

            SessionController.getInstance().dbaseconnector.openConnection();

            heroDesc.setVisible(true);

            int rasa_id = 0;

            String query = "SELECT * FROM projekt.pc WHERE id_postac = " + id + ";";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            if(rs.next()){
                heroStats_nameInput.setText(rs.getString("imie"));
                heroStats_levelInput.setText(""+rs.getInt("poziom"));
                heroStats_classInput.setText(rs.getString("klasa"));
                rasa_id = rs.getInt("id_rasa");
                heroStats_acInput.setText(""+rs.getInt("kp"));
                heroStats_pwmaxInput.setText(""+rs.getInt("pw_max"));
                heroStats_siaInput.setText(""+rs.getInt("sia"));
                heroStats_zrcznoInput.setText(""+rs.getInt("zrczno"));
                heroStats_mdroInput.setText(""+rs.getInt("mdro"));
                heroStats_inteligencjaInput.setText(""+rs.getInt("inteligencja"));
                heroStats_charyzmaInput.setText(""+rs.getInt("charyzma"));
                heroStats_kondycjaInput.setText(""+rs.getInt("kondycja"));
                heroStats_slotsInput.setText(rs.getString("komrki_zakl"));
            }
            else{
                throw new Exception ("error querying database! Nothing returned!");
            }

            query = "SELECT * FROM projekt.rasa WHERE id_rasa = " + rasa_id + ";";
            rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            if(rs.next()){
                heroStats_raceInput.setValue(rs.getString("nazwa_rasa"));
            }   
            else{
                throw new Exception ("error querying database! Nothing returned!");
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

    public void saveHero(){

        System.out.println("Adding new hero");

        try{

            
            SessionController.getInstance().dbaseconnector.openConnection();





            String s = (String)heroStats_raceInput.getValue();

            String query = "SELECT * FROM projekt.rasa WHERE nazwa_rasa = '" + s +"';";

            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            int rasa_id = 1;

            if(rs.next()){
                rasa_id = rs.getInt("id_rasa");
            }





            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.pc (czlowiek_id,imie,id_kampania,klasa,poziom,sia,zrczno,mdro,inteligencja,charyzma,kondycja,charakter,pw_max,kp,id_rasa,komrki_zakl) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

            insert_st.setInt(1,SessionController.getInstance().user_id);
            insert_st.setString(2,heroStats_nameInput.getText());
            insert_st.setInt(3,Integer.parseInt(nrCampaign.getText()));
            insert_st.setString(4,heroStats_classInput.getText());
            insert_st.setInt(5,Integer.parseInt(heroStats_levelInput.getText()));
            insert_st.setInt(6,Integer.parseInt(heroStats_siaInput.getText()));
            insert_st.setInt(7,Integer.parseInt(heroStats_zrcznoInput.getText()));
            insert_st.setInt(8,Integer.parseInt(heroStats_mdroInput.getText()));
            insert_st.setInt(9,Integer.parseInt(heroStats_inteligencjaInput.getText()));
            insert_st.setInt(10,Integer.parseInt(heroStats_charyzmaInput.getText()));
            insert_st.setInt(11,Integer.parseInt(heroStats_kondycjaInput.getText()));
            insert_st.setString(12,heroStats_charInput.getText());
            insert_st.setInt(13,Integer.parseInt(heroStats_pwmaxInput.getText()));
            insert_st.setInt(14,Integer.parseInt(heroStats_acInput.getText()));
            insert_st.setInt(15,rasa_id);
            insert_st.setString(16,heroStats_slotsInput.getText());

            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            System.out.println("Hero inserted, "+i);

                
            switchToLookupMode();
            heroDesc.setVisible(false);
            buildHeroList();
        }
        catch (SQLException es){
            status.setText("Problem SQLa: "+es);
            System.out.println("SQL Exception: "+es);
            es.printStackTrace();
        }
        catch (Exception e){
            status.setText("Dodawanie bohatera nie powiodlo sie!");
            System.out.println("Error: "+e);
            e.printStackTrace();
        }

    }

    public void switchToAddMode(){
        removeHeroBtn.setVisible(false);
        heroItems.getChildren().clear();
        if(inputNewPlayerMode) return;
        heroDesc.setVisible(true);

        inputNewPlayerMode = true;
        for(TextField tf : inputs){
            tf.setEditable(true);
            tf.setText("");
        }

        
        nrCampaignInfo = new Label();
        nrCampaignInfo.setText("Tu wpisz numer kampanii podany przez MG:");
        heroDesc.getChildren().add(nrCampaignInfo);

        nrCampaign = new TextField();
        heroDesc.getChildren().add(nrCampaign);


        acceptAndAdd = new Button("Akceptuj i dodaj");
        heroDesc.getChildren().add(acceptAndAdd);
        acceptAndAdd.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.saveHero();
        });

        modifyBtn.setVisible(false);

        
        
    }

    public void switchToLookupMode(){
        heroItems.getChildren().clear();
        heroDesc.setVisible(true);
        if(acceptAndAdd!=null){
            heroDesc.getChildren().remove(acceptAndAdd);
            heroDesc.getChildren().remove(nrCampaign);
            heroDesc.getChildren().remove(nrCampaignInfo);
        }
        inputNewPlayerMode = false;

        modifyBtn.setVisible(true);
    }
    
    public void showItems(int id){

        heroItems.getChildren().clear();

        try{


            SessionController.getInstance().dbaseconnector.openConnection();

            heroDesc.setVisible(true);

            String query = "SELECT * FROM projekt.przedmiot_postac pp join projekt.przedmiot p using(id_przedmiotu) WHERE id_postac = " + id + ";";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            while(rs.next()){
                Label l = new Label(rs.getString("nazwa_przedmiotu")+" ("+rs.getInt("ilosc")+")");
                heroItems.getChildren().add(l);
                l = new Label(rs.getString("dziaanie"));
                heroItems.getChildren().add(l);
                l = new Label(rs.getString("rzadkosc")+", wartosc w szt. s.: "+rs.getDouble("wartosc_sp"));    
                heroItems.getChildren().add(l);
                Button b = new Button("Usun");
                heroItems.getChildren().add(b);
                int itemId = rs.getInt("id_przedmiotu");
                b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.removeItemButton(itemId);
                });
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

    public void removeItemButton(int id){
        try{
            

            SessionController.getInstance().dbaseconnector.openConnection();
            heroDesc.setVisible(true);
            PreparedStatement delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.przedmiot_postac WHERE id_przedmiotu=? AND id_postac=?");
            delete_st.setInt(1,id);
            delete_st.setInt(2,heroid);
            SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);
            SessionController.getInstance().dbaseconnector.closeConnection();

            heroItems.getChildren().clear();
            showItems(heroid);

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

    public void initialize() {

        
        //System.out.println("initialising...");
        buildHeroList();

        inputs = new TextField [13];
        inputs[0] = heroStats_nameInput;
        inputs[1] = heroStats_levelInput;
        inputs[2] = heroStats_classInput;
        inputs[3] = heroStats_charInput;
        inputs[4] = heroStats_acInput;
        inputs[5] = heroStats_pwmaxInput;
        inputs[6] = heroStats_siaInput;
        inputs[7] = heroStats_zrcznoInput;
        inputs[8] = heroStats_mdroInput;
        inputs[9] = heroStats_inteligencjaInput;
        inputs[10] = heroStats_charyzmaInput;
        inputs[11] = heroStats_kondycjaInput;
        inputs[12] = heroStats_slotsInput;

        heroDesc.setVisible(false);

        Button addNewHero = new Button();
        addNewHero.setText("Dodaj nowego herosa");
        addNewHero.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.switchToAddMode();
        });
        utilityButtons.getChildren().add(addNewHero);


        modifyBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.modifyValues();
        });

        removeHeroBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.removeHero(heroid);
        });


        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.rasa ORDER BY id_rasa";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            while(rs.next()){
                int id_rasa = rs.getInt("id_rasa");
                String name_rasa = rs.getString("nazwa_rasa");
                heroStats_raceInput.getItems().add(name_rasa);
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

    }

}
