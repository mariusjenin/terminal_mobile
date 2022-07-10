package terminal.mobilite.manager;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import terminal.mobilite.TerminalApp;
import terminal.mobilite.activity.R;
import terminal.mobilite.exception.BadMethodException;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * Gestionnaire des requêtes HTTP
 */
public class HttpRequest {
    public final static String URL_SERVER_TERMINAL = "https://terminal-modhost.loria.fr";
    public final static String HTTP_METHOD_GET = "GET";
    public final static String HTTP_METHOD_POST = "POST";


    private final String url_string;
    private final Map<String, Object> params;
    private final Map<String, Object> header;

    /**
     * @param u String
     * @param p Map<String, Object>
     * @param h Map<String, Object
     */
    public HttpRequest(String u, Map<String, Object> p, Map<String, Object> h) {
        url_string = u;
        params = p;
        header = h;
    }


    /**
     * Send a POST request to the url with the params and the header
     *
     * @return the response of the post terminal.manager.request
     */
    public String post() {
        return sendRequest(HTTP_METHOD_POST);
    }

    /**
     * Send a GET request to the url with the params and the header
     *
     * @return the response of the get terminal.manager.request
     */
    public String get() {
        return sendRequest(HTTP_METHOD_GET);
    }

    /**
     * Send a GET or POST request to the url with the params and the header
     *
     * @return the response of the post terminal.manager.request
     */
    private String sendRequest(String method) {
        HttpURLConnection conn = null;
        try {
            try {
                URL url;
                switch (method) {
                    case HTTP_METHOD_GET:
                        url = new URL(url_string + http_build_query(params));
                        break;
                    case HTTP_METHOD_POST:
                        url = new URL(url_string);
                        break;
                    default:
                        throw new BadMethodException();
                }


                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method);

                //HEADER
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.setConnectTimeout(1500); //set timeout to 3 seconds

                StringBuilder postData = new StringBuilder();
                if (method.equals(HTTP_METHOD_POST)) {
                    //DATA POST
                    for (Map.Entry<String, Object> param : params.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                }

                for (Map.Entry<String, Object> h : header.entrySet()) {
                    conn.setRequestProperty(h.getKey(), String.valueOf(h.getValue()));
                }
                conn.setRequestProperty("Accept-Language", String.valueOf(TerminalApp.getContext().getResources().getConfiguration().locale));

                if (method.equals(HTTP_METHOD_POST)) {
                    byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
                    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postDataBytes);
                }

                Log.i("URL", String.valueOf(url));
                Log.i("PARAM", params.toString());
                Log.i("HEADER", header.toString());

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                for (int c; (c = in.read()) >= 0; ) sb.append((char) c);;
                if (sb.length() > 4000) {
                    Log.v("RESULT", "sb.length = " + sb.length());
                    int chunkCount = sb.length() / 4000;     // integer division
                    for (int i = 0; i <= chunkCount; i++) {
                        int max = 4000 * (i + 1);
                        if (max >= sb.length()) {
                            Log.v("RESULT", "chunk " + i + " of " + chunkCount + ":" + sb.substring(4000 * i));
                        } else {
                            Log.v("RESULT", "chunk " + i + " of " + chunkCount + ":" + sb.substring(4000 * i, max));
                        }
                    }
                } else {
                    Log.v("RESULT", sb.toString());
                }
                return sb.toString();
            } catch (MalformedURLException | SocketTimeoutException | BadMethodException e) {
                //Erreur d'URL malformé, de mauvaise méthode ou d'accès au serveur
                JSONObject json = new JSONObject();
                json.put("error", TerminalApp.getContext().getString(R.string.error_access_server));
                return json.toString(0);
            } catch (IOException ioe) {
                //Erreur renvoyée par la requete
                try {
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    for (int length; (length = Objects.requireNonNull(conn).getErrorStream().read(buffer)) != -1; ) {
                        result.write(buffer, 0, length);
                    }
                    return result.toString("UTF-8");
                } catch (IOException e) {
                    //Error of Input during creation of error json (it is not supposed to be possible)
                    return "{\"error\":\"" + TerminalApp.getContext().getString(R.string.error_recuperation_data_on_server) + "\"}";
                }

            }
        } catch (JSONException je) {
            //Error json during creation of error json (it is not supposed to be possible)
            return "{\"error\":\"" + TerminalApp.getContext().getString(R.string.error_recuperation_data_on_server) + "\"}";
        }
    }

    /**
     * Transform a Map of paramaters into a query string
     *
     * @param params Map<String,Object>
     * @return query for url
     */
    public static String http_build_query(Map<String, Object> params) {
        if (!params.isEmpty()) {
            StringBuilder reString = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                String key = param.getKey();
                Object value = param.getValue();
                reString.append(key).append("=").append(value).append("&");
            }
            reString = new StringBuilder(reString.substring(0, reString.length() - 1));

            reString = new StringBuilder(URLEncoder.encode(reString.toString()));
            reString = new StringBuilder(reString.toString().replace("%3D", "=").replace("%26", "&"));
            return reString.toString();
        }
        return "";
    }
}
