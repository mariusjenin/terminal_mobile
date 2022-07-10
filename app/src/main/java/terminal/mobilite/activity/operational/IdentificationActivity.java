package terminal.mobilite.activity.operational;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import terminal.mobilite.activity.NoToolbarActivity;
import terminal.mobilite.activity.NotConnectedActivity;
import terminal.mobilite.activity.R;
import terminal.mobilite.controller.IdentificationController;
import terminal.mobilite.manager.ConnectionManager;

/**
 * Activité de l'écran d'identification (Inscription/Connexion)
 */
public class IdentificationActivity extends NoToolbarActivity implements NotConnectedActivity {


    private boolean is_sign_up;
    private TextView tv_title;
    private EditText et_email;
    private EditText et_pwd;
    private EditText et_pwd_confirm;
    private Button but_submit;
    private Button but_toggle_identification;
    private CheckBox checkbox_stay_connected;

    /**
     * Creation of the IdentificationActivity (setting up of the layout and recuperation of all the fields)
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ConnectionManager cm =ConnectionManager.getInstance();
        if(!cm.isConnected()){
            String token = cm.getTokenUserFromPrefs(this);
            if(token != null){
                IdentificationController.getInstance().sign_in_auto(this,token);
            }
        }

        middleware.verify_and_redirect(this);

        //We begin on the signin page
        is_sign_up = false;

        //Put the right layout
        this.setContentView(R.layout.identification_activity);

        tv_title = findViewById(R.id.title_identification);
        et_email = findViewById(R.id.input_email_identification);
        et_pwd = findViewById(R.id.input_pwd_identification);
        et_pwd_confirm = findViewById(R.id.input_pwd_confirm_identification);
        but_submit = findViewById(R.id.submit_identification);
        but_toggle_identification = findViewById(R.id.toggle_identification);
        checkbox_stay_connected = findViewById(R.id.checkbox_stay_connected_identification);

        //Initialize text and hint of each component
        initializeFormidentification();
    }

    /**
     * Actualise tous les champs en fonction de si nous sommes sur le formulaire
     * d'inscription ou de connexion
     */
    private void actualizeFormIdentification() {
        if (is_sign_up) {
            tv_title.setText(R.string.sign_up_title);
            but_submit.setText(R.string.sign_up_submit);
            but_toggle_identification.setText(R.string.toggle_identification_from_signup);
            et_pwd.setText("");
            et_pwd_confirm.setText("");
            et_pwd_confirm.setVisibility(View.VISIBLE);
        } else {
            tv_title.setText(R.string.sign_in_title);
            but_submit.setText(R.string.sign_in_submit);
            but_toggle_identification.setText(R.string.toggle_identification_from_signin);
            et_pwd.setText("");
            et_pwd_confirm.setVisibility(View.GONE);
        }
    }

    /**
     * Initialisation du formulaire
     */
    private void initializeFormidentification() {
        et_email.setHint(R.string.hint_email_identification);
        et_pwd.setHint(R.string.hint_pwd_identification);
        et_pwd_confirm.setHint(R.string.hint_pwd_confirm_sign_up);


        but_toggle_identification.setOnClickListener(v -> {
            is_sign_up = !is_sign_up;
            actualizeFormIdentification();
        });

        but_submit.setOnClickListener(v -> submit());

        actualizeFormIdentification();
    }


    /**
     * Validation du formulaire (on effectue une requête au serveur)
     */
    private void submit() {
        //Get the value of each input
        String email = et_email.getText().toString();
        String pwd = et_pwd.getText().toString();
        String pwd_confirm = et_pwd_confirm.getText().toString();
        boolean stay_connected = checkbox_stay_connected.isChecked();

        IdentificationController.getInstance().sign_in_sign_up(this,is_sign_up,email,pwd,pwd_confirm,stay_connected);

    }
}
