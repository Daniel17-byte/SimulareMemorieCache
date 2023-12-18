package com.example.proiectssc.Caches;

import com.example.proiectssc.Responses.Actions;
import com.example.proiectssc.Others.CMD;
import lombok.Getter;

import java.util.*;

public class SetAssociativeCache extends Cache {
    private final ArrayList<Integer> frequencyListL1 = new ArrayList<>();
    private final ArrayList<Integer> frequencyListL2 = new ArrayList<>();
    @Getter
    private final int k;

    public SetAssociativeCache(int blockSize, int cacheLines, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L1, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L2, int k) {
        super(blockSize, cacheLines, L1, L2);
        this.k = k;
    }

    public Actions runCmd(String cmd, int address, int data) {
        Actions actions = new Actions();

        actions.getActions().add("Doing operation : " + cmd +" data : " + data + " from/to " + address);

        int blockNr = address / getBlockSize();
        int index = address % getBlockSize();

        int l1CacheSets = (getCacheLines() / 2) / k;
        int l2CacheSets = getCacheLines() / k;

        frequencyListL1.add(blockNr);
        frequencyListL2.add(blockNr);

        int l1CacheSetIndex = blockNr % l1CacheSets;
        int l2CacheSetIndex = blockNr % l2CacheSets;

        HashMap<Integer, ArrayList<Integer>> helperL1 = getL1().get(l1CacheSetIndex);
        HashMap<Integer, ArrayList<Integer>> helperL2 = getL2().get(l2CacheSetIndex);

        if (helperL1 != null && helperL1.containsKey(blockNr)) {
            actions.getActions().add("Hit in L1");

            if(cmd.equals(CMD.READ.toString())) {
                readData(helperL1, actions, blockNr, index);
            } else if(cmd.equals(CMD.WRITE.toString())){
                helperL1.get(blockNr).set(index, data);
                helperL2.get(blockNr).set(index, data);
                getL1().put(l1CacheSetIndex, helperL1);
                getL2().put(l2CacheSetIndex, helperL2);
            }
        } else if (helperL2 != null && helperL2.containsKey(blockNr)) {
            actions.getActions().add("Hit in L2");

            if (cmd.equals(CMD.READ.toString())) {
                readData(helperL2, actions, blockNr, index);
            } else if (cmd.equals(CMD.WRITE.toString())) {
                helperL2.get(blockNr).set(index, data);
                getL2().put(l2CacheSetIndex, helperL2);
            }

            if (helperL1 != null && helperL1.size() < k) {
                ArrayList<Integer> helper = writeData(cmd, index, data);
                helperL1.put(blockNr, helper);
                getL1().put(l1CacheSetIndex, helperL1);
            }

            if (helperL1 != null){
                actions.getActions().add(replaceBlockInCache(helperL1, frequencyListL1, "L1"));
                helperL1.put(blockNr, helperL2.get(blockNr));
            }

            getL1().put(l1CacheSetIndex, helperL1);
        } else {
            actions.getActions().add("Address not found");
            if (helperL1 == null && helperL2 == null) {
                ArrayList<Integer> helper = writeData(cmd, index, data);

                helperL1 = new HashMap<>();
                helperL2 = new HashMap<>();
                helperL1.put(blockNr, helper);
                helperL2.put(blockNr, helper);

                getL1().put(l1CacheSetIndex, helperL1);
                getL2().put(l2CacheSetIndex, helperL2);
            } else if (helperL1 != null && helperL2 == null) {
                actions.getActions().add(replaceBlockInCache(helperL1, frequencyListL1, "L1"));

                ArrayList<Integer> helper = writeData(cmd, index, data);

                helperL2 = new HashMap<>();
                helperL2.put(blockNr, helper);
                helperL1.put(blockNr, helper);

                getL1().put(l1CacheSetIndex, helperL1);
                getL2().put(l2CacheSetIndex, helperL2);
            } else if (helperL1 != null && helperL1.size() == k && helperL2.size() == k) {
                actions.getActions().add(replaceBlockInCache(helperL1, frequencyListL1, "L1"));
                actions.getActions().add(replaceBlockInCache(helperL2, frequencyListL2, "L2"));

                ArrayList<Integer> helper = writeData(cmd, index, data);

                helperL1.put(blockNr, helper);
                helperL2.put(blockNr, helper);

                getL1().put(l1CacheSetIndex, helperL1);
                getL2().put(l2CacheSetIndex, helperL2);
            } else if (helperL1 != null && helperL1.size() < k && helperL2.size() < k) {
                ArrayList<Integer> helper = writeData(cmd, index, data);

                helperL1.put(blockNr, helper);
                helperL2.put(blockNr, helper);

                getL1().put(l1CacheSetIndex, helperL1);
                getL2().put(l2CacheSetIndex, helperL2);
            } else {
                ArrayList<Integer> helper = writeData(cmd, index, data);

                if (helperL1 != null) {
                    actions.getActions().add(replaceBlockInCache(helperL1, frequencyListL1, "L1"));
                    helperL1.put(blockNr, helper);
                }

                helperL2.put(blockNr, helper);

                getL1().put(l1CacheSetIndex, helperL1);
                getL2().put(l2CacheSetIndex, helperL2);
            }
        }
        return actions;
    }
}