package com.example.proiectssc.Caches;

import com.example.proiectssc.Responses.Actions;
import com.example.proiectssc.Others.CMD;

import java.util.*;
import java.util.Map.Entry;

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

        HashMap<Integer, ArrayList<Integer>> helper1 = getL1().get(l1CacheLineIndex);
        HashMap<Integer, ArrayList<Integer>> helper2 = getL2().get(l2CacheLineIndex);

        if (helper1 != null && helper1.containsKey(blockNr)) {
            actions.getActions().add("Hit in L1");
            if (cmd.equals(CMD.READ.toString())) {
                if (helper1.get(blockNr).get(index) == Integer.MAX_VALUE) {
                    actions.getActions().add("empty");
                } else {
                    actions.getActions().add(helper1.get(blockNr).get(index).toString());
                }
            } else if (cmd.equals(CMD.WRITE.toString())) {
                helper1.get(blockNr).set(index, data);
                helper2.get(blockNr).set(index, data);
                getL1().put(l1CacheLineIndex, helper1);
                getL2().put(l2CacheLineIndex, helper2);
            }
        } else if (helper2 != null && helper2.containsKey(blockNr)) {
            actions.getActions().add("Hit in L2");
            if (cmd.equals(CMD.READ.toString())) {
                if (helper2.get(blockNr).get(index) == Integer.MAX_VALUE) {
                    actions.getActions().add("empty");
                } else {
                    actions.getActions().add(helper2.get(blockNr).get(index).toString());
                }
            } else if (cmd.equals(CMD.WRITE.toString())) {
                helper2.get(blockNr).set(index, data);
            }
            HashMap<Integer, ArrayList<Integer>> hp = new HashMap<>();
            if(helper1!=null){
                for (Entry < Integer, ArrayList < Integer >> mapElement: helper1.entrySet()) {
                    actions.getActions().add("Block " + getBinary(Integer.parseInt(mapElement.getKey() + "")) + " gets replaced in L1 cache");
                }
            }
            hp.put(blockNr, helper2.get(blockNr));
            getL1().put(l1CacheLineIndex, hp);
            getL2().put(l2CacheLineIndex, helper2);
        } else {
            actions.getActions().add("Address not found");
            ArrayList<Integer> arr = new ArrayList<>();
            for (int j = 0; j < getBlockSize(); j++) {
                arr.add(Integer.MAX_VALUE);
            }
            if (cmd.equals(CMD.WRITE.toString())) {
                arr.set(index, data);
            }
            if (helper1 == null && helper2 == null) {
                helper1 = new HashMap<>();
                helper1.put(blockNr, arr);
                helper2 = new HashMap<>();
                helper2.put(blockNr, arr);
                getL1().put(l1CacheLineIndex, helper1);
                getL2().put(l2CacheLineIndex, helper2);
            } else if (helper1 != null && helper2 == null) {
                helper2 = new HashMap<>();
                helper2.put(blockNr, arr);
                getL2().put(l2CacheLineIndex, helper2);
                for (Entry<Integer, ArrayList<Integer>> mapElement: helper1.entrySet()) {
                    actions.getActions().add("Block " + getBinary(Integer.parseInt(mapElement.getKey() + "")) + " gets replaced in L1 cache");
                }
                getL1().put(l1CacheLineIndex, helper1);
            } else {
                HashMap<Integer, ArrayList<Integer>> hp = new HashMap<>();
                hp.put(blockNr, arr);
                if(helper1 != null) {
                    for (Entry<Integer, ArrayList<Integer>> mapElement : helper1.entrySet()) {
                        actions.getActions().add("Block " + getBinary(Integer.parseInt(mapElement.getKey() + "")) + " gets replaced in L1 cache");
                    }
                }
                for (Entry<Integer, ArrayList<Integer>> mapElement: helper2.entrySet()) {
                    actions.getActions().add("Block " + getBinary(Integer.parseInt(mapElement.getKey() + "")) + " gets replaced in L2 cache");
                }
                getL1().put(l1CacheLineIndex, hp);
                getL2().put(l2CacheLineIndex, hp);
            }
        }
        return actions;
    }
}