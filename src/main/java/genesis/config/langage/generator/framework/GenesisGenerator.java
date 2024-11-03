package genesis.config.langage.generator.framework;

import genesis.config.langage.Editor;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.model.TableMetadata;

public interface GenesisGenerator {
    String generateModel(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception;

    String generateDao(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception;

    String generateDao(Framework framework, Language language, TableMetadata[] tableMetadata, String projectName, String groupLink) throws Exception;

    String generateService(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception;

    String generateController(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception;

    String generateView(Framework framework, Language language, Editor editor, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception;

    void generateViewMainLayout(Framework framework, Language language, Editor editor, TableMetadata[] tableMetadatas, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception;
}
