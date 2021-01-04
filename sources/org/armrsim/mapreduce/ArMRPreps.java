package org.armrsim.mapreduce;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowDatacenter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.armrsim.mapreduce.ArMRSettings.numCore;
import static org.armrsim.mapreduce.ArMRSettings.numHost;

public class ArMRPreps {
    private static int dataSize;
    private static int blockSize;

    private static String writeJobLine(int index, JobType jobType, double runTime) {
        // based on job type
        StringBuffer tmp = new StringBuffer();
        if (jobType == JobType.MAP) {
            tmp.append("<job id=\"MAP00" + index + "\" namespace=\"MapReduce\" name=\"MAP\" version=\"1.0\"");
        } else if (jobType == JobType.SHUFFLE) {
            int numDataBlocks = dataSize / blockSize;
            if (dataSize % blockSize > 0) numDataBlocks++;
            // we have map_sort_spill_percent setting
            // so we put the dependency based on that setting
            int firstWave = (int) Math.round(numDataBlocks * ArMRSettings.map_sort_spill_percent);
            // dependency of Shuffle to Map
            if (index < firstWave)
                tmp.append("<job id=\"SHUF0\" namespace=\"MapReduce\" name=\"SHU\" version=\"1.0\"");
            else
                tmp.append("<job id=\"SHUF1\" namespace=\"MapReduce\" name=\"SHU\" version=\"1.0\"");
        } else {
            tmp.append("<job id=\"RED00" + index + "\" namespace=\"MapReduce\" name=\"RED\" version=\"1.0\"");
        }

        // add runtime
        tmp.append(" runtime=\"" + runTime + "\"></job>\n");

        // task dependency
        // reduce -> shufflesort -> map

        // closing
        return tmp.toString();
    }

    /**
     * Write task flow in temp.xml
     * Reduce depends on Shuffle. Shuffle depends on Map
     *
     * @param dataSize
     * @param blockSize
     * @return
     */
    private static String writeTaskDependency(int dataSize, int blockSize) {
        //
        StringBuffer tmp = new StringBuffer();
        int numDataBlocks = dataSize / blockSize;
        if (dataSize % blockSize > 0) numDataBlocks++;

        // we have map_sort_spill_percent setting
        // so we put the dependency based on that setting
        int firstWave = (int) Math.round(numDataBlocks * ArMRSettings.map_sort_spill_percent);
        System.out.println("Num data block: " + numDataBlocks);
        System.out.println("First wave: " + firstWave);
        // dependency of Shuffle to Map
        for (int i=0; i<firstWave; i++) {
            tmp.append("<child ref=\"SHUF0\"><parent ref=\"MAP00" + i + "\"/></child>\n");
        }
        for (int i=firstWave; i<numDataBlocks; i++) {
            tmp.append("<child ref=\"SHUF1\"><parent ref=\"MAP00" + i + "\"/></child>\n");
        }

        // dependency of Reduce to Shuffle
        for (int i = 0; i< ArMRSettings.job_reduces; i++) {
            tmp.append("<child ref=\"RED00" + i + "\"><parent ref=\"SHUF" + i +"\"/></child>\n");
        }

        return tmp.toString();
    }

    /**
     * Write data to temp.xml in config -> dax folder
     * @param dataSize
     * @param blockSize
     * @return
     */
    public static String prepareData(int dataSize, int blockSize) {
        ArMRPreps.blockSize = blockSize;
        ArMRPreps.dataSize = dataSize;

        String currentDir = System.getProperty("user.dir");
        String dataPath = currentDir + "/config/dax/temp.xml";
        FileWriter xmlData;
        try {
            xmlData = new FileWriter(dataPath);
            xmlData.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xmlData.write("<adag xmlns=\"http://pegasus.isi.edu/schema/DAX\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://pegasus.isi.edu/schema/DAX http://pegasus.isi.edu/schema/dax-2.1.xsd\" version=\"2.1\" count=\"1\" index=\"0\" name=\"test\" jobCount=\"25\" fileCount=\"0\" childCount=\"20\">\n");

            int numDataBlocks = dataSize / blockSize;
            if (dataSize % blockSize > 0) numDataBlocks++;
            double mapRunTime = 20;

            for (int i=0; i<numDataBlocks; i++) {
                xmlData.write(writeJobLine(i, JobType.MAP, mapRunTime));
            }

            xmlData.write(writeJobLine(0, JobType.SHUFFLE, 0));

            double red0RunTime = mapRunTime * (numDataBlocks * ArMRSettings.map_sort_spill_percent);
            double red1RunTime = mapRunTime * (numDataBlocks * (1-ArMRSettings.map_sort_spill_percent));
            xmlData.write(writeJobLine(0, JobType.REDUCE, red0RunTime));
            xmlData.write(writeJobLine(1, JobType.REDUCE, red1RunTime));

//            for (int i=0; i<ArMRSettings.jobReduces; i++) {
//                xmlData.write(writeJobLine(i, JobType.REDUCE, redRunTime));
//            }
            xmlData.write(writeTaskDependency(dataSize, blockSize));

            xmlData.write("</adag>");
            xmlData.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String daxPath() {
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current directory: " + currentDir);
        String daxPath = currentDir + "/config/dax/temp.xml";
        return daxPath;
    }

    public static WorkflowDatacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store one or more
        //    Machines
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
        //    create a list to store these PEs before creating
        //    a Machine.

        for (int i = 1; i <= numHost; i++) {
            List<Pe> peList1 = new ArrayList<>();
            int mips = 10000;
            // 3. Create PEs and add these into the list.
            //for a quad-core machine, a list of 4 PEs is required:
            for (int core=0; core < numCore; core++) {
                peList1.add(new Pe(core, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
            }

            int hostId = 0;
            int ram = 2048; //host memory (MB)
            long storage = 1000000; //host storage
            int bw = 10000;
            hostList.add(
                    new Host(
                            hostId,
                            new RamProvisionerSimple(ram),
                            new BwProvisionerSimple(bw),
                            storage,
                            peList1,
                            new VmSchedulerSpaceShared(peList1))); // This is our first machine
            //hostId++;
        }

        // 4. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        String arch = "x86";      // system architecture
        String os = "Linux";          // operating system
        String vmm = "Xen";
        double time_zone = 8.0;         // time zone this resource located
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<>();	//we are not adding SAN devices by now
        WorkflowDatacenter datacenter = null;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        // 5. Finally, we need to create a storage object.
        /**
         * The bandwidth within a data center in MB/s.
         */
        int maxTransferRate = 15;// the number comes from the futuregrid site, you can specify your bw

        try {
            // Here we set the bandwidth to be 15MB/s
            HarddriveStorage s1 = new HarddriveStorage(name, 1e12);
            s1.setMaxTransferRate(maxTransferRate);
            storageList.add(s1);
            datacenter = new WorkflowDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }

    public static List<CondorVM> createVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<CondorVM> list = new LinkedList<>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = numCore; //number of vm cores
        String vmm = "Xen"; //VMM name

        //create VMs
        CondorVM[] vm = new CondorVM[vms];
        for (int i = 0; i < vms; i++) {
            double ratio = 1.0;
            vm[i] = new CondorVM(i, userId, mips * ratio, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            list.add(vm[i]);
        }
        return list;
    }
}
