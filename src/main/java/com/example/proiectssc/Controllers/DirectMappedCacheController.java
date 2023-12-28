package com.example.proiectssc.Controllers;

import com.example.proiectssc.Caches.DirectMappedCache;
import com.example.proiectssc.Models.Memory;
import com.example.proiectssc.Others.MemoryRepository;
import com.example.proiectssc.Responses.Actions;
import com.example.proiectssc.Responses.Address;
import com.example.proiectssc.Others.CMD;
import com.example.proiectssc.Responses.CacheTables;
import com.example.proiectssc.Responses.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/cache/direct/")
public class DirectMappedCacheController {
    @Autowired
    final MemoryRepository memoryRepository;
    DirectMappedCache directMappedCache;

    public DirectMappedCacheController(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
        this.directMappedCache = new DirectMappedCache(4,16, new HashMap<>(), new HashMap<>(), memoryRepository);
    }

    @GetMapping(value = "/run-cmd")
    public ResponseEntity<Actions> runCmd(@RequestParam String cmd, @RequestParam int address, @RequestParam int data){
        Actions actions = directMappedCache.runCmd(cmd,address,data);
        return new ResponseEntity<>(actions, HttpStatus.OK);
    }

    @GetMapping(value = "/view-address")
    public ResponseEntity<Address> getAddress(@RequestParam int address){
        Address stringAddress = directMappedCache.getAddress(address, 0);
        return new ResponseEntity<>(stringAddress, HttpStatus.OK);
    }

    @GetMapping(value = "/view-cache")
    public ResponseEntity<CacheTables> viewCache(){
        CacheTables cacheTables = directMappedCache.getViewCacheResponse();
        return new ResponseEntity<>(cacheTables, HttpStatus.OK);
    }

    @GetMapping(value = "/simulation")
    public ResponseEntity<ArrayList<Test>> runSimulation(@RequestParam int nrTests){
        ArrayList<Test> tests = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < nrTests; i++) {
            Test test = new Test();
            test.setNrTest(i);
            boolean randomBoolean = random.nextBoolean();
            int randomAddress = random.nextInt(0, 1000);
            int randomData = random.nextInt(0, 1000);
            if (randomBoolean){
                test.setActions(directMappedCache.runCmd(CMD.READ.toString(),randomAddress,randomData));
            } else {
                test.setActions(directMappedCache.runCmd(CMD.WRITE.toString(),randomAddress,randomData));
            }
            tests.add(test);
        }

        return new ResponseEntity<>(tests, HttpStatus.OK);
    }

    @GetMapping(value = "/memory")
    public ResponseEntity<ArrayList<Memory>> getMemoryData(){
        ArrayList<Memory> memory = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            memory.add(memoryRepository.getData(i));
        }

        return new ResponseEntity<>(memory, HttpStatus.OK);
    }
}
