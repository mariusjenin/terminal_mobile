package terminal.mobilite.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import terminal.mobilite.activity.R;
import terminal.mobilite.activity.TerminalActivity;
import terminal.mobilite.activity.operational.HomeActivity;
import terminal.mobilite.manager.ConnectionManager;
import terminal.mobilite.manager.HttpRequest;
import terminal.mobilite.manager.InternetConnectionManager;
import terminal.mobilite.model.user.Utilisateur;

import java.util.HashMap;

public class IdentificationController extends Controller {
    private static IdentificationController INSTANCE;

    private IdentificationController() {
    }

    public static IdentificationController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IdentificationController();
        }
        return INSTANCE;
    }

    /**
     * Registers or connects the user
     *
     * @param act            Activity
     * @param is_sign_up     boolean
     * @param email          String
     * @param pwd            String
     * @param pwd_confirm    String
     * @param stay_connected boolean
     */
    @SuppressLint("StaticFieldLeak")
    public void sign_in_sign_up(TerminalActivity act, boolean is_sign_up, String email, String pwd, String pwd_confirm, boolean stay_connected) {
        act.showProgress();
        if (InternetConnectionManager.checkConnection(act)) {
            new AsyncTask<Void, String, String>() {

                /**
                 * Launching the POST request to the server (signup or signin)
                 * @param voids no parameters
                 * @return the response of the request (normally json)
                 */
                @Override
                protected String doInBackground(Void... voids) {
                    String result;


                    HashMap<String, Object> params = new HashMap<>();
                    HashMap<String, Object> header = new HashMap<>();
                    params.put("email", email);
                    params.put("pwd", pwd);

                    //Take the right url (signin / signup)
                    String url_request;
                    if (is_sign_up) {
                        params.put("pwd_confirm", pwd_confirm);
                        url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/sign-up";
                    } else {
                        url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/sign-in";
                    }

                    //Doing the request
                    HttpRequest req = new HttpRequest(url_request, params, header);
                    result = req.post();

                    return result;
                }

                /**
                 * Processing of the response of the POST request (signin or signup)
                 * @param res response of the POST request
                 */
                @Override
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    onPostExecuteConnect(res, act, stay_connected);
                    act.hideProgress();
                }

            }.execute();
        } else {
            Toast.makeText(act, act.getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show();
            act.hideProgress();
        }
    }

    /**
     * connects only with the token the user
     *
     * @param act   Activity
     * @param token String
     */
    @SuppressLint("StaticFieldLeak")
    public void sign_in_auto(TerminalActivity act, String token) {
        act.showProgress();
        if (InternetConnectionManager.checkConnection(act)) {
            new AsyncTask<Void, String, String>() {

                /**
                 * Launching the POST request to the server (signup or signin)
                 * @param voids no parameters
                 * @return the response of the request (normally json)
                 */
                @Override
                protected String doInBackground(Void... voids) {
                    String result;


                    HashMap<String, Object> params = new HashMap<>();
                    HashMap<String, Object> header = new HashMap<>();
                    params.put("token", token);

                    //Take the right url
                    String url_request = HttpRequest.URL_SERVER_TERMINAL + "/mobile/sign-in-auto";

                    //Doing the request
                    HttpRequest req = new HttpRequest(url_request, params, header);
                    result = req.post();

                    return result;
                }

                /**
                 * Processing of the response of the POST request (signin or signup)
                 * @param res response of the POST request
                 */
                @Override
                protected void onPostExecute(String res) {
                    super.onPostExecute(res);
                    IdentificationController.this.onPostExecuteConnect(res, act, true);
                    act.hideProgress();
                }
            }.execute();
        } else {
            Toast.makeText(act, act.getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show();
            act.hideProgress();
        }
    }

    private void onPostExecuteConnect(String res, TerminalActivity act, boolean stay_connected) {
        //Initialization of the message to print in the toast with the error one
        response_str = act.getString(R.string.error_recuperation_data_on_server);
        try {
            JSONObject userJson = IdentificationController.this.parse(res);

            if (userJson != null && userJson.containsKey("token") && userJson.containsKey("email") && userJson.containsKey("type")) {
                String email = (String) userJson.get("email");
                String token = (String) userJson.get("token");
                String type = (String) userJson.get("type");

                assert type != null;
                if (type.equals(Utilisateur.PASSENGER_TYPE) || type.equals(Utilisateur.DRIVER_TYPE)) {
                    //Creating the model and connecting
                    Utilisateur u = new Utilisateur(email, type, token);
                    ConnectionManager.getInstance().setConnected(u);

                    //If the user want to remain connected, his information are stored
                    if (stay_connected) {
                        ConnectionManager.getInstance().storeTokenUserInPrefs(act);
                    }

                    //We can change the activity now
                    act.finish();
                    Intent intent = new Intent(act, HomeActivity.class);
                    act.startActivity(intent);
                }

            }
        } catch (
                ParseException e) {
            //Message of the error already stored in response_str attribute
        }

        Toast.makeText(act, response_str, Toast.LENGTH_SHORT).show();

    }

    /**
     * Disconnects the user if he is connected
     */
    public void disconnect(TerminalActivity act) {
        act.showProgress();
        ConnectionManager cm = ConnectionManager.getInstance();
        if (cm.isConnected()) {
            cm.disconnect();
            cm.removeTokenUserFromPrefs(act);
        }
        act.hideProgress();
    }
}
