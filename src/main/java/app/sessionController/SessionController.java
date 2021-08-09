package app.sessionController;

import app.psqlModule.PsqlModule;
import app.viewTool.*;

public class SessionController{

    private static SessionController instance = null;

    public boolean adminAcc = false;
    public boolean playerAcc = false;
    public boolean wbAcc = false;
    public boolean mgAcc = false;

    public int user_id = -1;

    public void setAcc(boolean adm, boolean pl, boolean mg, boolean wb){
        adminAcc = adm;
        playerAcc = pl;
        wbAcc = wb;
        mgAcc = mg;
    }

    public static SessionController getInstance(){
        if(instance==null) instance = new SessionController();
        return instance;
    }

    public static void resetSession(){
		instance = null;
    }
    
    private SessionController(){

    }

    public PsqlModule dbaseconnector;

    public static void logout(){
        ViewManager.setScene(Views.Login);
        getInstance().adminAcc = false;
        getInstance().playerAcc = false;
        getInstance().wbAcc = false;
        getInstance().mgAcc = false;
        getInstance().user_id = -1;
    }

}