package org.armrsim.mapreduce;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.workflowsim.Job;
import org.workflowsim.Task;

public class MRTask extends Job {

    final JobType jobType;

    public MRTask(
            final int cloudletId,
            final long cloudletLength,
            final JobType jobType) {
        super(
                cloudletId,
                cloudletLength
                );

        this.jobType = jobType;
    }
}
