import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Installer {

    private static final String REGEX = ".*AssignmentTodo.*\\.jar$";
    private static String name;

    public static void main(String[] args) {
        if (args.length != 1 && args.length != 2)
            exit("Incorrect number of arguments, expected <download URL> <install directory>");
        name = args.length == 2 ? scanJarName(args[1]) : scanJarName(System.getProperty("user.dir"));
        File installerFile = installerFile();
        if (installerFile.exists()) {
            System.out.println("Found Installer file");
            installerFile.deleteOnExit();
        }
        String jarURL = args[0];
        System.out.println("Installing from " + jarURL);
        URL url = createURL(jarURL);
        File install = new File(args[1], name);
        if (url != null) download(url, install);
        else System.err.println("Failed to createURL from " + jarURL);
    }

    private static File installerFile() {
        String path = System.getProperty("user.home") +
                File.separator +
                "AssignmentTodo";
        return new File(path, Installer.class.getName() + ".class");
    }

    private static URL createURL(String urlPath) {
        try {
            return new URL(urlPath);
        } catch (MalformedURLException e) {
            System.err.println("Passed bad url: " + urlPath);
        }
        return null;
    }

    private static void download(URL url, File install) {
        try {
            if (!install(url, install)) exit("Failed to Install Jar");
            System.out.println("Restarting");
            Runtime.getRuntime().exec("java -jar " + name);
        } catch (IOException e) {
            exit("Failed to Properly Install due to IOException: " + e.getMessage());
        }
    }

    private static boolean install(URL url, File install) {
        try {
            System.out.println("Downloading");
            InputStream stream = url.openStream();
            Files.copy(stream, install.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Installed");
            return true;
        } catch (IOException e) {
            System.err.println("Failed to install");
        }
        return false;
    }

    private static void exit(String message) {
        System.err.println(message);
        try {
            Runtime.getRuntime().exec("java -jar " + name + " --update=fail --updateLog=\"" + message + "\"");
        } catch (NullPointerException | IOException e) {
            System.err.println("Failed to restart jar: " + e.getMessage());
        }
        System.exit(1);
    }

    private static String scanJarName(String dirName) {
        File dir = new File(dirName);
        if (dir.exists() && dir.isDirectory()) {
            String[] files = dir.list((dir1, name) -> name.matches(REGEX));
            if (files != null && files.length == 1)
                return files[0];
        }
        throw new NullPointerException("Directory did not exist");
    }

}