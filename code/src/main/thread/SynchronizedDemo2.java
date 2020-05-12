package thread;

public class SynchronizedDemo2 {
        Object object = new Object();
        public void method1() {
            synchronized (object) {

            }
        }

        public synchronized void method2() {

        }

}
