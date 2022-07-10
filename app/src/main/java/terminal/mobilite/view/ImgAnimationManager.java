package terminal.mobilite.view;

import android.content.Context;
import android.graphics.*;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.model.LatLng;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.R;

/**
 * Manager de l'affichages d'images dynamiques (notamment sur des cartes google)
 */
public final class ImgAnimationManager {

    /**
     * Marker size
     */
    public static final long MARKER_ITINERARY_SIZE = 40;
    public static final long MARKER_ITINERARY_SIZE_BIGGER = 50;
    public static final long MARKER_POSITION_SIZE = 60;
    public static final long MARKER_POSITION_WHITE_STRIPE_SIZE = 10;

    /**
     * Car size
     */
    private static final long HEIGHT_CAR = 100;
    private static final long WIDTH_CAR = HEIGHT_CAR / 2;


    /**
     * Arrow size
     */
    private static final long SIZE_ARROW = 100;


    /**
     * Instance for Singleton Design Pattern
     */
    private static ImgAnimationManager INSTANCE;

    /**
     * Empty constructor for Singleton Design Pattern
     */
    private ImgAnimationManager() {
    }

    /**
     * getInstance for Singleton Design Pattern
     *
     * @return INSTANCE of AnimationCarOnMap
     */
    public static ImgAnimationManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImgAnimationManager();
        }
        return INSTANCE;
    }

    /**
     * Get the Bitmap picture of the car
     *
     * @param context Context
     * @return Bitmap of the car
     */
    public final Bitmap getArrowBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow);
        return Bitmap.createScaledBitmap(bitmap, (int) SIZE_ARROW, (int) SIZE_ARROW, false);
    }

    /**
     * Get the Bitmap picture of the car
     *
     * @param context Context
     * @return Bitmap of the car
     */
    public final Bitmap getCarBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_car);
        return Bitmap.createScaledBitmap(bitmap, (int) WIDTH_CAR, (int) HEIGHT_CAR, false);
    }

    /**
     * Get the round Bitmap picture of a marker
     *
     * @return Bitmap of a marker
     */
    public final Bitmap getRoundMarkerBitmap(long size ,int color) {
        Bitmap bitmap = Bitmap.createBitmap((int) size, (int) size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(TerminalApp.getContext(), R.color.black));
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setColor(ContextCompat.getColor(TerminalApp.getContext(), R.color.white));
        canvas.drawCircle(size / 2f, size / 2f, (size / 2f)-1, paint);
        paint.setColor(color);
        canvas.drawCircle(size / 2f, size / 2f, (size - MARKER_POSITION_WHITE_STRIPE_SIZE) / 2f, paint);
        return bitmap;
    }

    /**
     * Get car orientation of the car durinf the trip
     *
     * @param start start coords
     * @param end   and coords
     * @return car orientation
     */
    public final float getRotation(LatLng start, LatLng end) {
        double lngDifference = start.latitude - end.latitude;
        double latDifference = Math.abs(lngDifference);
        double var11 = start.longitude - end.longitude;
        lngDifference = Math.abs(var11);
        float rotation = -1.0F;
        double degr = Math.toDegrees(Math.atan(lngDifference / latDifference));
        if (start.latitude < end.latitude && start.longitude < end.longitude) {
            rotation = (float) degr;
        } else {
            if (start.latitude >= end.latitude && start.longitude < end.longitude) {
                rotation = (float) (90 - degr + (double) 90);
            } else if (start.latitude >= end.latitude && start.longitude >= end.longitude) {
                rotation = (float) (degr + (double) 180);
            } else if (start.latitude < end.latitude && start.longitude >= end.longitude) {
                rotation = (float) (90 - degr + (double) 270);
            }
        }

        return rotation;
    }
}
