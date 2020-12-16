package org.armrsim.mapreduce;

public class Shuffle extends MRTask{
    public Shuffle(final int taskID, final int taskLength) {
        super(taskID, taskLength, JobType.SHUFFLE);
    }
}
