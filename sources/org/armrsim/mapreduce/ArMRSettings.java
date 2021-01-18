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

    // When the contents of the buffer reach a certain threshold size (mapreduce.map.sort.spill.percent,
    // which has the default value 0.80, or 80%), a background thread will start to spill the contents
    public static double map_sort_spill_percent = 0.8;

    // When the map task starts producing output it is first written to a memory buffer which is 100 MB by defaul
    public static int task_io_sort_mb = 100;

    // 	The number of streams to merge at once while sorting files. This determines the number of open file handles.
    public static int task_io_sort_factor = 100;

    // number of reducers
    public static int job_reduces = 2;

    // uber jobs will be used only if the number of mappers is less or equal to the value of mapreduce.job.ubertask.maxmaps
    public static int job_ubertask_maxmaps = 9;

    // uber jobs will be used only if the number of reducers is less or equal to the value of mapreduce.job.ubertask.maxreduces
    public static int job_ubertask_maxreduces = 1;

    // memory of each map
    public static int map_memory_mb = 1024;

    // memory of each reduce
    public static int reduce_memory_mb = 1024;

    // the usage threshold at which an in-memory merge will be initiated, expressed as a percentage of the total memory
    public static double reduce_shuffle_merge_percent = 0.9;

}
