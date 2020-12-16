package org.armrsim.mapreduce;

public class Map extends MRTask{
    public Map(final int taskID, final int taskLength) {
        super(taskID, taskLength, JobType.MAP);
    }
}
