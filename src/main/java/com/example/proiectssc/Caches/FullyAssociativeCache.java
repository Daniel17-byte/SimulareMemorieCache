package com.example.proiectssc.Caches;

import com.example.proiectssc.Responses.Actions;
import com.example.proiectssc.Others.CMD;

import java.util.*;

public class FullyAssociativeCache extends Cache {
    ArrayList<Integer> freq1 = new ArrayList<>();
    ArrayList<Integer> freq2 = new ArrayList<>();

    public FullyAssociativeCache(int blockSize, int cacheLines, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L1, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L2) {
        super(blockSize, cacheLines, L1, L2);
    }

    public Actions runCmd(String cmd, int address, int data) {
        Actions actions = new Actions();
        int blockNr = address / getBlockSize();
        int index = address % getBlockSize();

        freq1.add(blockNr);
        freq2.add(blockNr);

        int l1CacheLineIndex = blockNr % (getCacheLines() / 2);
        int l2CacheLineIndex = blockNr % getCacheLines();

        HashMap<Integer, ArrayList<Integer>> helper1 = getL1().get(l1CacheLineIndex);
        HashMap<Integer, ArrayList<Integer>> helper2 = getL2().get(l2CacheLineIndex);

        if (helper1 == null) {
            helper1 = new HashMap<>();
            getL1().put(l1CacheLineIndex, helper1);
        }

        if (helper2 == null) {
            helper2 = new HashMap<>();
            getL2().put(l2CacheLineIndex, helper2);
        }

        if (helper1.containsKey(blockNr)) {
            actions.getActions().add("Hit in L1");
            if (cmd.equals(CMD.READ.toString())) {
                if (helper1.get(blockNr).get(index) == Integer.MAX_VALUE) {
                    actions.getActions().add("empty");
                } else {
                    System.out.println(helper1.get(blockNr).get(index));
                }
            } else if (cmd.equals(CMD.WRITE.toString())) {
                helper1.get(blockNr).set(index, data);
                helper2.get(blockNr).set(index, data);
            }
        } else if (helper2.containsKey(blockNr)) {
            actions.getActions().add("Hit in L2");
            if (cmd.equals(CMD.READ.toString())) {
                if (helper2.get(blockNr).get(index) == Integer.MAX_VALUE) {
                    actions.getActions().add("empty");
                } else {
                    System.out.println(helper2.get(blockNr).get(index));
                }
            } else if (cmd.equals(CMD.WRITE.toString())) {
                helper2.get(blockNr).set(index, data);
            }
            if (helper1.size() < getCacheLines() / 2) {
                helper1.put(blockNr, helper2.get(blockNr));
            }
            actions.getActions().add(replaceBlockInCache(helper1, freq1, "L1"));
            helper1.put(blockNr, helper2.get(blockNr));
        } else {
            actions.getActions().add("Address not found");
            if (helper1.size() == getCacheLines() / 2 && helper2.size() == getCacheLines()) {
                actions.getActions().add(replaceBlockInCache(helper1, freq1, "L1"));
                actions.getActions().add(replaceBlockInCache(helper2, freq2, "L2"));
                ArrayList<Integer> helper = new ArrayList<>();
                for (int j = 0; j < getBlockSize(); j++) {
                    helper.add(Integer.MAX_VALUE);
                }
                if (cmd.equals(CMD.WRITE.toString())) {
                    helper.set(index, data);
                }
                helper1.put(blockNr, helper);
                helper2.put(blockNr, helper);
            } else if (helper1.size() < getCacheLines() / 2 && helper2.size() < getCacheLines()) {
                ArrayList<Integer> helper = new ArrayList<>();
                for (int j = 0; j < getBlockSize(); j++) {
                    helper.add(Integer.MAX_VALUE);
                }
                if (cmd.equals(CMD.WRITE.toString())) {
                    helper.set(index, data);
                }
                helper1.put(blockNr, helper);
                helper2.put(blockNr, helper);
            } else {
                actions.getActions().add(replaceBlockInCache(helper1,freq1, "L1"));
                ArrayList<Integer> helper = new ArrayList < > ();
                for (int j = 0; j < getBlockSize(); j++) {
                    helper.add(Integer.MAX_VALUE);
                }
                if (cmd.equals(CMD.WRITE.toString())) {
                    helper.set(index, data);
                }
                helper1.put(blockNr, helper);
                helper2.put(blockNr, helper);
            }
        }
        return actions;
    }
}