package terminal.mobilite.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class Controller {
    protected String response_str;

    /**
     * Parse une réponse JSON venant de l'API Terminal
     * @param str
     * @return
     * @throws ParseException
     */
    public JSONObject parse(String str) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(str);

        if (json.containsKey("success")) {
            //SUCCESS response
            response_str = (String) json.get("success");
            return (JSONObject) json.get("result");
        } else {
            response_str = (String) json.get("error");
            throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
        }
    }
}
