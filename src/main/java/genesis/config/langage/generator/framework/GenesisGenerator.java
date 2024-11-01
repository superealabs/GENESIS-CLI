package genesis.config.langage.generator.framework;

import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.connexion.model.TableMetadata;

public interface GenesisGenerator {
    String generateModel(Framework framework, Language language, TableMetadata tableMetadata, String destinationFolder, String projectName, String groupLink) throws Exception;

    String generateDao(Framework framework, Language language, TableMetadata tableMetadata, String destinationFolder, String projectName, String groupLink) throws Exception;

    String generateDao(Framework framework, Language language, TableMetadata[] tableMetadata, String destinationFolder, String projectName, String groupLink) throws Exception;

    String generateService(Framework framework, Language language, TableMetadata tableMetadata, String destinationFolder, String projectName, String groupLink) throws Exception;

    String generateController(Framework framework, Language language, TableMetadata tableMetadata, String destinationFolder, String projectName, String groupLink) throws Exception;
}
