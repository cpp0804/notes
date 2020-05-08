package thread;

/**
 * 单例模式的几种实现
 */
public class Singleton {

    /**
     * 饿汉模式
     */
    public static class Singleton1 {
        private static Singleton1 instance = new Singleton1();

        private Singleton1() {

        }

        public static Singleton1 getInstance() {
            return instance;
        }
    }

    /**
     * 懒汉模式
     */
    public static class Singleton2 {
        private static Singleton2 instance;

        private Singleton2() {

        }

        public static Singleton2 getInstance() {
            if (instance == null) {
                instance = new Singleton2();
            }
            return instance;
        }
    }

    /**
     * 线程安全懒汉模式
     */
    public static class Singleton3 {
        private static Singleton3 instance;

        private Singleton3() {

        }

        public static synchronized Singleton3 getInstance() {
            if (instance == null) {
                instance = new Singleton3();
            }
            return instance;
        }
    }

    /**
     * 双重检查模式(DCL)
     */
    public static class Singleton4 {
        private static volatile Singleton4 instance;

        private Singleton4() {

        }

        public static synchronized Singleton4 getInstance() {
            if (instance == null) {
                synchronized (Singleton4.class) {
                    if (instance == null) {
                        instance = new Singleton4();
                    }
                }
            }
            return instance;
        }
    }

    /**
     * 静态内部模式
     */
    public static class Singleton5 {
        public static Singleton5 getInstance() {
            return SingletonHolder.instance;
        }

        private static class SingletonHolder {
            private static final Singleton5 instance = new Singleton5();
        }
    }

    /**
     * 枚举单例
     */
    public class Singleton6 {

    }

    /**
     * 使用容器
     */
    public class Singleton7 {

    }
}
