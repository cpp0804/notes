package thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 测试Fork/Join 实现斐波那契数列: 1、1、2、3、5、8、13、21、34、…… 公式 : F(1)=1，F(2)=1, F(n)=F(n-1)+F(n-2)(n>=3，n∈N*)
 */
public class Fibonacci extends RecursiveTask<Integer> {

    public static void main(String[] args) {
        // 最大并发数4
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        Fibonacci fibonacci = new Fibonacci(20);
        long startTime = System.currentTimeMillis();
        Integer result = forkJoinPool.invoke(fibonacci);
        long endTime = System.currentTimeMillis();
        System.out.println("Fork/join sum: " + result + " in " + (endTime - startTime) + " ms.");
    }

    final int n;

    public Fibonacci(int n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n <= 1) {
            return n;
        }

        Fibonacci f1 = new Fibonacci(n - 1);
        Fibonacci f2 = new Fibonacci(n - 2);
        f1.fork();
        f2.fork();

        return f1.join() + f2.join();
    }
}
