package com.example.proiectssc.Caches;

import com.example.proiectssc.Responses.Actions;
import com.example.proiectssc.Others.CMD;
import lombok.Getter;

import java.util.*;

public class SetAssociativeCache extends Cache {
    private final ArrayList<Integer> freq1 = new ArrayList<>();
    private final ArrayList<Integer> freq2 = new ArrayList<>();
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

        freq1.add(blockNr);
        freq2.add(blockNr);

        int l1CacheSetIndex = blockNr % l1CacheSets;
        int l2CacheSetIndex = blockNr % l2CacheSets;

        HashMap<Integer, ArrayList<Integer>> helper1 = getL1().get(l1CacheSetIndex);
        HashMap<Integer, ArrayList<Integer>> helper2 = getL2().get(l2CacheSetIndex);

        if (helper1 != null && helper1.containsKey(blockNr)) {
            actions.getActions().add("Hit in L1");
            if(cmd.equals(CMD.READ.toString())) {
                if (helper1.get(blockNr).get(index) == Integer.MAX_VALUE) {
                    actions.getActions().add("empty");
                } else {
                    actions.getActions().add(helper1.get(blockNr).get(index).toString());
                }
            } else if(cmd.equals(CMD.WRITE.toString())){
                helper1.get(blockNr).set(index, data);
                helper2.get(blockNr).set(index, data);
                getL1().put(l1CacheSetIndex, helper1);
                getL2().put(l2CacheSetIndex, helper2);
            }
        } else if (helper2 != null && helper2.containsKey(blockNr)) {
            actions.getActions().add("Hit in L2");
            if (cmd.equals(CMD.READ.toString())) {
                if (helper2.get(blockNr).get(index) == Integer.MAX_VALUE) {
                    actions.getActions().add("empty");
                } else {
                    actions.getActions().add(getL2().get(blockNr).get(index).toString());
                }
            } else if (cmd.equals(CMD.WRITE.toString())) {
                helper2.get(blockNr).set(index, data);
                getL2().put(l2CacheSetIndex, helper2);
            }

            if (helper1 != null && helper1.size() < k) {
                ArrayList<Integer> h = new ArrayList<>();
                for (int j = 0; j < getBlockSize(); j++) {
                    h.add(Integer.MAX_VALUE);
                }
                if (cmd.equals(CMD.WRITE.toString())) {
                    h.set(index, data);
                }
                helper1.put(blockNr, h);
                getL1().put(l1CacheSetIndex, helper1);
            }

            if (helper1 != null){
                actions.getActions().add(replaceBlockInCache(helper1, freq1, "L1"));
                helper1.put(blockNr, helper2.get(blockNr));
            }

            getL1().put(l1CacheSetIndex, helper1);
        } else {
            actions.getActions().add("Address not found");
            if (helper1 == null && helper2 == null) {
                ArrayList<Integer> arr = new ArrayList<>();
                for (int j = 0; j < getBlockSize(); j++) {
                    arr.add(Integer.MAX_VALUE);
                }
                if (cmd.equals(CMD.WRITE.toString())) {
                    arr.set(index, data);
                }
                helper1 = new HashMap < > ();
                helper1.put(blockNr, arr);
                helper2 = new HashMap < > ();
                helper2.put(blockNr, arr);
                getL1().put(l1CacheSetIndex, helper1);
                getL2().put(l2CacheSetIndex, helper2);

            } else if (helper1 != null && helper2 == null) {
                ArrayList<Integer> arr = new ArrayList<>();
                for (int j = 0; j < getBlockSize(); j++) {
                    arr.add(Integer.MAX_VALUE);
                }
                if (cmd.equals(CMD.WRITE.toString())) {
                    arr.set(index, data);
                }
                helper2 = new HashMap < > ();
                helper2.put(blockNr, arr);
                getL2().put(l2CacheSetIndex, helper2);
                actions.getActions().add(replaceBlockInCache(helper1, freq1, "L1"));

                helper1.put(blockNr, arr);
                getL1().put(l1CacheSetIndex, helper1);

            } else if (helper1 != null && helper1.size() == k && helper2.size() == k) {
                actions.getActions().add(replaceBlockInCache(helper1, freq1, "L1"));
                actions.getActions().add(replaceBlockInCache(helper2, freq2, "L2"));
                ArrayList<Integer> ar = new ArrayList<>();
                for (int j = 0; j < getBlockSize(); j++) {
                    ar.add(Integer.MAX_VALUE);
                }
                if (cmd.equals(CMD.WRITE.toString())) {
                    ar.set(index, data);
                }
                helper1.put(blockNr, ar);
                helper2.put(blockNr, ar);
                getL1().put(l1CacheSetIndex, helper1);
                getL2().put(l2CacheSetIndex, helper2);
            } else if (helper1 != null && helper1.size() < k && helper2.size() < k) {
                ArrayList<Integer> ar = new ArrayList<>();
                for (int j = 0; j < getBlockSize(); j++) {
                    ar.add(Integer.MAX_VALUE);
                }
                if (cmd.equals(CMD.WRITE.toString())) {
                    ar.set(index, data);
                }
                helper1.put(blockNr, ar);
                helper2.put(blockNr, ar);
                getL1().put(l1CacheSetIndex, helper1);
                getL2().put(l2CacheSetIndex, helper2);
            } else {
                ArrayList<Integer> ar = new ArrayList<>();
                for (int j = 0; j < getBlockSize(); j++) {
                    ar.add(Integer.MAX_VALUE);
                }
                if (cmd.equals(CMD.WRITE.toString())) {
                    ar.set(index, data);
                }
                if (helper1 != null) {
                    actions.getActions().add(replaceBlockInCache(helper1, freq1, "L1"));
                    helper1.put(blockNr, ar);
                }
                helper2.put(blockNr, ar);
                getL1().put(l1CacheSetIndex, helper1);
                getL2().put(l2CacheSetIndex, helper2);
            }
        }
        return actions;
    }
}