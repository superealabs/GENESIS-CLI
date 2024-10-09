package genesis.config.langage.generator;

import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.model.TableMetadata;

import java.io.IOException;
import java.util.HashMap;

public interface GenesisGenerator {
    String generateModel(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception;
    String generateDao(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception;
    String generateDao(Framework framework, Language language, TableMetadata[] tableMetadata, String projectName, String groupLink) throws Exception;
    String generateService(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception;
    String generateController(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception;
    String generateView(Framework framework, Language language, TableMetadata tableMetadata, Database database, Credentials credentials, String projectName, String groupLink) throws IOException;
}
