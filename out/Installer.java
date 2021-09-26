import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Installer {

    private static final String REGEX = ".*AssignmentTodo.*\\.jar$";
    private static int tries = 4;
    private static final String name = scanJarName();

    public static void main(String[] args) {
        if (args.length != 1)
            exit("Incorrect number of arguments, expected <download URL>");

        final String installerFilename = Installer.class.getName() + ".class";
        final String dir = System.getProperty("user.dir");
        File installerFile = new File(dir, installerFilename);
        if (installerFile.exists()) {
            System.out.println("Found Installer file");
            installerFile.deleteOnExit();
        }
        String jarURL = args[0];
        System.out.println("Installing from " + jarURL);
        URL url = createURL(jarURL);
        if (url != null) download(url);
        else System.err.println("Failed to createURL from " + jarURL);
    }

    private static URL createURL(String urlPath) {
        try {
            return new URL(urlPath);
        } catch (MalformedURLException e) {
            System.err.println("Passed bad url: " + urlPath);
        }
        return null;
    }

    private static void download(URL url) {
        try {
            System.out.println("Downloading");
            InputStream stream = url.openStream();
            File file = new File(System.getProperty("user.dir"), name);
            Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Installed");
            System.out.println("Restarting");
            Runtime.getRuntime().exec("java -jar " + name);
        } catch (IOException e) {
            if (tries > 0) {
                tries--;
                System.out.println("Failed attempt, remaining: " + tries);
                download(url);
            } else exit("Failed to replace jar");
        }
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

    private static String scanJarName() {
        final String dirName = System.getProperty("user.dir");
        File dir = new File(dirName);
        if (dir.exists() && dir.isDirectory()) {
            String[] files = dir.list((dir1, name) -> name.matches(REGEX));
            if (files != null && files.length == 1)
                return files[0];
        }
        throw new NullPointerException("Directory did not exist");
    }

}