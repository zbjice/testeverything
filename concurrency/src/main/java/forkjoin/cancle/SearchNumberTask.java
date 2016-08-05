package forkjoin.cancle;

import com.sun.javafx.logging.PulseLogger;

import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * 功能:
 * 作者: ldl
 * 时间: 2016-08-05 22:39
 */
public class SearchNumberTask extends RecursiveTask<Integer> {

    private int start, end;
    private int number;
    private TaskManager manager;
    private final static int NOT_FOUND = -1;

    private int[] numbers;

    public SearchNumberTask(int start, int end, int number, TaskManager manager, int[] numbers) {
        this.start = start;
        this.end = end;
        this.number = number;
        this.manager = manager;
        this.numbers = numbers;
    }

    @Override
    protected Integer compute() {
        System.out.println("Task: " + start + ":" + end);
        int ret;
        if (end - start > 10) {
            ret = launchTasks();
        } else {
            ret = lookForNumber();
        }
        return ret;
    }

    private int lookForNumber() {

        for (int i = start; i < end; i++) {
            if (numbers[i] == number) {
                System.out.printf("Task: Number %d found in position %d\n", number, i);
                manager.cancleTasks(this);
                return i;
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return NOT_FOUND;
    }

    private int launchTasks() {
        int mid = (start + end) / 2;
        SearchNumberTask task1 = new SearchNumberTask(start, mid, number, manager, numbers);
        SearchNumberTask task2 = new SearchNumberTask(mid, end, number, manager, numbers);

        manager.addTask(task1);
        manager.addTask(task2);

        task1.fork();
        task2.fork();

        int returnValue;
        returnValue = task1.join();
        if (returnValue != -1) {
            return returnValue;
        }
        returnValue = task2.join();
        return returnValue;
    }

    public void writeCancelMessage(){
        System.out.printf("Task: Cancelled task from %d to %d\n",start,end);
    }
}
