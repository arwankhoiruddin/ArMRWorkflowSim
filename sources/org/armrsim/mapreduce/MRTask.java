package org.armrsim.mapreduce;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;

public class MRTask extends Cloudlet {

    public static UtilizationModel utilizationModel = new UtilizationModelFull();

    public MRTask(
            final int cloudletId,
            final long cloudletLength,
            final JobType jobType) {
        super(
                cloudletId,
                cloudletLength,
                1, // number of CPU per task is only one
                100, // won't consider file size
                100, // won't consider file output
                utilizationModel,
                utilizationModel,
                utilizationModel,
                false);
        vmId = -1;
        accumulatedBwCost = 0.0;
        costPerBw = 0.0;
    }
}
