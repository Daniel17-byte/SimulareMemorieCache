package com.example.proiectssc.Caches;

import com.example.proiectssc.Responses.Address;
import com.example.proiectssc.Responses.CacheTable;
import com.example.proiectssc.Responses.CacheTables;
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

    public String replaceBlockInCache(HashMap<Integer, ArrayList<Integer>> cache, ArrayList<Integer> frequencyList, String cacheLevel) {
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

        removeAll(frequencyList, val);
        cache.remove(val);
        return new String("Block " + getBinary(val) + " gets replaced in cache " + cacheLevel);
    }

    public CacheTables getViewCacheResponse(){
        CacheTables cacheTables = new CacheTables();
        cacheTables.setL1Cache(getCache(L1));
        cacheTables.setL2Cache(getCache(L2));
        return cacheTables;
    }

    public ArrayList<CacheTable> getCache(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> map) {
        ArrayList<CacheTable> cacheEntries = new ArrayList<>();
        for (Map.Entry<Integer, HashMap<Integer, ArrayList<Integer>>> mapElement: map.entrySet()) {
            HashMap<Integer, ArrayList<Integer>> h = mapElement.getValue();
            CacheTable cacheTable = new CacheTable();
            for (Map.Entry<Integer, ArrayList<Integer>> mapElement2: h.entrySet()) {
                cacheTable.setCacheLine(mapElement.getKey());
                cacheTable.setBlockNumber(mapElement2.getKey());
                cacheTable.setBlockAddress(getBinary(mapElement2.getKey()));
                for (int i = 0; i < mapElement2.getValue().size(); i++) {
                    if (mapElement2.getValue().get(i) == Integer.MAX_VALUE) {
                        cacheTable.getBlockContent().add(new String("Empty"));
                    } else {
                        cacheTable.getBlockContent().add(mapElement2.getValue().get(i));
                    }
                }
            }
            cacheEntries.add(cacheTable);
        }
        return cacheEntries;
    }

    public Address getAddress(int address, int k) {
        String str = getBinary(address);

        int b = getN(getBlockSize());
        int setNr1 = 0;
        int setNr2 = 0;

        if (k != 0){
            setNr1 = getN(k / 2);
            setNr2 = getN(k);
        }

        int beginIndex = str.length() - b;

        String offset = str.substring(beginIndex);
        String line1 = str.substring(beginIndex - setNr1, beginIndex);
        String tag1 = str.substring(0, beginIndex - setNr1);
        String line2 = str.substring(beginIndex - setNr2, beginIndex);
        String tag2 = str.substring(0, beginIndex - setNr2);

        return new Address("Physical Address: " + str + "; Tag: " + tag1 + ", Set-offset: " + line1 + ", Word-offset: " +
                offset + " for L1 cache; " + " Tag: " + tag2 + ", Set-offset: " + line2 + ", Word-offset: " +
                offset + " for L2 cache");
    }
}
