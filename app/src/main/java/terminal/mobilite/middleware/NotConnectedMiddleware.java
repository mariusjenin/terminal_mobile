package terminal.mobilite.middleware;

import android.app.Activity;
import android.content.Intent;
import terminal.mobilite.activity.operational.HomeActivity;
import terminal.mobilite.manager.ConnectionManager;

/**
 * Middleware permettant de rediriger l'application sur l'ecran d'accueil
 * si l'utilisateur est connecté
 */
public class NotConnectedMiddleware implements TerminalMiddleware {

    public void verify_and_redirect(Activity activity){
        ConnectionManager cm = ConnectionManager.getInstance();
        boolean isConnected = cm.isConnected();
        if(isConnected){
            activity.finish();
            Intent intent = new Intent(activity, HomeActivity.class);
            activity.startActivity(intent);
        }
    }
}
