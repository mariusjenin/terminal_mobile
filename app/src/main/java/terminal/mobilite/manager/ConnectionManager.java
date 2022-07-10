package terminal.mobilite.manager;

import android.app.Activity;
import android.content.SharedPreferences;
import terminal.mobilite.model.user.Utilisateur;

/**
 * Gère la connexion d'un utilisateur à l'application mobile
 */
public class ConnectionManager {
    private final static String TOKEN_PREF = "TOKEN_PREF";
    private final static String NAME_SHARED_PREFERENCE_IDENTIFICATION = "IDENTIFICATION";

    private Utilisateur utilisateur;
    private boolean connected;
    private static ConnectionManager INSTANCE;

    private ConnectionManager() {
        utilisateur = null;
        connected = false;
    }

    public static ConnectionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionManager();
        }
        return INSTANCE;
    }

    /**
     * Connects the user
     *
     * @param u Utilisateur
     */
    public void setConnected(Utilisateur u) {
        utilisateur = u;
        connected = true;
    }

    /**
     * Getter of connected
     *
     * @return if the user is conencted
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Getter of utilisateur
     *
     * @return utilisateur
     */
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    /**
     * Disconnects the user
     */
    public void disconnect() {
        utilisateur = null;
        connected = false;
    }

    /**
     * Will get the user stored in SharePreferences and connect him
     * @param act current activity
     * @return success of the action
     */
    public String getTokenUserFromPrefs(Activity act){

        SharedPreferences preferences = act.getSharedPreferences(NAME_SHARED_PREFERENCE_IDENTIFICATION,Activity.MODE_PRIVATE);

        return preferences.getString(ConnectionManager.TOKEN_PREF, null);
    }

    /**
     * Will store the user connected to be able to retreive it later (stored in SharePreferences in private mode)
     * @param act current activity
     */
    public void storeTokenUserInPrefs(Activity act){
        SharedPreferences preferences = act.getSharedPreferences(NAME_SHARED_PREFERENCE_IDENTIFICATION,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Utilisateur user = getUtilisateur();
        if(user != null){
            editor.putString(ConnectionManager.TOKEN_PREF, user.getToken());
            editor.apply();
        }
    }

    /**
     * Remove the user stored in SharedPreferences
     * @param act current activity
     */
    public void removeTokenUserFromPrefs(Activity act){
        SharedPreferences preferences = act.getSharedPreferences(NAME_SHARED_PREFERENCE_IDENTIFICATION,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(ConnectionManager.TOKEN_PREF);
        editor.apply();
    }
}
