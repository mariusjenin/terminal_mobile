package terminal.mobilite.activity;

import terminal.mobilite.middleware.ConnectedMiddleware;
import terminal.mobilite.middleware.TerminalMiddleware;

/**
 * Activité qui requiert d'être connecté
 */
public interface ConnectedActivity {
    TerminalMiddleware middleware = new ConnectedMiddleware();
}
