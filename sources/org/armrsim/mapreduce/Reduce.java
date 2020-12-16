package org.armrsim.mapreduce;

public class Reduce extends MRTask{
    public Reduce(final int taskID, final int taskLength) {
        super(taskID, taskLength, JobType.REDUCE);
    }
}
