package genesis.connexion.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import genesis.connexion.Database;
import genesis.connexion.providers.MySQLDatabase;
import genesis.connexion.providers.OracleDatabase;
import genesis.connexion.providers.PostgreSQLDatabase;
import genesis.connexion.providers.SQLServerDatabase;

public class DatabaseTypeAdapter extends TypeAdapter<Database> {
    @Override
    public void write(JsonWriter out, Database value) {
    }


    @Override
    public Database read(JsonReader in) {
        JsonObject jsonObject = JsonParser.parseReader(in).getAsJsonObject();
        String name = jsonObject.get("name").getAsString();

        if ("mysql".equalsIgnoreCase(name)) {
            return new Gson().fromJson(jsonObject, MySQLDatabase.class);
        }
        else if ("postgresql".equalsIgnoreCase(name)) {
            return new Gson().fromJson(jsonObject, PostgreSQLDatabase.class);
        }
        else if ("sqlServer".equalsIgnoreCase(name)) {
            return new Gson().fromJson(jsonObject, SQLServerDatabase.class);
        }
        else if ("oracle".equalsIgnoreCase(name)) {
            return new Gson().fromJson(jsonObject, OracleDatabase.class);
        }

        throw new IllegalArgumentException("Unknown database type: " + name);
    }
}
