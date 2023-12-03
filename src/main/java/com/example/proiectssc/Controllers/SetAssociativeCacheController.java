package com.example.proiectssc.Controllers;

import com.example.proiectssc.Caches.SetAssociativeCache;
import com.example.proiectssc.Others.CMD;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/cache/set-associative/")
public class SetAssociativeCacheController {
    SetAssociativeCache setAssociativeCache = new SetAssociativeCache(4,16, new HashMap<>(), new HashMap<>(), 4);

    @GetMapping(value = "/run-cmd")
    public void runCmd(@RequestParam String cmd, @RequestParam int address, @RequestParam int data){
        setAssociativeCache.runCmd(cmd,address,data);
    }

    @GetMapping(value = "/view-address")
    public void getAddress(@RequestParam int address){
        setAssociativeCache.getAddress(address, setAssociativeCache.getCacheLines() / setAssociativeCache.getK());
    }

    @GetMapping(value = "/view-cache")
    public void viewCache(){
        setAssociativeCache.printMapStrings();
    }

    @GetMapping(value = "/simulation")
    public void runSimulation(@RequestParam int nrTests){
        Random random = new Random();

        for (int i = 0; i < nrTests; i++) {
            boolean randomBoolean = random.nextBoolean();
            int randomAddress = random.nextInt(0, Integer.MAX_VALUE);
            int randomData = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (randomBoolean){
                setAssociativeCache.runCmd(CMD.READ.toString(),randomAddress,randomData);
            } else {
                setAssociativeCache.runCmd(CMD.WRITE.toString(),randomAddress,randomData);
            }
        }
    }
}
