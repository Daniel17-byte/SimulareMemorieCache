import React, {useEffect, useState} from "react";
import {useGlobalContext} from "./GlobalContext";
import Endpoints from "./Endpoints";

const CacheTable = () => {
    const {
        viewCacheCommand,
        viewCacheApi,
    } = Endpoints();

    const {cacheType, updateCache, parameters, updateParams, response, updateResponse, rs} = useGlobalContext();


    const [l1CacheData, setL1CacheData] = useState([]);
    const [l2CacheData, setL2CacheData] = useState([]);

    const refreshCache = async () => {
        viewCacheApi(cacheType, viewCacheCommand);
    };

    useEffect(() => {
        if(response && response.l1Cache && response.l2Cache){
            setL1CacheData(response.l1Cache);
            setL2CacheData(response.l2Cache);
        }
    }, [response])

    useEffect(() => {
        refreshCache();
    }, [cacheType, rs]);

    return (
        <>
            <h3>Cache Type: {cacheType && cacheType.toUpperCase()}</h3>
            <div className="cache-table">
                <div>
                    <h2>L1 Cache</h2>
                    <table>
                        <thead>
                        <tr>
                            <th>Cache Line</th>
                            <th>Block Number</th>
                            <th>Block Address</th>
                            <th>Block Content</th>
                        </tr>
                        </thead>
                        <tbody>
                        {l1CacheData.map((item, index) => (
                            <tr key={index}>
                                <td>{item.cacheLine}</td>
                                <td>{item.blockNumber}</td>
                                <td>{item.blockAddress}</td>
                                <td>{item.blockContent.join(", ")}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
                <div>
                    <h2>L2 Cache</h2>
                    <table>
                        <thead>
                        <tr>
                            <th>Cache Line</th>
                            <th>Block Number</th>
                            <th>Block Address</th>
                            <th>Block Content</th>
                        </tr>
                        </thead>
                        <tbody>
                        {l2CacheData.map((item, index) => (
                            <tr key={index}>
                                <td>{item.cacheLine}</td>
                                <td>{item.blockNumber}</td>
                                <td>{item.blockAddress}</td>
                                <td>{item.blockContent.join(", ")}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </>
    );
};

export default CacheTable;