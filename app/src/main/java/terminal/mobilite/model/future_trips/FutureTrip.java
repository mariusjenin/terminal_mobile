package terminal.mobilite.model.future_trips;

/**
 * Trajet à venir (Utilisé pour le parsing de JSON)
 */
public class FutureTrip implements Comparable<FutureTrip> {
    private String id_reserv;
    private final String id_trajet;
    private final String titre;
    private final String date_depart;
    private final String arret_depart;
    private final String arret_fin;
    private final String heure_depart;
    private final String heure_fin;

    public FutureTrip(String idt, String t, String d_dep, String a_dep, String a_fin, String h_dep, String h_fin) {
        id_trajet = idt;
        titre = t;
        date_depart = d_dep;
        arret_depart = a_dep;
        arret_fin = a_fin;
        heure_depart = h_dep;
        heure_fin = h_fin;
    }

    public FutureTrip(String idr,String idt, String t, String d_dep, String a_dep, String a_fin, String h_dep, String h_fin) {
        this(idt, t, d_dep, a_dep, a_fin, h_dep, h_fin);
        id_reserv = idr;
    }

    @Override
    public int compareTo(FutureTrip t) {
        return id_trajet.compareTo(t.id_trajet);
    }

    public String getTitre() {
        return titre;
    }

    public String getDateDepart() {
        return date_depart;
    }

    public String getArretDepart() {
        return arret_depart;
    }

    public String getArretFin() {
        return arret_fin;
    }

    public String getHeureDepart() {
        return heure_depart;
    }

    public String getHeureFin() {
        return heure_fin;
    }

    public String getIdTrajet() {
        return id_trajet;
    }

    public String getIdReserv() {
        return id_reserv;
    }
}
