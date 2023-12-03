package com.example.proiectssc.Caches;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Cache {
    private final int blockSize;
    private final int cacheLines;
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L1;
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L2;

    public Cache(int blockSize, int cacheLines, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L1, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L2) {
        this.blockSize = blockSize;
        this.cacheLines = cacheLines;
        this.L1 = L1;
        this.L2 = L2;
    }

    public int getN(int n) {
        return Integer.numberOfTrailingZeros(Integer.highestOneBit(n));
    }

    public String getBinary(int n) {
        int b = getN(blockSize);
        String binaryRepresentation = Integer.toBinaryString(n);
        int leadingZerosToAdd = 16 - b - binaryRepresentation.length();
        if (leadingZerosToAdd > 0) {
            return "0".repeat(leadingZerosToAdd) + binaryRepresentation;
        } else {
            return binaryRepresentation;
        }
    }

    public void removeAll(ArrayList<Integer> frequencyList, int item) {
        frequencyList.removeIf(integer -> integer == item);
    }

    public void replaceBlockInCache(HashMap<Integer, ArrayList<Integer>> cache, ArrayList<Integer> frequencyList) {
        int maxDistance = Integer.MIN_VALUE;

        for (Map.Entry<Integer, ArrayList<Integer>> mapElement : cache.entrySet()) {
            int keyDistance = 0;
            for (int h = frequencyList.size() - 1; h >= 0; h--) {
                if (frequencyList.get(h).equals(mapElement.getKey())) {
                    break;
                }
                keyDistance++;
            }
            if (maxDistance < keyDistance) {
                maxDistance = keyDistance;
            }
        }

        int u = frequencyList.size() - maxDistance - 1;
        int val = frequencyList.get(u);

        System.out.println("Block " + getBinary(val) + " gets replaced in cache");
        removeAll(frequencyList, val);
        cache.remove(val);
    }

    public void printMapStrings(){
        System.out.println("L1 Cache : ");
        printMap(getL1());
        System.out.println("---------------------------------");
        System.out.println("L2 Cache : ");
        printMap(getL2());
        System.out.println("---------------------------------");
    }

    public void printMap(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> map) {
        System.out.println("                     Block number    Block Address         Block Content");
        for (Map.Entry<Integer, HashMap<Integer, ArrayList<Integer>>> mapElement: map.entrySet()) {
            HashMap<Integer, ArrayList<Integer>> h = mapElement.getValue();
            for (Map.Entry<Integer, ArrayList<Integer>> mapElement2: h.entrySet()) {
                System.out.print(mapElement.getKey() + "  \t" + "\t" + "\t" + mapElement2.getKey() + "  \t    " + getBinary(mapElement2.getKey()) + "   \t ");
                for (int i = 0; i < mapElement2.getValue().size(); i++) {
                    if (mapElement2.getValue().get(i) == Integer.MAX_VALUE) {
                        System.out.print("Empty" + "  ");
                    } else {
                        System.out.print(mapElement2.getValue().get(i) + "  ");
                    }
                }
                System.out.println();
            }
        }
    }

    public void getAddress(int address, int k) {
        String str = getBinary(address);

        int b = getN(getBlockSize());
        int setNr1 = getN(k / 2);
        int setNr2 = getN(k);
        int beginIndex = str.length() - b;

        String offset = str.substring(beginIndex);
        String line1 = str.substring(beginIndex - setNr1, beginIndex);
        String tag1 = str.substring(0, beginIndex - setNr1);
        String line2 = str.substring(beginIndex - setNr2, beginIndex);
        String tag2 = str.substring(0, beginIndex - setNr2);

        System.out.println("Physical Address: " + str + "; Tag: " + tag1 + ", Set-offset: " + line1 + ", Word-offset: " +
                offset + " for L1 cache; " + " Tag: " + tag2 + ", Set-offset: " + line2 + ", Word-offset: " +
                offset + " for L2 cache");
    }
}
