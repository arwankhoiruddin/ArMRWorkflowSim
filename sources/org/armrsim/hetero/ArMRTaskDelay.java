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
    private static long speculativeDelay(long taskDuration) {
        // run in (replication) number of nodes
        // then generate random number in each nodes to represent the degradation
        long fastest = 10000000; // to take the fastest execution among the different executions
        for (int i=0; i< ArMRSettings.replication; i++) {
            // add extra time to send the tasks speculated
            long duration = taskDuration + Math.round(0.01 * taskDuration);

            // incorporate multi tenancy problem in cloud
            duration = multiTenancyDelay(duration);
            // only take the fastest one
            if (fastest > duration)
                fastest = duration;
        }
        return fastest;
    }

    /**
     * Add delay due to multi tenancy problem (network/CPU sharing between cloud users/apps)
     * @param taskDuration
     * @return
     *
     * Virtualization + Resource Sharing = Multi-Tenancy
     * H. AlJahdali, A. Albatli, P. Garraghan, P. Townend, L. Lau and J. Xu, "Multi-tenancy in Cloud Computing," 2014 IEEE 8th International Symposium on Service Oriented System Engineering, Oxford, 2014, pp. 344-351, doi: 10.1109/SOSE.2014.50.
     */
    private static long multiTenancyDelay(long taskDuration) {
        long duration = 0;
        // interference from other application/user
        duration += (taskDuration * (1 + new Random().nextDouble())); // can double the duration
        return duration;
    }

    /**
     * Add delay to the taskDuration
     * @param taskDuration
     * @return
     */
    public static long addDelay(long taskDuration) {
        long durationWithDelay = 0;
        // add task speculation
        if (ArMRSettings.speculate) {
            durationWithDelay = speculativeDelay(taskDuration);
        } else {
            durationWithDelay = multiTenancyDelay(taskDuration);
        }
        return durationWithDelay;
    }
}
