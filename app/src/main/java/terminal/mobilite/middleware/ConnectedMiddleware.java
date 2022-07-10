package terminal.mobilite.middleware;

import android.app.Activity;
import android.content.Intent;
import terminal.mobilite.activity.operational.IdentificationActivity;
import terminal.mobilite.manager.ConnectionManager;

/**
 * Middleware permettant de rediriger l'application sur l'ecran d'identification
 * si l'utilisateur n'est pas connecté
 */
public class ConnectedMiddleware implements TerminalMiddleware{

    public void verify_and_redirect(Activity activity){
        ConnectionManager cm = ConnectionManager.getInstance();
        boolean isConnected = cm.isConnected();
        if(!isConnected){
            activity.finish();
            Intent intent = new Intent(activity, IdentificationActivity.class);
            activity.startActivity(intent);
        }
    }
}
