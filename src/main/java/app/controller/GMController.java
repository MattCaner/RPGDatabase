package app.controller;

import java.sql.Connection;
import java.sql.DriverManager;
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
import javafx.scene.control.Alert;

import javafx.scene.Node;

public class GMController implements ViewOnloadEvent {

    int active_campaign_id = 0;
    int active_session_id = 0;

    @FXML
    VBox campaignPanelButtons;
    @FXML
    VBox sessionPanelButtons;
    @FXML
    Button addNewCampaignBtn;
    @FXML
    Button addNewItemBtn;
    @FXML
    VBox workingPanel;
    @FXML
    Button readItemsBtn;

    Button newSession;

    Label addCampaign_name;
    TextField addCampaign_name_TF;
    Label addCampaign_world;
    TextField addCampaign_world_TF;
    Button addCampaign;

    Label addSession_info;
    Label addSession_place;
    TextField addSession_place_TF;
    Label addSession_player;
    VBox addSession_playersBox;
    TextField addSession_player_TF;
    Button addSession_player_btn;
    Button addSessionBtn;
    Button removeCampaignBtn;

    Label newItemDesc;
    Label newItemName;
    TextField newItemNameTF;
    Label newItemType;
    TextField newItemTypeTF;
    Label newItemRarity;
    TextField newItemRarityTF;
    Label newItemValue;
    TextField newItemValueTF;
    Label newItemEffect;
    TextField newItemEffectTF;

    Button acceptNewItem;

    Label givePlayerInfo;
    Label givePlayerNumInfo;

    TextField givePlayerTF;
    TextField givePlayerNumTF;

    Button givePlayerBtn;


    Label sessionInfo;
    VBox sessionPlayers;

    public void buildCampaignList(){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.kampania WHERE id_czlowiek_autor = '" + SessionController.getInstance().user_id + "';";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);



            while(rs.next()){
               // System.out.println("checking record...");
                String name = rs.getString("nazwa");
                int id = rs.getInt("id_kampania");

                Button btn = new Button();
                btn.setText(name);
                btn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    System.out.println("id="+id);
                    this.buildSessionList(id);
                });
                campaignPanelButtons.getChildren().add(btn);
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

    public void removeSession(int sessionId){
        
        try{

            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement delete_st;

            delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.udzial_w_sesji WHERE id_sesja = ?;");
            delete_st.setInt(1,sessionId);

            SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);                 


            delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.sesja WHERE id_sesja = ?;");
            delete_st.setInt(1,sessionId);
            SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);     

            SessionController.getInstance().dbaseconnector.closeConnection();

            buildSessionList(active_campaign_id);

            workingPanel.getChildren().clear();
            
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

