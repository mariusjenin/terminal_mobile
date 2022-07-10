package terminal.mobilite.model.reservation;

/**
 * Segment d'un itinéraire (Utilisé pour le parsing de JSON)
 */
public class Segment {
    private final int start_stop_pos;
    private final int end_stop_pos;
    private final float distance;
    private final float duration;

    public Segment(int ssp, int esp, float dist , float dur){
        start_stop_pos = ssp;
        end_stop_pos = esp;
        distance = dist;
        duration = dur;
    }

    public int getEndStopPos() {
        return end_stop_pos;
    }

    public int getStartStopPos() {
        return start_stop_pos;
    }

    public float getDistance() {
        return distance;
    }

    public float getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return
                "(start_stop_pos : " + start_stop_pos +
                        ", end_stop_pos : " + end_stop_pos +
                        ", distance : " + distance +
                        ", end_stop_pos : " + end_stop_pos +
                        ", duration : " + duration+")";
    }
}
