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

public class AdminController implements ViewOnloadEvent {

    

    @FXML
    VBox stats;

    @FXML
    Button showPeople;

    @FXML
    Button showSessions;

    @FXML
    Button showPlaces;

    @FXML
    Button showGroups;

    @FXML
    VBox workingPanel;

    @FXML
    Button newPlace;

    @FXML
    Button newUser;

    @FXML
    Button newGroup;

    @FXML
    Button showLogs;

    Label workingPanelTitle;
    Label workingPanelLabel1;
    TextField workingPanelTF1;
    Label workingPanelLabel2;
    TextField workingPanelTF2;
    Label workingPanelLabel3;
    TextField workingPanelTF3;

    Button workingPanelAdd;


    private void listLogs(){
        stats.getChildren().clear();
        try{

            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.log;";

            boolean anyReturned = false;

            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            while(rs.next()){

                anyReturned = true;

                Label l = new Label();
                l.setText("czas: " + rs.getTime("_time")+",data: "+rs.getDate("_date")+",typ operacji: "+rs.getString("typ_akcji"));
                stats.getChildren().add(l);
            }

            if(!anyReturned){
                Label l = new Label();
                l.setText("Brak zarejestrowanej aktywnosci ;)");
                stats.getChildren().add(l);
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

    private void buildPeopleList(){
        stats.getChildren().clear();
        try{

            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.czlowiek;";

            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            while(rs.next()){

                int user_id = rs.getInt("id_czlowiek");
                Label l = new Label();
                l.setText("id: "+user_id+" | nick: " + rs.getString("nick"));
                stats.getChildren().add(l);
                Button b = new Button("Edytuj uprawnienia");

                b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.changeAccessScreen(user_id);
                });
                stats.getChildren().add(b);

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

    void createAddPlaceScreen(){
        workingPanel.getChildren().clear();
        workingPanelTitle = new Label("Dodawanie nowego miejsca");
        workingPanel.getChildren().add(workingPanelTitle);
        workingPanelLabel1 = new Label("Wpisz nazwe:");
        workingPanel.getChildren().add(workingPanelLabel1);
        workingPanelTF1 = new TextField();
        workingPanel.getChildren().add(workingPanelTF1);
        workingPanelLabel2 = new Label("Wpisz adres:");
        workingPanel.getChildren().add(workingPanelLabel2);
        workingPanelTF2 = new TextField();
        workingPanel.getChildren().add(workingPanelTF2);

        workingPanelAdd = new Button("Dodaj miejsce");
        workingPanel.getChildren().add(workingPanelAdd);

        workingPanelAdd.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addPlace();
        });

    }

    void addPlace(){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.miejsce (nazwa_miejsca,adres) values (?,?)");

            insert_st.setString(1,workingPanelTF1.getText());
            insert_st.setString(2,workingPanelTF2.getText());
            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();
            //racePanel.getChildren().clear();
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

    private void changeAccessScreen(int user_id){
        workingPanel.getChildren().clear();
        try{
            SessionController.getInstance().dbaseconnector.openConnection();
            SessionController.getInstance().dbaseconnector.setNoFlushMode();

            String query = "SELECT * FROM projekt.uprawnienia";

            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            Label l = new Label("Modyfikacja uprawnien uzytkownika");
            workingPanel.getChildren().add(l);
            
            while(rs.next()){

                int acc_id = rs.getInt("id");

                String query2 = "SELECT * FROM projekt.czlowiek_uprawnienia cu JOIN projekt.uprawnienia u USING(id) WHERE id_czlowiek = "+user_id+"and id="+acc_id+";";

                ResultSet rs2 = SessionController.getInstance().dbaseconnector.executeQuery(query2);

                Button b = new Button();

                if(rs2.next()){
                    b.setText("Usun uprawnienie "+rs.getString("nazwa"));
                    b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                        this.removeAccess(user_id,acc_id);
                    });
                }else{
                    b.setText("Dodaj uprawnienie "+rs.getString("nazwa"));
                    b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                        this.addAcc(user_id,acc_id);
                    });
                }

                workingPanel.getChildren().add(b);

            }

            SessionController.getInstance().dbaseconnector.setAutoFlushMode();            
            SessionController.getInstance().dbaseconnector.closeConnection();

            //workingPanel.getChildren().clear();
           // worldPanel.getChildren().clear();
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

    private void removeAccess(int user_id,int acc_id){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.czlowiek_uprawnienia WHERE id_czlowiek = ? and id = ?");

            delete_st.setInt(1,user_id);
            delete_st.setInt(2,acc_id);
            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();
            System.out.println("Hero updated, "+i);

            workingPanel.getChildren().clear();
            buildPeopleList();
           // worldPanel.getChildren().clear();
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

    private void addAcc(int user_id,int acc_id){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.czlowiek_uprawnienia (id_czlowiek,id) values (?,?)");

            insert_st.setInt(1,user_id);
            insert_st.setInt(2,acc_id);
            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();
            System.out.println("Hero updated, "+i);

            workingPanel.getChildren().clear();
            buidlPlacesList();
           // worldPanel.getChildren().clear();
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

    private void addWorld(){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.swiat (nazwa,krotki_opis) values (?,?)");

            insert_st.setString(1,workingPanelTF1.getText());
            insert_st.setString(2,workingPanelTF2.getText());
            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();
            System.out.println("Hero updated, "+i);

            workingPanel.getChildren().clear();
            buidlPlacesList();
           // worldPanel.getChildren().clear();
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



    private void buildSessionList(){
        stats.getChildren().clear();
        try{

            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.sesja s join projekt.czlowiek c on s.id_czlowiek_mg = c.id_czlowiek;";

            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            while(rs.next()){

                Label l = new Label();
                l.setText("id: "+rs.getString("id_sesja")+" | nick MG: " + rs.getString("nick"));
                stats.getChildren().add(l);
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

    private void buidlPlacesList(){
        stats.getChildren().clear();
        try{

            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.miejsce;";

            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            while(rs.next()){

                Label l = new Label();
                l.setText("id: "+rs.getString("id_miejsca")+" | nazwa: " + rs.getString("nazwa_miejsca")+ " | adres: " +rs.getString("adres"));
                stats.getChildren().add(l);
                Button b = new Button("skasuj");
                
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

    

    private void addUserScreen(){
        workingPanel.getChildren().clear();
        workingPanelTitle = new Label("Dodawanie nowego uzytkownika");
        workingPanel.getChildren().add(workingPanelTitle);
        workingPanelLabel1 = new Label("Wpisz nick:");
        workingPanel.getChildren().add(workingPanelLabel1);
        workingPanelTF1 = new TextField();
        workingPanel.getChildren().add(workingPanelTF1);
        workingPanelLabel2 = new Label("Wpisz haslo (potem uzytkownik moze je zmienic):");
        workingPanel.getChildren().add(workingPanelLabel2);
        workingPanelTF2 = new TextField();
        workingPanel.getChildren().add(workingPanelTF2);

        workingPanelAdd = new Button("Dodaj uzytkownika");
        workingPanel.getChildren().add(workingPanelAdd);

        workingPanelAdd.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addUser();
        });
    }    
    
    private void addUser(){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.czlowiek (imie,nick) values (?,?)");

            insert_st.setString(1,"brak");
            insert_st.setString(2,workingPanelTF1.getText());
            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();
            buidlPlacesList();
           // worldPanel.getChildren().clear();
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

    private void removeUser(int id){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.czlowiek WHERE id_czlowiek=?");

            insert_st.setInt(1,id);
            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();
            buildPeopleList();
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

    private void buildGroupList(){
        stats.getChildren().clear();
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            SessionController.getInstance().dbaseconnector.setNoFlushMode();

            String query = "SELECT * FROM projekt.grupa";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);      

            while(rs.next()){
                Label l = new Label("Grupa: "+rs.getString("nazwa"));
                stats.getChildren().add(l);
                String query2 = "SELECT * FROM projekt.gracz_grupa g JOIN projekt.czlowiek c USING(id_czlowiek) WHERE id_grupa="+rs.getInt("id_grupa")+";";
                ResultSet rs2 = SessionController.getInstance().dbaseconnector.executeQuery(query2);
                while(rs2.next()){
                    Label l2 = new Label(rs2.getString("nick"));
                    stats.getChildren().add(l2);
                    Button b2 = new Button("Usun z grupy");
                    stats.getChildren().add(b2);
                    int id1 = rs.getInt("id_grupa");
                    int id2 = rs2.getInt("id_czlowiek");
                    b2.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                        this.removeFromGroup(id1,id2);
                    });
                }
                Button b = new Button("Dodaj do grupy...");
                int tmpid = rs.getInt("id_grupa");
                b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.addToGroupScreen(tmpid);
                });
                stats.getChildren().add(b);

                b = new Button("Usun grupe");
                int tmpid2 = rs.getInt("id_grupa");
                b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.removeGroup(tmpid2);
                });
                stats.getChildren().add(b);

            }

            SessionController.getInstance().dbaseconnector.flushStatement();
            SessionController.getInstance().dbaseconnector.setAutoFlushMode();
        
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();

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

    private void removeGroup(int id_group){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.grupa WHERE id_grupa=?");

            delete_st.setInt(1,id_group);


            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();
            buildGroupList();
           // worldPanel.getChildren().clear();
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


    private void removeFromGroup(int id_group, int id_user){
        //System.out.println("REMOVING");
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement delete_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.gracz_grupa WHERE id_grupa=? AND id_czlowiek=?");

            delete_st.setInt(1,id_group);
            delete_st.setInt(2,id_user);

            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(delete_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();
            buildGroupList();
           // worldPanel.getChildren().clear();
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

    private void addToGroupScreen(int id_group){
        workingPanel.getChildren().clear();
        workingPanelTitle = new Label("Dodawanie nowego uzytkownika do grupy");
        workingPanel.getChildren().add(workingPanelTitle);
        workingPanelLabel1 = new Label("Wpisz nick:");
        workingPanel.getChildren().add(workingPanelLabel1);
        workingPanelTF1 = new TextField();
        workingPanel.getChildren().add(workingPanelTF1);

        workingPanelAdd = new Button("Dodaj uzytkownika");
        workingPanel.getChildren().add(workingPanelAdd);

        workingPanelAdd.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addUserToGroup(id_group);
        });
    }

    private void addUserToGroup(int id_group){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.czlowiek WHERE nick = '"+workingPanelTF1.getText()+"';";
            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);

            int user_id = -1;
            if(rs.next()){
                user_id = rs.getInt("id_czlowiek");
            }else{
                throw new SQLException("No such user!");
            }

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.gracz_grupa (id_grupa,id_czlowiek) values (?,?)");

            insert_st.setInt(1,id_group);
            insert_st.setInt(2,user_id);

            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();
            buildGroupList();
           // worldPanel.getChildren().clear();
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

    private void addGroupScreen(){
        workingPanel.getChildren().clear();
        workingPanelTitle = new Label("Dodawanie nowej grupy");
        workingPanel.getChildren().add(workingPanelTitle);
        workingPanelLabel1 = new Label("Wpisz nazwe:");
        workingPanel.getChildren().add(workingPanelLabel1);
        workingPanelTF1 = new TextField();
        workingPanel.getChildren().add(workingPanelTF1);

        workingPanelAdd = new Button("Dodaj grupe");
        workingPanel.getChildren().add(workingPanelAdd);

        workingPanelAdd.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addGroup();
        });
    }

    private void addGroup(){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.grupa (nazwa) values (?)");

            insert_st.setString(1,workingPanelTF1.getText());

            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();
            buildGroupList();
           // worldPanel.getChildren().clear();
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


    public void initialize() {
        showPeople.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.buildPeopleList();
        });
        showSessions.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.buildSessionList();
        });
        showPlaces.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.buidlPlacesList();
        });
        showGroups.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.buildGroupList();
        });
        newPlace.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.createAddPlaceScreen();
        });
        newUser.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addUserScreen();
        });
        newGroup.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addGroupScreen();
        });
        showLogs.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.listLogs();
        });
    }


    
}
