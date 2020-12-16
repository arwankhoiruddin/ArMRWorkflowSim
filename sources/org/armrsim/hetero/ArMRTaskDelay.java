package org.armrsim.hetero;

import org.armrsim.mapreduce.ArMRSettings;

import java.util.Random;

/**
 * This class manages delay due to task speculation happens in MapReduce
 *
 * In Hadoop, MapReduce breaks jobs into tasks and these tasks run parallel rather than sequential, thus reduces overall execution time.
 * This model of execution is sensitive to slow tasks (even if they are few in numbers) as they slow down the overall execution of a job.
 * There may be various reasons for the slowdown of tasks, including hardware degradation or software misconfiguration,
 * but it may be difficult to detect causes since the tasks still complete successfully,
 * although more time is taken than the expected time. Hadoop doesn’t try to diagnose and fix slow running tasks,
 * instead, it tries to detect them and runs backup tasks for them. This is called speculative execution in Hadoop.
 * These backup tasks are called Speculative tasks in Hadoop.
 *
 * (source: https://data-flair.training/blogs/speculative-execution-in-hadoop-mapreduce/)
 */
public class ArMRTaskDelay {

    /**
     * Add delay due to speculative execution
     * @param taskDuration
     * @return
     */
    private static double speculativeDelay(double taskDuration) {
        // run in (replication) number of nodes
        // then generate random number in each nodes to represent the degradation
        double fastest = 10000000; // to take the fastest execution among the different executions
        for (int i=0; i< ArMRSettings.replication; i++) {
            double degradation = new Random().nextDouble() * taskDuration;
            taskDuration += degradation;
            // only take the fastest one
            if (fastest > taskDuration)
                fastest = taskDuration;
        }
        return fastest;
    }

    /**
     * Add delay to the taskDuration
     * @param taskDuration
     * @return
     */
    public static double addDelay(double taskDuration) {
        double durationWithDelay = 0;

        return durationWithDelay;
    }
}
