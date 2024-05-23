package com.adobe.aem.guides.wknd.core.servlets;


import com.adobe.aem.guides.wknd.core.models.RESTJSON;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.*;
import java.util.List;
import java.util.Set;



@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/poc/starwars"
        })
public class StarWars extends SlingSafeMethodsServlet {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // API end point should be provided by configuration
    private final static String STARWARS_API = "https://swapi.dev/api/people/13/";

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {

        List<String[]> fieldsByLevel = List.of(
                new String[]{"name"},
                new String[]{"height", "mass", "hair_color", "skin_color", "eye_color", "birth_year","gender","homeworld"},
                new String[]{"vehicles", "starships"},
                new String[]{"films", "species"}
        );
        RequestParameter levelParam = req.getRequestParameter("level");
        String level = levelParam != null ? levelParam.getString() : "-1";
        int intLevel = Integer.parseInt(level);
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonObject apiJson = getStarWarsData(STARWARS_API);
        Set<String> keys = apiJson.keySet();
        for (int i=0; i< fieldsByLevel.size() && i <= intLevel; i++) {
            String[] props = fieldsByLevel.get(i);
            for (String prop : props) {
                if (keys.contains(prop)) {
                    if (apiJson.get(prop).getValueType() == JsonObject.ValueType.ARRAY) {
                        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                        JsonArray jsonArray = apiJson.getJsonArray(prop);
                        for (int j=0; j<jsonArray.size(); j++) {
                            String value = jsonArray.getString(j);
                            if (value.startsWith("http")) {
                                String newValue = getDefaultField(getStarWarsData(value));
                                arrayBuilder.add(newValue);
                            } else {
                                arrayBuilder.add(value);
                            }
                        }
                        builder.add(prop, arrayBuilder);
                    } else {
                        String value = apiJson.getString(prop);
                        if (value.startsWith("http")) {
                            String newValue = getDefaultField(getStarWarsData(value));
                            builder.add(prop, newValue);
                        } else {
                            builder.add(prop, apiJson.getString(prop));
                        }
                    }
                }
            }
        }
        JsonObject finalJson = builder.build();
        resp.setContentType("application/json");
        String jsonString;
        try(Writer writer = new StringWriter()) {
            JsonWriter jsonWriter = Json.createWriter(writer);
            jsonWriter.write(finalJson);
            jsonString = writer.toString();
            jsonWriter.close();
        }
        resp.getWriter().write(jsonString);
    }

    private JsonObject getStarWarsData(String url) {
        String restString = RESTJSON.callGet(url, null);
        JsonObject json;
        try (JsonReader reader = Json.createReader(new StringReader(restString))) {
            json = reader.readObject();
        }
        return json;
    }

    private String getDefaultField(JsonObject json) {
        String retVal =json.containsKey("name") ? json.getString("name") : "";
        if (retVal.isEmpty() && json.containsKey("title")) {
            retVal = json.getString("title");
        }
        return retVal;
    }
}