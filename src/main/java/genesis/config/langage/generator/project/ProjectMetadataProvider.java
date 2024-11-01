package genesis.config.langage.generator.project;

import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.engine.TemplateEngine;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectMetadataProvider {
    private static final TemplateEngine engine = new TemplateEngine();

    static HashMap<String, Object> getInitialHashMap(String destinationFolder, String projectName, String groupLink) {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("destinationFolder", destinationFolder);
        metadata.put("projectName", projectName);
        metadata.put("groupLink", groupLink);
        metadata.put("groupLinkPath", groupLink.replace(".", "/"));
        return metadata;
    }

    static HashMap<String, Object> getConfigFileHashMap(String projectPort, Database database, Credentials credentials, Language language, HashMap<String, String> frameworkOptions) throws Exception {
        HashMap<String, Object> configFile = new HashMap<>();
        String databaseUrl = database.getConnectionString().get(language.getId());

        Map<String, Object> databaseMetadata = database.getDatabaseMetadataHashMap(credentials);
        databaseUrl = engine.render(databaseUrl, databaseMetadata);

        configFile.put("projectPort", projectPort);
        configFile.put("databaseUrl", databaseUrl);
        configFile.put("databaseUsername", database.getCredentials().getUser());
        configFile.put("databasePassword", database.getCredentials().getPwd());
        configFile.putAll(frameworkOptions);

        return configFile;
    }

    static HashMap<String, Object> getDependencyFileHashMap(String projectDescription, Database database, Language language, Framework framework, HashMap<String, String> langageConfiguration, HashMap<String, String> frameworkConfiguration) {
        HashMap<String, Object> dependencyFileMap = new HashMap<>();
        dependencyFileMap.putAll(langageConfiguration);
        dependencyFileMap.putAll(frameworkConfiguration);
        dependencyFileMap.put("projectDescription", projectDescription);

        List<HashMap<String, String>> dependencies = getDependenciesHashMaps(framework);
        dependencyFileMap.put("dependencies", dependencies);

        if (database != null) {
            dependencyFileMap.put("useDB", framework.getUseDB());

            Framework.Dependency databaseDependency = database.getDependencies().get(String.valueOf(language.getId()));

            dependencyFileMap.put("DBgroupId", databaseDependency.getGroupId());
            dependencyFileMap.put("DBartifactId", databaseDependency.getArtifactId());
            dependencyFileMap.put("DBversion", database.getDriverVersion());
        } else {
            dependencyFileMap.put("useDB", false);
            dependencyFileMap.put("DBgroupId", "{{removeLine}}");
            dependencyFileMap.put("DBartifactId", "{{removeLine}}");
            dependencyFileMap.put("DBversion", "{{removeLine}}");
        }

        return dependencyFileMap;
    }

    private static @NotNull List<HashMap<String, String>> getDependenciesHashMaps(Framework framework) {
        List<HashMap<String, String>> dependencies = new ArrayList<>();
        List<Framework.Dependency> dependenciesList = framework.getDependencies();

        for (Framework.Dependency dependency : dependenciesList) {
            HashMap<String, String> dependencyMap = new HashMap<>();
            dependencyMap.put("groupId", dependency.getGroupId());
            dependencyMap.put("artifactId", dependency.getArtifactId());
            dependencyMap.put("version", dependency.getVersion());
            dependencies.add(dependencyMap);
        }

        return dependencies;
    }

    static HashMap<String, Object> getProjectFilesEditsHashMap(String destinationFolder, String projectName, String groupLink, String projectPort, @NotNull Database database, @NotNull Credentials credentials, @NotNull Language language, String projectDescription, HashMap<String, String> langageConfiguration, Framework framework, HashMap<String, String> frameworkOptions) throws Exception {
        HashMap<String, Object> combinedMap = new HashMap<>();

        combinedMap.putAll(getConfigFileHashMap(projectPort, database, credentials, language, frameworkOptions));
        combinedMap.putAll(getDependencyFileHashMap(projectDescription, database, language, framework, langageConfiguration, frameworkOptions));
        combinedMap.putAll(getInitialHashMap(destinationFolder, projectName, groupLink));

        return combinedMap;
    }
}
