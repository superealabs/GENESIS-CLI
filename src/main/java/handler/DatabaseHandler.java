package handler;

import genesis.config.Constantes;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import utils.FileUtils;

import java.io.IOException;
import java.util.Scanner;

public class DatabaseHandler {

    public Database chooseDatabase(Scanner scanner) throws IOException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        System.out.println("Choose a database engine:");
        for (int i = 0; i < databases.length; i++) {
            System.out.println((i + 1) + ") " + databases[i].getName());
        }
        System.out.print("> ");
        return databases[scanner.nextInt() - 1];
    }

    public Credentials inputCredentials(Scanner scanner) {
        System.out.println("Enter your database credentials");
        System.out.print("Database name: ");
        String databaseName = scanner.next();
        System.out.print("Username: ");
        String user = scanner.next();
        System.out.print("Password: ");
        String pwd = scanner.next();
        System.out.print("Database host: ");
        String host = scanner.next();
        System.out.print("Port: ");
        String port = scanner.next();
        System.out.print("Use SSL ?(Y/n): ");
        boolean useSSL = scanner.next().equalsIgnoreCase("Y");
        System.out.print("Trust Certificate ?(Y/n): ");
        boolean trustCertificate = scanner.next().equalsIgnoreCase("Y");
        System.out.print("Allow public key retrieval ?(Y/n): ");
        boolean allowPublicKeyRetrieval = scanner.next().equalsIgnoreCase("Y");

        Credentials credentials = new Credentials();

        credentials
                .setHost(host)
                .setDatabaseName(databaseName)
                .setUser(user)
                .setPwd(pwd)
                .setPort(port)
                .setTrustCertificate(trustCertificate)
                .setUseSSL(useSSL)
                .setAllowPublicKeyRetrieval(allowPublicKeyRetrieval);

        return credentials;
    }
}
