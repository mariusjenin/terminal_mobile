package terminal.mobilite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import terminal.mobilite.activity.operational.FutureTripsActivity;
import terminal.mobilite.activity.operational.HomeActivity;
import terminal.mobilite.activity.operational.IdentificationActivity;
import terminal.mobilite.activity.operational.ItinerariesActivity;
import terminal.mobilite.controller.IdentificationController;

import java.util.Objects;

/**
 * Activité avec une toolbar
 */
public abstract class ToolbarActivity extends TerminalActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.TerminalTheme);

        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        View customView = getLayoutInflater().inflate(R.layout.custom_toolbar, null);
        actionBar.setCustomView(customView);

        Toolbar toolbar = (Toolbar) customView.getParent();
        toolbar.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        toolbar.setContentInsetsAbsolute(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.home_menu_item:
                if (!(ToolbarActivity.this instanceof HomeActivity)) {
                    Intent intent = new Intent(ToolbarActivity.this, HomeActivity.class);
                    ToolbarActivity.this.startActivity(intent);
                }
                return true;
            case R.id.itineraries_menu_item:
                if (!(ToolbarActivity.this instanceof ItinerariesActivity)) {
                    Intent intent = new Intent(ToolbarActivity.this, ItinerariesActivity.class);
                    ToolbarActivity.this.startActivity(intent);
                }
                return true;
            case R.id.future_trips_menu_item:
                if (!(ToolbarActivity.this instanceof FutureTripsActivity)) {
                    Intent intent = new Intent(ToolbarActivity.this, FutureTripsActivity.class);
                    ToolbarActivity.this.startActivity(intent);
                }
                return true;
            case R.id.disconnect_menu_item:
                IdentificationController.getInstance().disconnect(this);
                Intent intent = new Intent(this, IdentificationActivity.class);
                ToolbarActivity.this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
