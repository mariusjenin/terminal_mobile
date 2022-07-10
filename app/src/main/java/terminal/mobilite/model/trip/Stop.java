package terminal.mobilite.model.trip;

import androidx.annotation.NonNull;

/**
 * Arrêt (Utilisé pour le parsing de JSON)
 */
public abstract class Stop {
    protected final String id;
    protected final String name;
    protected final String num;

    public Stop(String i, String n, String nu){
        id=i;
        name=n;
        num = nu;
    }

    @NonNull
    @Override
    public String toString() {
        return "(id : "+id+", name : "+name+")";
    }

    public String toItemString(){
        return name;
    }


    public String getName() {
        return name;
    }
}
