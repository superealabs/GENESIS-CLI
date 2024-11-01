package genesis.config.langage.generator.project;

import genesis.config.langage.Editor;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.generator.framework.FrameworkMetadataProvider;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.engine.TemplateEngine;
import genesis.model.TableMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectMetadataProvider {
    private static final TemplateEngine engine = new TemplateEngine();

    static HashMap<String, Object> getInitialHashMap(String projectName, String groupLink) {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("projectName", projectName);
        metadata.put("groupLink", groupLink);

        return metadata;
    }

    static HashMap<String, Object> getApplicationPropertiesHashMap(String projectPort,
                                                                   String logLevel,
                                                                   Database database,
                                                                   Credentials credentials,
                                                                   Language language,
                                                                   String hibernateDdlAuto) throws Exception {
        HashMap<String, Object> appPropertiesMap = new HashMap<>();
        appPropertiesMap.put("projectPort", projectPort);
        appPropertiesMap.put("logLevel", logLevel);
        String databaseUrl = database.getConnectionString().get(language.getId());

        Map<String, Object> databaseMetadata = database.getDatabaseMetadataHashMap(credentials);
        databaseUrl = engine.render(databaseUrl, databaseMetadata);

        appPropertiesMap.put("databaseUrl", databaseUrl);
        appPropertiesMap.put("databaseUsername", database.getCredentials().getUser());
        appPropertiesMap.put("databasePassword", database.getCredentials().getPwd());
        appPropertiesMap.put("hibernateDdlAuto", hibernateDdlAuto);

        return appPropertiesMap;
    }

    static HashMap<String, Object> getDependencyFileHashMap(String frameworkVersion, String projectDescription, String languageVersion, Database database, Language language, Framework framework) {
        HashMap<String, Object> pomXmlMap = new HashMap<>();
        pomXmlMap.put("frameworkVersion", frameworkVersion);
        pomXmlMap.put("projectDescription", projectDescription);
        pomXmlMap.put("languageVersion", languageVersion);

        List<HashMap<String, String>> dependencies = getDependenciesHashMaps(framework);
        pomXmlMap.put("dependencies", dependencies);

        // Database dependency example (conditionally added)
        if (database != null) {
            pomXmlMap.put("useDB", true);

            Framework.Dependency databaseDependency = database.getDependencies().get(String.valueOf(language.getId()));

            pomXmlMap.put("DBgroupId", databaseDependency.getGroupId());
            pomXmlMap.put("DBartifactId", databaseDependency.getArtifactId());
            pomXmlMap.put("DBversion", database.getDriverVersion());
        } else {
            pomXmlMap.put("useDB", false);
            pomXmlMap.put("DBgroupId", "{{removeLine}}");
            pomXmlMap.put("DBartifactId", "{{removeLine}}");
            pomXmlMap.put("DBversion", "{{removeLine}}");
        }

        return pomXmlMap;
    }

    private static @NotNull List<HashMap<String, String>> getDependenciesHashMaps(Framework framework) {
        List<HashMap<String, String>> dependencies = new ArrayList<>();
        Framework.Dependency[] dependenciesList = framework.getDependencies();

        for (Framework.Dependency dependency : dependenciesList) {
            HashMap<String, String> dependencyMap = new HashMap<>();
            dependencyMap.put("groupId", dependency.getGroupId());
            dependencyMap.put("artifactId", dependency.getArtifactId());
            dependencyMap.put("version", dependency.getVersion());
            dependencies.add(dependencyMap);
        }

        return dependencies;
    }

    static HashMap<String, Object> getProjectFilesEditsHashMap(String projectName,
                                                               String groupLink,
                                                               String projectPort,
                                                               String logLevel,
                                                               @NotNull Database database,
                                                               @NotNull Credentials credentials,
                                                               @NotNull Language language,
                                                               String hibernateDdlAuto,
                                                               String frameworkVersion,
                                                               String projectDescription,
                                                               String languageVersion,
                                                               Framework framework,
                                                               Editor editor,
                                                               TableMetadata[] entities) throws Exception {
        HashMap<String, Object> combinedMap = new HashMap<>();


        // Ajoute les propriétés de l'application
        combinedMap.putAll(getApplicationPropertiesHashMap(
                projectPort,
                logLevel,
                database,
                credentials,
                language,
                hibernateDdlAuto));

        // Ajoute le POM XML
        combinedMap.putAll(getDependencyFileHashMap(
                frameworkVersion,
                projectDescription,
                languageVersion,
                database,
                language,
                framework));

        // Ajoute les métadonnées initiales
        combinedMap.putAll(getInitialHashMap(projectName, groupLink));
        combinedMap.put("fields", FrameworkMetadataProvider.getClassNameHashMap(entities));

        for (TableMetadata tableMetadata : entities) {
            combinedMap.putAll(FrameworkMetadataProvider.getMenuLayoutHashMap(editor, tableMetadata, projectName, groupLink));
        }

        return combinedMap;
    }

    public static HashMap<String, Object> getAltHashMap(Editor editor) {
        HashMap<String, Object> altMap = new HashMap<>();

        altMap.put("listLink", editor.getLayout().getMenu().getListLink());
        altMap.put("createLink", editor.getLayout().getMenu().getCreateLink());

        return altMap;
    }
}
