import {useGlobalContext} from "./GlobalContext";

const Endpoints = () => {
    const {parameters, updateResponse, actions, updateActions, updateRS, updateAddress, memory, updateMemory} = useGlobalContext();

    const url = "http://localhost:8080/api/v1/cache/";
    const directCacheURL = "/direct/";
    const associativeCacheURL = "/associative/";
    const setAssociativeURL = "/set-associative/";

    const viewAddressCommand = "/view-address";
    const viewCacheCommand = "/view-cache";
    const runCommand = "/run-cmd";
    const runSimulation = "/simulation";
    const seeMemory = "/memory";

    const viewCacheApi = async (cacheType, cmd) => {
        fetch(`${url}${cacheType}${cmd}?${parameters.join("&")}`)
            .then((response) => response.json())
            .then((data) => {
                updateResponse(data);
            })
            .catch((err) => {
                console.log(err.message);
            });
    }

    const runSimulationApi = async (cacheType, cmd) => {
        const timeoutPromise = new Promise((_, reject) => {
            setTimeout(() => {
                reject(new Error('Timeout exceeded'));
            }, 5000);
        });

        try {
            const response = await Promise.race([
                fetch(`${url}${cacheType}${cmd}?${parameters.join("&")}`),
                timeoutPromise,
            ]);

            const data = await response.json();
            updateActions(data);
        } catch (err) {
            console.log(err.message);
        }

        updateRS(Math.random())
    };

    const runCommandApi = async (cacheType, cmd) => {

        const timeoutPromise = new Promise((_, reject) => {
            setTimeout(() => {
                reject(new Error('Timeout exceeded'));
            }, 5000);
        });

        try {
            const response = await Promise.race([
                fetch(`${url}${cacheType}${cmd}?${parameters.join("&")}`),
                timeoutPromise,
            ]);

            const data = await response.json();

            const json = [];
            const auxJson = {};

            auxJson.nrTest = 0;
            auxJson.actions = { "actions": data.actions }; // Wrap data.actions in an object
            json.unshift(auxJson);

            updateActions(json);
        } catch (err) {
            console.log(err.message);
        }

        updateRS(Math.random())
    };

    const viewAddressApi = async (cacheType, cmd) => {
        fetch(`${url}${cacheType}${cmd}?${parameters.join("&")}`)
            .then((response) => response.json())
            .then((data) => {
                updateAddress(data);
            })
            .catch((err) => {
                console.log(err.message);
            });
    };

    const getMemoryApi = async (cacheType, cmd) => {
        fetch(`${url}${cacheType}${cmd}`)
            .then((response) => response.json())
            .then((data) => {
                updateMemory(data);
            })
            .catch((err) => {
                console.log(err.message);
            });
    };


    return {
        directCacheURL,
        associativeCacheURL,
        setAssociativeURL,
        viewAddressCommand,
        viewCacheCommand,
        runCommand,
        runSimulation,
        seeMemory,
        runSimulationApi,
        viewCacheApi,
        viewAddressApi,
        runCommandApi,
        getMemoryApi
    };
}

export default Endpoints;
