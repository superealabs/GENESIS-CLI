package genesis.config.langage.generator;

import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.model.Entity;

import java.io.IOException;

public interface GenesisGenerator {
    String generateModel(Framework framework, Language language, Entity entity, String projectName) throws IOException;
    String generateController(Framework framework, Language language, Entity entity, Database database, Credentials credentials, String projectName) throws IOException;
    String generateView(Framework framework, Language language, Entity entity, Database database, Credentials credentials, String projectName) throws IOException;
}
