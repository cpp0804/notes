package thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * 测试使用Fork/Join框架 计算1+2+3+…+10000
 */
public class CountTask extends RecursiveTask<Integer> {
    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //生成一个计算任务来计算1+2+3+4
        CountTask task = new CountTask(1, 10000);
        Future<Integer> result = forkJoinPool.submit(task);
        try {
            System.out.println(result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static final int THRESHOLD = 1000;

    private int start;
    private int end;

    public CountTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;

        boolean canCompute = (end - start) <= THRESHOLD;

        if (canCompute) {
            System.out.println(Thread.currentThread().getName() + " 开始执行: " + start + "-" + end);
            for (int i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            int middle = (start + end) / 2;
            CountTask leftTask = new CountTask(start, middle);
            CountTask rightTask = new CountTask(middle + 1, end);

            //执行子任务
            leftTask.fork();
            rightTask.fork();

            //等待子任务执行完并获得结果
            int leftResult = leftTask.join();
            int rightResult = rightTask.join();

            //合并子任务
            sum = leftResult + rightResult;
        }

        return sum;
    }
}
