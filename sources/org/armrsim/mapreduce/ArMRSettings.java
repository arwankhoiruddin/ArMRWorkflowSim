package org.armrsim.mapreduce;

import java.util.ArrayList;
import java.util.List;

public class ArMRSettings {
    public static boolean heterogeneous = false;
    public static int replication = 3; // default: 3
    public static int jobReduces = 2;
    public static int numHost = 4;
    public static int numCore = 12;

    public static boolean speculate = true;

    // number of vm
    // this will be the processing VM
    public static int vmNum = 2;

    public static List<MRTask> taskList = new ArrayList<>();

    /*
    https://ercoppa.github.io/HadoopInternals/HadoopConfigurationParameters.html
    Hadoop settings
     */
    public static int task_io_sort_mb = 100;
    public static double map_sort_spill_percent = 0.8;
    public static int task_io_sort_factor = 100;
    public static int map_combine_minspills = 3;
    public static int job_reduces = 2;
    public static int job_ubertask_maxmaps = 9;
    public static int job_ubertask_maxreduces = 1;
    public static int map_memory_mb = 1024;
    public static int reduce_memory_mb = 1024;
    public static double reduce_shuffle_merge_percent = 0.9;

}
