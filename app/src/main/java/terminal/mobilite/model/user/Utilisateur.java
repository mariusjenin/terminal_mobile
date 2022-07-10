package terminal.mobilite.model.user;

/**
 * Utilisateur (Utilisé pour le parsing de JSON)
 */
public class Utilisateur {
    public static final String PASSENGER_TYPE = "PASSAGER";
    public static final String DRIVER_TYPE = "CONDUCTEUR";

    private final String email;
    private final String type;
    private final String token;

    public Utilisateur(String e,String t,String tok){
        email=e;
        type=t;
        token=tok;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }
}
