package com.example.proiectssc.Caches;

import com.example.proiectssc.Responses.Actions;
import com.example.proiectssc.Others.CMD;

import java.util.*;

public class DirectMappedCache extends Cache {

    public DirectMappedCache(int blockSize, int cacheLines, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L1, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L2) {
        super(blockSize, cacheLines, L1, L2);
    }

    public Actions runCmd(String cmd, int address, int data) {
        Actions actions = new Actions();

        actions.getActions().add("Doing operation : " + cmd +" data : " + data + " from/to " + address);

        int blockNr = address / getBlockSize();
        int index = address % getBlockSize();

        int l1CacheLineIndex = blockNr % (getCacheLines() / 2);
        int l2CacheLineIndex = blockNr % getCacheLines();

        HashMap<Integer, ArrayList<Integer>> helperL1 = getL1().get(l1CacheLineIndex);
        HashMap<Integer, ArrayList<Integer>> helperL2 = getL2().get(l2CacheLineIndex);

        if (helperL1 != null && helperL1.containsKey(blockNr)) {
            actions.getActions().add("Hit in L1");

            if (cmd.equals(CMD.READ.toString())) {
                readData(helperL1, actions, blockNr, index);
            } else if (cmd.equals(CMD.WRITE.toString())) {
                helperL1.get(blockNr).set(index, data);
                helperL2.get(blockNr).set(index, data);

                getL1().put(l1CacheLineIndex, helperL1);
                getL2().put(l2CacheLineIndex, helperL2);
            }
        } else if (helperL2 != null && helperL2.containsKey(blockNr)) {
            actions.getActions().add("Hit in L2");

            if (cmd.equals(CMD.READ.toString())) {
                readData(helperL2, actions, blockNr, index);
            } else if (cmd.equals(CMD.WRITE.toString())) {
                helperL2.get(blockNr).set(index, data);
            }

            if(helperL1 != null){
                actions.getActions().add("Block " + getBinary(Integer.parseInt(helperL1.keySet().iterator().next() + "")) + " gets replaced in L1 cache");
            }

            getL1().put(l1CacheLineIndex, new HashMap<>());
            getL1().get(l1CacheLineIndex).put(blockNr, helperL2.get(blockNr));
            getL2().put(l2CacheLineIndex, helperL2);
        } else {
            actions.getActions().add("Address not found");
            ArrayList<Integer> helper = writeData(cmd, index, data);

            if (helperL1 == null && helperL2 == null) {
                helperL1 = new HashMap<>();
                helperL2 = new HashMap<>();
                helperL1.put(blockNr, helper);
                helperL2.put(blockNr, helper);

                getL1().put(l1CacheLineIndex, helperL1);
                getL2().put(l2CacheLineIndex, helperL2);
            } else if (helperL1 != null && helperL2 == null) {
                actions.getActions().add("Block " + getBinary(Integer.parseInt(helperL1.keySet().iterator().next() + "")) + " gets replaced in L1 cache");

                helperL2 = new HashMap<>();
                helperL2.put(blockNr, helper);

                getL1().put(l1CacheLineIndex, helperL1);
                getL2().put(l2CacheLineIndex, helperL2);
            } else {
                if(helperL1 != null) {
                    actions.getActions().add("Block " + getBinary(Integer.parseInt(helperL1.keySet().iterator().next() + "")) + " gets replaced in L1 cache");
                }
                actions.getActions().add("Block " + getBinary(Integer.parseInt(helperL2.keySet().iterator().next() + "")) + " gets replaced in L2 cache");

                getL1().put(l1CacheLineIndex, new HashMap<>());
                getL1().get(l1CacheLineIndex).put(blockNr, helper);

                getL2().put(l2CacheLineIndex, new HashMap<>());
                getL2().get(l2CacheLineIndex).put(blockNr, helper);
            }
        }
        return actions;
    }
}