package com.example.proiectssc.Caches;

import com.example.proiectssc.Models.Memory;
import com.example.proiectssc.Others.CMD;
import com.example.proiectssc.Others.MemoryRepository;
import com.example.proiectssc.Responses.Actions;
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
    private final MemoryRepository memoryRepository;

    public Cache(int blockSize, int cacheLines, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L1, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L2, MemoryRepository memoryRepository) {
        this.blockSize = blockSize;
        this.cacheLines = cacheLines;
        this.L1 = L1;
        this.L2 = L2;
        this.memoryRepository = memoryRepository;
    }

    public ArrayList<Integer> writeData(int address, String cmd, int index, int data){
        ArrayList<Integer> helper = new ArrayList<>();
        for (int j = 0; j < getBlockSize(); j++) {
            helper.add(Integer.MAX_VALUE);
        }
        if (cmd.equals(CMD.WRITE.toString())) {
            helper.set(index, data);
            addInMemory(address, data);
        }
        return helper;
    }

    public void readData(HashMap<Integer, ArrayList<Integer>> helper, Actions actions, int blockNr, int index){
        if (helper.get(blockNr).get(index) == Integer.MAX_VALUE) {
            actions.getActions().add("Data : " + getFromMemory(Integer.parseInt(getBinary(blockNr + index), 2)).getData());
        } else {
            actions.getActions().add("Data : " + helper.get(blockNr).get(index).toString());
        }
    }

    public String getBinary(int n) {
        int b = Integer.numberOfTrailingZeros(Integer.highestOneBit(getBlockSize()));
        String binaryRepresentation = Integer.toBinaryString(n);
        int leadingZerosToAdd = 16 - b - binaryRepresentation.length();
        if (leadingZerosToAdd > 0) {
            return "0".repeat(leadingZerosToAdd) + binaryRepresentation;
        } else {
            return binaryRepresentation;
        }
    }

    public String replaceBlockInCache(HashMap<Integer, ArrayList<Integer>> cache, ArrayList<Integer> frequencyList, String cacheLevel, String... flag) {
        if (flag.length > 0){
            return FIFO(cache, frequencyList, cacheLevel);
        }
        return LRU(cache,frequencyList, cacheLevel);
    }

    public String LRU(HashMap<Integer, ArrayList<Integer>> cache, ArrayList<Integer> frequencyList, String cacheLevel) {
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

        frequencyList.removeIf(integer -> integer == val);
        cache.remove(val);

        return new String("Block " + val + " gets replaced in cache " + cacheLevel);
    }

    public String FIFO(HashMap<Integer, ArrayList<Integer>> cache, ArrayList<Integer> frequencyList, String cacheLevel) {
        int val = frequencyList.get(0);

        frequencyList.removeIf(integer -> integer == val);
        cache.remove(val);

        frequencyList.add(val);

        return new String("Block " + val + " gets replaced in cache " + cacheLevel);
    }

    public String LFU(HashMap<Integer, Integer> cache, ArrayList<Integer> frequencyList, String cacheLevel) {
        int minFrequency = Integer.MAX_VALUE;
        int valToRemove = -1;

        for (Map.Entry<Integer, Integer> entry : cache.entrySet()) {
            int frequency = entry.getValue();
            if (frequency < minFrequency) {
                minFrequency = frequency;
                valToRemove = entry.getKey();
            }
        }

        frequencyList.remove((Integer) valToRemove);
        cache.remove(valToRemove);

        return new String("Block " + valToRemove + " gets replaced in cache " + cacheLevel);
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
                cacheTable.setBlockAddress(String.valueOf(mapElement2.getKey() * getBlockSize()));
                for (int i = 0; i < mapElement2.getValue().size(); i++) {
                    if (mapElement2.getValue().get(i) == Integer.MAX_VALUE) {
                        cacheTable.getBlockContent().add(getFromMemory(Integer.parseInt(getBinary(mapElement2.getKey() * getBlockSize() + i), 2)).getData());
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

        int b = Integer.numberOfTrailingZeros(Integer.highestOneBit(getBlockSize()));
        int setNr1 = 0;
        int setNr2 = 0;

        if (k != 0){
            setNr1 = Integer.numberOfTrailingZeros(Integer.highestOneBit(k / 2));
            setNr2 = Integer.numberOfTrailingZeros(Integer.highestOneBit(k));
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

    public void addInMemory(int address, int data){
        Memory memory = getFromMemory(address);

        if (memory == null){
            memory = new Memory(address, data);
            memoryRepository.save(memory);
        }else {
            memoryRepository.updateData(address, data);
        }
    }

    public Memory getFromMemory(int address){
        return memoryRepository.getData(address);
    }
}
