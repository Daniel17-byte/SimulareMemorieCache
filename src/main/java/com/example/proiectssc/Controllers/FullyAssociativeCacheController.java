package com.example.proiectssc.Controllers;

import com.example.proiectssc.Caches.FullyAssociativeCache;
import com.example.proiectssc.Responses.Actions;
import com.example.proiectssc.Responses.Address;
import com.example.proiectssc.Others.CMD;
import com.example.proiectssc.Responses.CacheTables;
import com.example.proiectssc.Responses.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/cache/associative/")
public class FullyAssociativeCacheController {
    FullyAssociativeCache fullyAssociativeCache = new FullyAssociativeCache(4,16, new HashMap<>(), new HashMap<>());

    @GetMapping(value = "/run-cmd")
    public ResponseEntity<Actions> runCmd(@RequestParam String cmd, @RequestParam int address, @RequestParam int data){
        Actions actions = fullyAssociativeCache.runCmd(cmd,address,data);
        return new ResponseEntity<>(actions, HttpStatus.OK);
    }

    @GetMapping(value = "/view-address")
    public ResponseEntity<Address> getAddress(@RequestParam int address){
        Address stringAddress = fullyAssociativeCache.getAddress(address, 0);
        return new ResponseEntity<>(stringAddress, HttpStatus.OK);
    }

    @GetMapping(value = "/view-cache")
    public ResponseEntity<CacheTables> viewCache(){
        CacheTables cacheTables = fullyAssociativeCache.getViewCacheResponse();
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
                test.setActions(fullyAssociativeCache.runCmd(CMD.READ.toString(),randomAddress,randomData));
            } else {
                test.setActions(fullyAssociativeCache.runCmd(CMD.WRITE.toString(),randomAddress,randomData));
            }
            tests.add(test);
        }

        return new ResponseEntity<>(tests, HttpStatus.OK);
    }
}
