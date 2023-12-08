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
public class CacheTables {
    ArrayList<CacheTable> l1Cache;
    ArrayList<CacheTable> l2Cache;
}
