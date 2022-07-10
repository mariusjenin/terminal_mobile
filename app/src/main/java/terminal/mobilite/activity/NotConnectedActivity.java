package terminal.mobilite.activity;

import terminal.mobilite.middleware.NotConnectedMiddleware;
import terminal.mobilite.middleware.TerminalMiddleware;

/**
 * Activité qui requiert de ne pas être connecté
 */
public interface NotConnectedActivity {
    TerminalMiddleware middleware = new NotConnectedMiddleware();
}
