package org.arwan.armrsim;

public class ArMRSettings {
    public static boolean heterogeneous = false;
    public static int replication = 3;
    public static int jobReduces = 2;

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
