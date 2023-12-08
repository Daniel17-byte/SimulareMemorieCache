package com.example.proiectssc.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CacheTable {
    int cacheLine;
    int blockNumber;
    String blockAddress;
    ArrayList<Object> blockContent = new ArrayList<>();
}
