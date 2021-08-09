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

public class WBController implements ViewOnloadEvent {

    @FXML
    Button addRaceButton;

    @FXML
    Button addWorldButton;

    @FXML
    VBox racePanel;

    @FXML
    VBox worldPanel;

    @FXML
    VBox workingPanel;

    Label workingPanelTitle;
    Label workingPanelLabel1;
    TextField workingPanelTF1;
    Label workingPanelLabel2;
    TextField workingPanelTF2;
    Label workingPanelLabel3;
    TextField workingPanelTF3;  

    Button workingPanelAdd;

    private void buildWorldsPanel(){
        worldPanel.getChildren().clear();
        try{

            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.swiat;";

            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            while(rs.next()){

                HBox h = new HBox();

                Label l = new Label();
                l.setText("id: "+rs.getString("id_swiat")+" | nazwa: " + rs.getString("nazwa")+" | opis: "+rs.getString("krotki_opis"));
                h.getChildren().add(l);

                Button b = new Button();
                b.setText("skasuj");
                h.getChildren().add(b);

                int id_world = rs.getInt("id_swiat");

                b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.removeWorld(id_world);
                });

                Button be = new Button();
                be.setText("edytuj");
                h.getChildren().add(be);
                String opis = rs.getString("krotki_opis");

                be.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.prepareWorldEditScreen(id_world,opis);
                });

                worldPanel.getChildren().add(h);
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
    
    private void buildRacesPanel(){
        racePanel.getChildren().clear();
        try{

            SessionController.getInstance().dbaseconnector.openConnection();

            String query = "SELECT * FROM projekt.rasa;";

            ResultSet rs = SessionController.getInstance().dbaseconnector.executeQuery(query);
            while(rs.next()){

                HBox h = new HBox();

                Label l = new Label();
                l.setText("id: "+rs.getString("id_rasa")+" | nazwa: " + rs.getString("nazwa_rasa")+" | cechy: "+rs.getString("cechy")+" | opis: "+rs.getString("opis"));
                h.getChildren().add(l);

                Button b = new Button();
                b.setText("skasuj");
                h.getChildren().add(b);

                int id_race = rs.getInt("id_rasa");

                b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.removeRace(id_race);
                });

                Button be = new Button();
                be.setText("edytuj");
                h.getChildren().add(be);

                String cechy = rs.getString("cechy");
                String opis = rs.getString("opis");

                be.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    this.prepareRaceEditScreen(id_race,cechy,opis);
                });

                racePanel.getChildren().add(h);
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

    private void prepareWorldAddScreen(){
        workingPanel.getChildren().clear();
        workingPanelTitle = new Label("Dodawanie nowego swiata");
        workingPanel.getChildren().add(workingPanelTitle);
        workingPanelLabel1 = new Label("Wpisz nazwe:");
        workingPanel.getChildren().add(workingPanelLabel1);
        workingPanelTF1 = new TextField();
        workingPanel.getChildren().add(workingPanelTF1);
        workingPanelLabel2 = new Label("Wpisz opis swiata:");
        workingPanel.getChildren().add(workingPanelLabel2);
        workingPanelTF2 = new TextField();
        workingPanel.getChildren().add(workingPanelTF2);
    
        workingPanelAdd = new Button("Dodaj swiat");
        workingPanel.getChildren().add(workingPanelAdd);

        workingPanelAdd.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addWorld();
        });
    }

    private void prepareRaceAddScreen(){
        workingPanel.getChildren().clear();
        workingPanelTitle = new Label("Dodawanie nowej rasy");
        workingPanel.getChildren().add(workingPanelTitle);
        workingPanelLabel1 = new Label("Wpisz nazwe:");
        workingPanel.getChildren().add(workingPanelLabel1);
        workingPanelTF1 = new TextField();
        workingPanel.getChildren().add(workingPanelTF1);
        workingPanelLabel2 = new Label("Wpisz cechy:");
        workingPanel.getChildren().add(workingPanelLabel2);
        workingPanelTF2 = new TextField();
        workingPanel.getChildren().add(workingPanelTF2);
        workingPanelLabel3 = new Label("Wpisz krotki opis:");
        workingPanel.getChildren().add(workingPanelLabel3);
        workingPanelTF3 = new TextField();
        workingPanel.getChildren().add(workingPanelTF3);
    
        workingPanelAdd = new Button("Dodaj rase");
        workingPanel.getChildren().add(workingPanelAdd);

        workingPanelAdd.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.addRace();
        });
    }

    private void prepareRaceEditScreen(int id, String cechy, String opis){

        workingPanel.getChildren().clear();


        workingPanelLabel2 = new Label("Edytuj cechy:");
        workingPanel.getChildren().add(workingPanelLabel2);
        workingPanelTF2 = new TextField();
        workingPanelTF2.setText(cechy);
        workingPanel.getChildren().add(workingPanelTF2);
        workingPanelLabel3 = new Label("Edytuj krotki opis:");
        workingPanel.getChildren().add(workingPanelLabel3);
        workingPanelTF3 = new TextField();
        workingPanelTF3.setText(opis);
        workingPanel.getChildren().add(workingPanelTF3);
    
        workingPanelAdd = new Button("Zapisz zmiany");
        workingPanel.getChildren().add(workingPanelAdd);

        workingPanelAdd.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.editRace(id);
        });

    }

    private void editRace(int id){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement update = SessionController.getInstance().dbaseconnector.getPreparedStatement("UPDATE projekt.rasa SET cechy = ?, opis = ? WHERE id_rasa = ?");

            update.setString(1,workingPanelTF2.getText());
            update.setString(2,workingPanelTF3.getText());
            update.setInt(3,id);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(update);            
            SessionController.getInstance().dbaseconnector.closeConnection();
            workingPanel.getChildren().clear();
            buildRacesPanel();
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

    private void prepareWorldEditScreen(int id, String opis){

        workingPanel.getChildren().clear();


        workingPanelLabel2 = new Label("Edytuj opis:");
        workingPanel.getChildren().add(workingPanelLabel2);
        workingPanelTF2 = new TextField();
        workingPanelTF2.setText(opis);
        workingPanel.getChildren().add(workingPanelTF2);
    
        workingPanelAdd = new Button("Zapisz zmiany");
        workingPanel.getChildren().add(workingPanelAdd);

        workingPanelAdd.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.editWorld(id);
        });
    }

    private void editWorld(int id){

        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement update = SessionController.getInstance().dbaseconnector.getPreparedStatement("UPDATE projekt.swiat SET krotki_opis = ? WHERE id_swiat = ?");

            update.setString(1,workingPanelTF2.getText());
            update.setInt(2,id);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(update);            
            SessionController.getInstance().dbaseconnector.closeConnection();
            workingPanel.getChildren().clear();
            buildWorldsPanel();
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
           // worldPanel.getChildren().clear();
            buildWorldsPanel();
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

    private void addRace(){
        try{


            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("INSERT INTO projekt.rasa (nazwa_rasa,cechy, opis) values (?,?,?)");

            insert_st.setString(1,workingPanelTF1.getText());
            insert_st.setString(2,workingPanelTF2.getText());
            insert_st.setString(3,workingPanelTF3.getText());
            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();
            //racePanel.getChildren().clear();
            buildRacesPanel();
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

    private void removeWorld(int id){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.swiat WHERE id_swiat=?");

            insert_st.setInt(1,id);
            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();
            buildWorldsPanel();
        }
        catch(SQLException es){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Uwaga!");
            alert.setHeaderText("Usuwanie swiata sie nie powiodlo.");
            alert.setContentText("Sprawdz, czy w swiecie nie ma zadnych kampanii.");

            alert.showAndWait();

            System.out.println("SQL Exception: "+es);
            es.printStackTrace();
        }
        catch (Exception e){
            System.out.println("Error: "+e);
            e.printStackTrace();
        }
    }

    private void removeRace(int id){
        try{
            SessionController.getInstance().dbaseconnector.openConnection();

            PreparedStatement insert_st = SessionController.getInstance().dbaseconnector.getPreparedStatement("DELETE FROM projekt.rasa WHERE id_rasa=?");

            insert_st.setInt(1,id);
            //System.out.println("INSERT:");
            //System.out.println(insert_st);
            int i = SessionController.getInstance().dbaseconnector.executePreparedStatement(insert_st);            
            SessionController.getInstance().dbaseconnector.closeConnection();

            workingPanel.getChildren().clear();
            buildRacesPanel();
        }
        catch(SQLException es){
            System.out.println("SQL Exception: "+es);
            es.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Uwaga!");
            alert.setHeaderText("Usuwanie rasy sie nie powiodlo.");
            alert.setContentText("Sprawdz, czy zadne PC nie naleza do tej rasy.");
        }
        catch (Exception e){
            System.out.println("Error: "+e);
            e.printStackTrace();
        }
    }


    public void initialize() {
        buildWorldsPanel();
        buildRacesPanel();
        
        addRaceButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.prepareRaceAddScreen();
        });

        
        addWorldButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.prepareWorldAddScreen();
        });

    }
}
