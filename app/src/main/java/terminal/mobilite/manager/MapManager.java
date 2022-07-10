package terminal.mobilite.manager;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;
import terminal.mobilite.manager.LocalizationManager;
import terminal.mobilite.view.ImgAnimationManager;

import java.util.ArrayList;

/**
 * Manager de carte
 */
public class MapManager {
    public static final int CAMERA_AROUND_BOUNDS_PADDING = 80;

    private final GoogleMap gm;

    private LatLng previousLatLng;
    private LatLng currentLatLng;

    public MapManager(GoogleMap g) {
        this.gm = g;
    }

    /**
     * Put the camera at the given coords with zoom given
     *
     * @param latLng LatLng
     * @param zoom   float
     */
    public void moveCameraToLatLngWithZoom(LatLng latLng, float zoom) {
        CameraPosition cameraPosition = (new CameraPosition.Builder()).target(latLng).zoom(zoom).build();
        gm.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Animate the camera to go at the given coords with a given zoom
     *
     * @param latLng LatLng
     * @param zoom   float
     */
    public void animateCameraToLatLngWithZoom(LatLng latLng, float zoom) {
        CameraPosition cameraPosition = (new CameraPosition.Builder()).target(latLng).zoom(zoom).build();
        gm.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Animate the camera to go around given coords
     *
     * @param latLngList ArrayList<LatLng>
     */
    public void animateCameraAroundBounds(ArrayList<LatLng> latLngList) {
        com.google.android.gms.maps.model.LatLngBounds.Builder builder = new com.google.android.gms.maps.model.LatLngBounds.Builder();

        for (LatLng latLng : latLngList) {
            builder.include(latLng);
        }

        LatLngBounds bounds = builder.build();
        gm.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, CAMERA_AROUND_BOUNDS_PADDING));
    }

    /**
     * Put the camera at the given coords and zoom to have the default position
     *
     * @param latLng LatLng
     */
    public void cameraDefaultPosition(LatLng latLng) {
        moveCameraToLatLngWithZoom(latLng, 14f);
        animateCameraToLatLngWithZoom(latLng, 15f);
    }

    public Marker addMarkerAndGet(LatLng latLng, Bitmap bitmap, String title, String snippet, int zIndex) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        MarkerOptions m_opt = (new MarkerOptions()).zIndex(zIndex).position(latLng).flat(true).icon(bitmapDescriptor);
        if (!title.equals("")) {
            m_opt.title(title);
        }
        if (!snippet.equals("")) {
            m_opt.snippet(snippet);
        }
        Marker m = gm.addMarker(m_opt);
        assert m != null;
        m.setAnchor(0.5F, 0.5F);
        return m;
    }

    public void removeAllMarker() {
        gm.clear();
    }

    public final void updateCarLocation(Marker cabMarker,long delay,long duration, final LatLng latLng) {
        Runnable r = () -> {
            if (previousLatLng == null) {
                currentLatLng = latLng;
                previousLatLng = currentLatLng;
                cabMarker.setPosition(currentLatLng);
            } else {
                previousLatLng = currentLatLng;
                currentLatLng = latLng;
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
                valueAnimator.setDuration(duration);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(va -> {
                    if (currentLatLng != null && previousLatLng != null) {
                        float multiplier = va.getAnimatedFraction();
                        LatLng nextLocation = new LatLng(
                                multiplier * currentLatLng.latitude + (1 - multiplier) * previousLatLng.latitude,
                                multiplier * currentLatLng.longitude + (1 - multiplier) * previousLatLng.longitude
                        );
                        cabMarker.setPosition(nextLocation);
                        float rotation = ImgAnimationManager.getInstance().getRotation(previousLatLng, nextLocation);
                        if (!Float.isNaN(rotation)) {
                            cabMarker.setRotation(rotation);
                        }
                    }

                });
                valueAnimator.start();
            }
        };
        new Handler().postDelayed(r,delay);
    }

    public void drawCab(Marker cabMarker,ArrayList<LatLng> cabLatLngList, long delay){
        int size = cabLatLngList.size();
        int distAmount = 0;
        int[] distances = new int[size];
        LatLng prevLatLng = cabLatLngList.get(0);
        for (int i = 0; i < size; i++) {
            LatLng currentLatLng = cabLatLngList.get(i);
            distances[i] += LocalizationManager.haversine(prevLatLng, currentLatLng);
            prevLatLng = currentLatLng;
            distAmount += distances[i];
        }
        if(distAmount<=0) distAmount = 1;

        int timeAmount = 0;
        for (int i = 0; i < size; i++) {
            long time = ((long) distances[i] * delay) / distAmount;
            timeAmount += time;
            this.updateCarLocation(cabMarker,timeAmount, time, cabLatLngList.get(i));
        }
    }
}
