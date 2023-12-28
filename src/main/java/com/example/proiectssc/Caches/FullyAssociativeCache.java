package com.example.proiectssc.Caches;

import com.example.proiectssc.Others.MemoryRepository;
import com.example.proiectssc.Responses.Actions;
import com.example.proiectssc.Others.CMD;

import java.util.*;

public class FullyAssociativeCache extends Cache {
    private final ArrayList<Integer> frequencyListL1 = new ArrayList<>();
    private final ArrayList<Integer> frequencyListL2 = new ArrayList<>();

    public FullyAssociativeCache(int blockSize, int cacheLines, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L1, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L2, MemoryRepository memoryRepository) {
        super(blockSize, cacheLines, L1, L2, memoryRepository);
    }

    public Actions runCmd(String cmd, int address, int data) {
        Actions actions = new Actions();

        actions.getActions().add("Doing operation : " + cmd +" data : " + data + " from/to " + address);

        int blockNr = address / getBlockSize();
        int index = address % getBlockSize();

        frequencyListL1.add(blockNr);
        frequencyListL2.add(blockNr);

        int l1CacheLineIndex = blockNr % (getCacheLines() / 2);
        int l2CacheLineIndex = blockNr % getCacheLines();

        HashMap<Integer, ArrayList<Integer>> helperL1 = getL1().get(l1CacheLineIndex);
        HashMap<Integer, ArrayList<Integer>> helperL2 = getL2().get(l2CacheLineIndex);

        if (helperL1 == null) {
            helperL1 = new HashMap<>();
            getL1().put(l1CacheLineIndex, helperL1);
        }

        if (helperL2 == null) {
            helperL2 = new HashMap<>();
            getL2().put(l2CacheLineIndex, helperL2);
        }

        if (helperL1.containsKey(blockNr)) {
            actions.getActions().add("Hit in L1");

            if (cmd.equals(CMD.READ.toString())) {
                readData(helperL1, actions, blockNr, index);
            } else if (cmd.equals(CMD.WRITE.toString())) {
                addInMemory(address, data);
                helperL1.get(blockNr).set(index, data);
                helperL2.get(blockNr).set(index, data);
            }
        } else if (helperL2.containsKey(blockNr)) {
            actions.getActions().add("Hit in L2");

            if (cmd.equals(CMD.READ.toString())) {
                readData(helperL2, actions, blockNr, index);
            } else if (cmd.equals(CMD.WRITE.toString())) {
                addInMemory(address, data);
                helperL2.get(blockNr).set(index, data);
            }
            if (helperL1.size() < getCacheLines() / 2) {
                helperL1.put(blockNr, helperL2.get(blockNr));
            }
            actions.getActions().add(replaceBlockInCache(helperL1, frequencyListL1, "L1"));
            helperL1.put(blockNr, helperL2.get(blockNr));
        } else {
            actions.getActions().add("Address not found in Cache");

            if (helperL1.size() == getCacheLines() / 2 && helperL2.size() == getCacheLines()) {
                actions.getActions().add(replaceBlockInCache(helperL1, frequencyListL1, "L1"));
                actions.getActions().add(replaceBlockInCache(helperL2, frequencyListL2, "L2"));

                ArrayList<Integer> helper = writeData(address, cmd, index, data);

                helperL1.put(blockNr, helper);
                helperL2.put(blockNr, helper);
            } else if (helperL1.size() < getCacheLines() / 2 && helperL2.size() < getCacheLines()) {
                ArrayList<Integer> helper = writeData(address, cmd, index, data);

                helperL1.put(blockNr, helper);
                helperL2.put(blockNr, helper);
            } else {
                actions.getActions().add(replaceBlockInCache(helperL1, frequencyListL1, "L1"));
                ArrayList<Integer> helper = writeData(address, cmd, index, data);

                helperL1.put(blockNr, helper);
                helperL2.put(blockNr, helper);
            }
        }
        return actions;
    }
}