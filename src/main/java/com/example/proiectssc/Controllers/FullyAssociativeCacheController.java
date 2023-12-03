package com.example.proiectssc.Controllers;

import com.example.proiectssc.Caches.FullyAssociativeCache;
import com.example.proiectssc.Others.CMD;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/cache/associative/")
public class FullyAssociativeCacheController {
    FullyAssociativeCache fullyAssociativeCache = new FullyAssociativeCache(4,16, new HashMap<>(), new HashMap<>());

    @GetMapping(value = "/run-cmd")
    public void runCmd(@RequestParam String cmd, @RequestParam int address, @RequestParam int data){
        fullyAssociativeCache.runCmd(cmd,address,data);
    }

    @GetMapping(value = "/view-address")
    public void getAddress(@RequestParam int address){
        fullyAssociativeCache.getAddress(address, 1);
    }

    @GetMapping(value = "/view-cache")
    public void viewCache(){
        fullyAssociativeCache.printMapStrings();
    }

    @GetMapping(value = "/simulation")
    public void runSimulation(@RequestParam int nrTests){
        Random random = new Random();

        for (int i = 0; i < nrTests; i++) {
            boolean randomBoolean = random.nextBoolean();
            int randomAddress = random.nextInt(0, Integer.MAX_VALUE);
            int randomData = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (randomBoolean){
                fullyAssociativeCache.runCmd(CMD.READ.toString(),randomAddress,randomData);
            } else {
                fullyAssociativeCache.runCmd(CMD.WRITE.toString(),randomAddress,randomData);
            }
        }
    }
}
