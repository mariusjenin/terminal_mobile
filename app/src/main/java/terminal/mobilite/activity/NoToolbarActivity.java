package terminal.mobilite.activity;

import android.os.Bundle;
import android.view.WindowManager;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Activité sans toolbar
 */
public abstract class NoToolbarActivity extends TerminalActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.TerminalThemeTranslucent);

        Objects.requireNonNull(this.getSupportActionBar()).hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

    }
}
