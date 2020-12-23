/**
 * Copyright 2012-2013 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.arwan.armrsim.example;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import org.armrsim.mapreduce.ArMRPreps;
import org.armrsim.mapreduce.Map;
import org.armrsim.mapreduce.Reduce;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.workflowsim.CondorVM;
import org.workflowsim.Task;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.Job;
import org.workflowsim.WorkflowEngine;
import org.workflowsim.WorkflowPlanner;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;
import org.workflowsim.utils.Parameters.ClassType;

import static org.armrsim.mapreduce.ArMRPreps.*;
import static org.armrsim.mapreduce.ArMRSettings.vmNum;

/**
 * This WorkflowSimExample creates a workflow planner, a workflow engine, and
 * one schedulers, one data centers and 20 vms. You should change daxPath at
 * least. You may change other parameters as well.
 *
 * @author Weiwei Chen
 * @since WorkflowSim Toolkit 1.0
 * @date Apr 9, 2013
 */
public class ArMRSim {


    ////////////////////////// STATIC METHODS ///////////////////////
    /**
     * Creates main() to run this example This example has only one datacenter
     * and one storage
     */
    public static void main(String[] args) {
        int dataSize = 1*1024;
        int blockSize = 64;
        prepareData(dataSize, blockSize);

        runSimulation(1);
    }

    public static void runSimulation(int numExperiments) {
        for (int r=0; r<numExperiments; r++) {
            System.out.println("Execution number: " + r);
            try {
                // First step: Initialize the WorkflowSim package.
                /**
                 * However, the exact number of vms may not necessarily be vmNum If
                 * the data center or the host doesn't have sufficient resources the
                 * exact vmNum would be smaller than that. Take care.
                 */
                String daxPath = ArMRPreps.daxPath();

                /**
                 * Since we are using MINMIN scheduling algorithm, the planning
                 * algorithm should be INVALID such that the planner would not
                 * override the result of the scheduler
                 */
                Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.MINMIN;
                Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.INVALID;
                ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.SHARED;

                /**
                 * No overheads
                 */
                OverheadParameters op = new OverheadParameters(0, null, null, null, null, 0);

                /**
                 * No Clustering
                 */
                ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;
                ClusteringParameters cp = new ClusteringParameters(0, 0, method, null);

                /**
                 * Initialize static parameters
                 */
                Parameters.init(vmNum, daxPath, null,
                        null, op, cp, sch_method, pln_method,
                        null, 0);
                ReplicaCatalog.init(file_system);

                // before creating any entities.
                int num_user = 1;   // number of grid users
                Calendar calendar = Calendar.getInstance();
                boolean trace_flag = true;  // mean trace events

                // Initialize the CloudSim library
                CloudSim.init(num_user, calendar, trace_flag);

                WorkflowDatacenter datacenter0 = createDatacenter("Datacenter_0");

                /**
                 * Create a WorkflowPlanner with one schedulers.
                 */
                WorkflowPlanner wfPlanner = new WorkflowPlanner("planner_0", 2);
                /**
                 * Create a WorkflowEngine.
                 */
                WorkflowEngine wfEngine = wfPlanner.getWorkflowEngine();
                /**
                 * Create a list of VMs.The userId of a vm is basically the id of
                 * the scheduler that controls this vm.
                 */
                List<CondorVM> vmlist0 = createVM(wfEngine.getSchedulerId(0), Parameters.getVmNum());

                /**
                 * Submits this list of vms to this WorkflowEngine.
                 */
                wfEngine.submitVmList(vmlist0, 0);

                /**
                 * Binds the data centers with the scheduler.
                 */
                wfEngine.bindSchedulerDatacenter(datacenter0.getId(), 0);
                CloudSim.startSimulation();
                List<Job> outputList0 = wfEngine.getJobsReceivedList();
                CloudSim.stopSimulation();
                printJobList(outputList0);
            } catch (Exception e) {
                Log.printLine("The simulation has been terminated due to an unexpected error");
            }
        }
    }

    /**
     * Prints the job objects
     *
     * @param list list of jobs
     */
    protected static void printJobList(List<Job> list) {
        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Job ID" + indent + "Task ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + indent
                + "Time" + indent + "Start Time" + indent + "Finish Time" + indent + "Depth");
        DecimalFormat dft = new DecimalFormat("###.##");
        for (Job job : list) {
            if (job instanceof Map)
                System.out.println("Map task");
            else if (job instanceof Reduce)
                System.out.println("Reduce task");
            else
                System.out.println("Not both");
            Log.print(indent + job.getCloudletId() + indent + indent);
            if (job.getClassType() == ClassType.STAGE_IN.value) {
                Log.print("Stage-in");
            }
            for (Task task : job.getTaskList()) {
                Log.print(task.getCloudletId() + ",");
            }
            Log.print(indent);

            if (job.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");
                Log.printLine(indent + indent + job.getResourceId() + indent + indent + indent + job.getVmId()
                        + indent + indent + indent + dft.format(job.getActualCPUTime())
                        + indent + indent + dft.format(job.getExecStartTime()) + indent + indent + indent
                        + dft.format(job.getFinishTime()) + indent + indent + indent + job.getDepth());
            } else if (job.getCloudletStatus() == Cloudlet.FAILED) {
                Log.print("FAILED");
                Log.printLine(indent + indent + job.getResourceId() + indent + indent + indent + job.getVmId()
                        + indent + indent + indent + dft.format(job.getActualCPUTime())
                        + indent + indent + dft.format(job.getExecStartTime()) + indent + indent + indent
                        + dft.format(job.getFinishTime()) + indent + indent + indent + job.getDepth());
            }
        }
    }
}
