package JVM;

import java.net.URL;

/**
 * 该类用于测试类加载器
 */
public class ClassLoaderTest {

    public static void main(String[] args) {
        printApp();
    }

    public static void printBootstrap() {
        System.out.println(System.getProperty("sun.boot.class.path"));

        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        for (int i = 0; i < urls.length; i++) {
            System.out.println(urls[i].toExternalForm());
        }
    }

    public static void printExt() {
        System.out.println(System.getProperty("java.ext.dirs"));
    }

    public static void printApp() {
        System.out.println(System.getProperty("java.class.path"));
    }
}
