package terminal.mobilite.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import terminal.mobilite.TerminalApp;

/**
 * Activité de l'application TERMINAL
 */
public abstract class TerminalActivity extends AppCompatActivity {
    private ProgressDialog progDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TerminalApp.setCurrentAct(this);
    }

    public void showProgress(){
        progDialog = ProgressDialog.show( this, null, null, false, false );
        progDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progDialog.setContentView( R.layout.progress_bar );
    }

    public void hideProgress(){
        progDialog.dismiss();
    }
}
