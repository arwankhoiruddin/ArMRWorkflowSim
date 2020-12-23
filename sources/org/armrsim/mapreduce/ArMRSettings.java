package org.armrsim.mapreduce;

import java.util.ArrayList;
import java.util.List;

public class ArMRSettings {
    public static boolean heterogeneous = false;
    public static int replication = 3; // default: 3
    public static int jobReduces = 2;
    public static int numHost = 4;
    public static int numCore = 12;

    // number of vm
    // this will be the processing VM
    public static int vmNum = 2;

    public static List<MRTask> taskList = new ArrayList<>();

    /*
    https://ercoppa.github.io/HadoopInternals/HadoopConfigurationParameters.html
    Hadoop settings
    mapreduce.task.io.sort.mb	100
    mapreduce.map.sort.spill.percent	0.8
    mapreduce.task.io.sort.factor	100
    mapreduce.map.combine.minspills	3
    mapreduce.job.reduces	1
    mapreduce.job.ubertask.maxmaps	9
    mapreduce.job.ubertask.maxreduces	1
    mapreduce.map.memory.mb	1024
    mapreduce.reduce.memory.mb	1024
    mapreduce.reduce.shuffle.merge.percent	0.9
     */
}