    public void buildSessionList(int campaignId){
        sessionPanelButtons.getChildren().clear();
        active_campaign_id = campaignId;
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            int id = active_campaign_id;
            String query = "SELECT * FROM projekt.sesja WHERE id_kampania = " + campaignId + ";";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            Label l = new Label("ID kampanii: "+id);
            sessionPanelButtons.getChildren().add(l);
            
            while(rs.next()){
                System.out.println("checking record...");
                //String name = rs.getString("nazwa");
                id = rs.getInt("id_sesja");
                String name = "sesja id " + id;

                Button btn = new Button();
                btn.setText(name);
                final int id_showSession = id;
                btn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.showSession(id_showSession);
                });
                sessionPanelButtons.getChildren().add(btn);
            }
            
            SessionController.getInstance().dbaseconnector.closeConnection();

            newSession = new Button();
            newSession.setText("Dodaj sesje...");
            System.out.println("ACI: "+active_campaign_id);
            newSession.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                this.createSessionScreen();
            });

            removeCampaignBtn = new Button();
            removeCampaignBtn.setText("Skasuj wybrana kampanie");
            sessionPanelButtons.getChildren().add(removeCampaignBtn);
            removeCampaignBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                this.removeActiveCampaign();
            });


            sessionPanelButtons.getChildren().add(newSession);
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

    public void removeActiveCampaign(){

        System.out.println("deleting...");
        try{

            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.sesja WHERE id_kampania="+active_campaign_id+";";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            PreparedStatement delete_st;

            while(rs.next()){
                delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.udzial_w_sesji WHERE id_sesja = ?;");
                delete_st.setInt(1,rs.getInt("id_sesja"));
                SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);
            }

            delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.sesja WHERE id_kampania = ?;");
            delete_st.setInt(1,active_campaign_id);
            SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);


            delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.kampania WHERE id_kampania = ?;");
            delete_st.setInt(1,active_campaign_id);
            SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);        

            SessionController.getInstance().dbaseconnector.closeConnection();

            campaignPanelButtons.getChildren().clear();
            buildCampaignList();
            sessionPanelButtons.getChildren().clear();
            active_campaign_id = -1;
            
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

    public void showEmpty(){
        workingPanel.getChildren().clear();
    }

    public void showSession(int sessionId){
        workingPanel.getChildren().clear();

        sessionInfo = new Label();
        sessionInfo.setText("Informacje o sesji nr "+sessionId);
        workingPanel.getChildren().add(sessionInfo);

        sessionPlayers = new VBox();
        workingPanel.getChildren().add(sessionPlayers);

        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.udzial_w_sesji us JOIN projekt.pc p using(id_postac) WHERE id_sesja = " + sessionId + ";";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            while(rs.next()){
                Label l = new Label();
                l.setText("Gracz: " + rs.getString("imie") + " | " + rs.getString("klasa") + " " + rs.getString("poziom")+" poziomu");
                sessionPlayers.getChildren().add(l);
            }

            Button b = new Button("Usun sesje");
            workingPanel.getChildren().add(b);
            b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                this.removeSession(sessionId);
            });
            
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

    public void createCampaignScreen(){
        workingPanel.getChildren().clear();

        addCampaign_name = new Label();
        addCampaign_name.setText("Wpisz nazwe kampanii:");
        workingPanel.getChildren().add(addCampaign_name);

        addCampaign_name_TF = new TextField();
        workingPanel.getChildren().add(addCampaign_name_TF);

        addCampaign_world = new Label();
        addCampaign_world.setText("Wpisz nazwe swiata:");
        workingPanel.getChildren().add(addCampaign_world);

        addCampaign_world_TF = new TextField();
        workingPanel.getChildren().add(addCampaign_world_TF);

        addCampaign = new Button();
        addCampaign.setText("Dodaj kampanie");
        workingPanel.getChildren().add(addCampaign);

        addCampaign.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addCampaign();
        });
    }

    public void addCampaign(){

        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            String s = addCampaign_world_TF.getText();

            String query = "SELECT * FROM projekt.swiat WHERE nazwa = '" + s + "';";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            int world_id = 0;
            if(rs.next()){
                world_id = rs.getInt("id_swiat");
            }
            else throw new SQLException("Such world does not exist");

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.kampania (nazwa,id_swiat,id_czlowiek_autor) VALUES (?,?,?);");

            insert_st.setString(1,addCampaign_name_TF.getText());
            insert_st.setInt(2,world_id);
            insert_st.setInt(3,SessionController.getInstance().user_id);

            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);    
            insert_st.close();        
            SessionController.getInstance().dbaseconnector.closeConnection();



            System.out.println("Campaign inserted, "+i);

            showEmpty();
            campaignPanelButtons.getChildren().clear();
            buildCampaignList();

            //buildSessionList(active_campaign_id);

        }
        catch (SQLException es){
            //status.setText("Problem SQLa: "+es);
            System.out.println("SQL Exception: "+es);
            es.printStackTrace();
        }
        catch (Exception e){
            //status.setText("Dodawanie bohatera nie powiodlo sie!");
            System.out.println("Error: "+e);
            e.printStackTrace();
        }
    }


    public void addPlayerToSessionScreen(){
        if(addSession_player_TF.getText().equals("")) return;
        String name = addSession_player_TF.getText();
        //System.out.println("name: "+name);
        Label l = new Label();
        l.setText(name);
        addSession_playersBox.getChildren().add(l);
    }

    public void createSessionScreen(){



        workingPanel.getChildren().clear();

        addSession_info = new Label();
        addSession_info.setText("Dodawanie nowej sesji");
        workingPanel.getChildren().add(addSession_info);

        addSession_place = new Label();
        addSession_place.setText("wpisz miejsce:");
        workingPanel.getChildren().add(addSession_place);

        addSession_place_TF = new TextField();
        workingPanel.getChildren().add(addSession_place_TF);


        addSession_player = new Label();
        addSession_player.setText("Wpisz imie postaci gracza i kliknij \"Dodaj gracza\"");
        workingPanel.getChildren().add(addSession_player);

        addSession_playersBox = new VBox();
        workingPanel.getChildren().add(addSession_playersBox);

        addSession_player_TF = new TextField();
        workingPanel.getChildren().add(addSession_player_TF);

        addSession_player_btn = new Button();
        addSession_player_btn.setText("Dodaj gracza");
        workingPanel.getChildren().add(addSession_player_btn);

        //System.out.println("name get: "+ addSession_player_TF.getText());

        addSession_player_btn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addPlayerToSessionScreen();
        });

        addSessionBtn = new Button();
        addSessionBtn.setText("Dodaj sesje");
        workingPanel.getChildren().add(addSessionBtn);



        addSessionBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addSession();
        });
    }

    public void addSession(){
        try{


            SessionController.getInstance().dbaseconnector.openConnection();

            String s = addSession_place_TF.getText();

            String query = "SELECT * FROM projekt.miejsce WHERE nazwa_miejsca = '" + s + "';";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            int place_id;

            if(rs.next()){
                place_id = rs.getInt("id_miejsca");
            }else{
                place_id = 1;
            }

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.sesja (id_czlowiek_mg,id_miejsca,id_kampania) VALUES (?,?,?);");
            

            insert_st.setInt(1,SessionController.getInstance().user_id);
            insert_st.setInt(2,place_id);
            insert_st.setInt(3,active_campaign_id);

            

            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);

            int id = 0;
   
            query = "SELECT * FROM projekt.sesja ORDER BY id_sesja DESC LIMIT 1";

            rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            if(rs.next()){
                System.out.println("id="+id);
                id = rs.getInt("id_sesja");
            }

            boolean done = true;

            for(Node n : addSession_playersBox.getChildren()){
                Label l = (Label)n;
                query = "SELECT * FROM projekt.pc WHERE imie = '" + l.getText() + "';";
                System.out.println("imie =" + l.getText());
                rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

                if(rs.next()){
                    System.out.println("dodawanie: "+l.getText());
                    System.out.println(""+l.getText()+" nie istnieje w bazie danych");
                    insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.udzial_w_sesji (id_postac,id_sesja) VALUES (?,?);");
                    System.out.println("id= " + id + "postac = "+rs.getInt("id_postac"));
                    insert_st.setInt(1,rs.getInt("id_postac"));
                    insert_st.setInt(2,id);
                    SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st); 
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Uwaga!");
                    alert.setHeaderText("PC nie istnieje:");
                    alert.setContentText("PC o imieniu: "+l.getText()+" nie istnieje!");

                    alert.showAndWait();
                    System.out.println(""+l.getText()+" nie istnieje w bazie danych");
                    addSession_playersBox.getChildren().remove(l);

                    done = false;
                }
            }
            insert_st.close();

            SessionController.getInstance().dbaseconnector.closeConnection();

            //System.out.println("Campaign inserted, "+i);

            buildSessionList(active_campaign_id);
            
            if(done) showEmpty();
        }
        catch (SQLException es){
            //status.setText("Problem SQLa: "+es);
            System.out.println("SQL Exception: "+es);
            es.printStackTrace();
        }
        catch (Exception e){
            //status.setText("Dodawanie bohatera nie powiodlo sie!");
            System.out.println("Error: "+e);
            e.printStackTrace();
        }
    }

    public void showItemList(){
        sessionPanelButtons.getChildren().clear();
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            
            String query = "SELECT * FROM projekt.przedmiot;";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            
            while(rs.next()){
                System.out.println("checking record...");

                int id = rs.getInt("id_przedmiotu");
                String name = rs.getString("nazwa_przedmiotu");

                Button btn = new Button();
                btn.setText(name);
                final int id_showSession = id;
                btn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.showSingleItem(id);
                });
                sessionPanelButtons.getChildren().add(btn);
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

    public void showSingleItem(int id){
        workingPanel.getChildren().clear();
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            
            String query = "SELECT * FROM projekt.przedmiot WHERE id_przedmiotu="+id+";";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            
            if(rs.next()){
                System.out.println("checking record...");

                //id = rs.getInt("id_przedmiotu");

                Label l = new Label("Nazwa przedmiotu: "+rs.getString("nazwa_przedmiotu"));
                workingPanel.getChildren().add(l);
                
                l = new Label("Typ: "+rs.getString("typ"));
                workingPanel.getChildren().add(l);

                l = new Label("Dzialanie: "+rs.getString("dziaanie"));
                workingPanel.getChildren().add(l);
                
                l = new Label("Rzadkosc: "+rs.getString("rzadkosc"));
                workingPanel.getChildren().add(l);

                l = new Label("wartosc: "+rs.getDouble("wartosc_sp"));
                workingPanel.getChildren().add(l);

                givePlayerInfo = new Label("Wpisz imie postaci, by dac jej przedmiot:");
                workingPanel.getChildren().add(givePlayerInfo);
                        
                givePlayerTF = new TextField();
                workingPanel.getChildren().add(givePlayerTF);




                Label givePlayerNumInfo = new Label("Wpisz ilosc przedmiotow tego typu, ktora chcesz dac postaci:");
                
                workingPanel.getChildren().add(givePlayerNumInfo);

                givePlayerNumTF = new TextField();

                workingPanel.getChildren().add(givePlayerNumTF);
        
                Button givePlayerBtn = new Button("Daj");
                workingPanel.getChildren().add(givePlayerBtn);

                givePlayerBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.giveItem(id);
                });

            
            }
            else{
                throw new Exception("Item od id "+id+" was not found. YES, THIS IS AN ERROR!");
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

    public void giveItem(int id){
        workingPanel.getChildren().clear();
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.pc WHERE imie = '" + givePlayerTF.getText() + "';";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            int id_player = 0;
            if(rs.next()){
                id_player = rs.getInt("id_postac");
                PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.przedmiot_postac (id_postac,id_przedmiotu,ilosc,umie_uzywac) VALUES (?,?,?,?);");

                insert_st.setInt(1,id_player);
                insert_st.setInt(2,id);
                insert_st.setInt(3,Integer.parseInt(givePlayerNumTF.getText()));
                insert_st.setBoolean(4,true);
                SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);
                System.out.println("Added successfully!");
            }  
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Uwaga!");
                alert.setHeaderText("PC nie istnieje:");
                alert.setContentText("PC o imieniu: "+givePlayerTF.getText()+" nie istnieje!");

                alert.showAndWait();
                System.out.println(""+givePlayerTF.getText()+" nie istnieje w bazie danych");
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

    public void createItemScreen(){

        newItemDesc = new Label("Tworzenie nowego przedmiotu:");
        workingPanel.getChildren().add(newItemDesc);

        newItemName = new Label("Nazwa:");
        workingPanel.getChildren().add(newItemName);


        newItemNameTF = new TextField();
        workingPanel.getChildren().add(newItemNameTF);

        newItemType = new Label ("Typ:");
        workingPanel.getChildren().add(newItemType);

        newItemTypeTF = new TextField();
        workingPanel.getChildren().add(newItemTypeTF);

        newItemEffect = new Label ("Efekt:");
        workingPanel.getChildren().add(newItemEffect);

        newItemEffectTF = new TextField();
        workingPanel.getChildren().add(newItemEffectTF);

        newItemRarity = new Label ("Rzadkosc:");
        workingPanel.getChildren().add(newItemRarity);

        newItemRarityTF = new TextField();
        workingPanel.getChildren().add(newItemRarityTF);

        newItemValue = new Label ("Cena:");
        workingPanel.getChildren().add(newItemValue);

        newItemValueTF = new TextField();
        workingPanel.getChildren().add(newItemValueTF);

        Button acceptNewItem = new Button("Dodaj");
        workingPanel.getChildren().add(acceptNewItem);


        acceptNewItem.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addNewItem();
        });

    }

    public void addNewItem(){
        workingPanel.getChildren().clear();
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.przedmiot (nazwa_przedmiotu,typ,dziaanie,rzadkosc,wartosc_sp) VALUES (?,?,?,?,?);");

            insert_st.setString(1,newItemNameTF.getText());
            insert_st.setString(2,newItemTypeTF.getText());
            insert_st.setString(3,newItemEffectTF.getText());
            insert_st.setString(4,newItemRarityTF.getText());
            insert_st.setDouble(5,Double.parseDouble(newItemValueTF.getText()));


            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);    
            
            insert_st.close();        
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



    public void initialize() {

        buildCampaignList();

        addNewItemBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            this.createItemScreen();
        });


        addNewCampaignBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.createCampaignScreen();
        });

        

        readItemsBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.showItemList();
        });


    }
}
