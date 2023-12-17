import { createContext, useContext, useState } from "react";

const GlobalContext = createContext();

const GlobalProvider = ({ children }) => {
    const [cacheType, setCacheType] = useState("direct");
    const [rs, setRS] = useState(0);
    const [parameters, setParameters] = useState([`cmd=READ`]);
    const [response, setResponse] = useState(null);
    const [actions, setActions] = useState(null);
    const [address, setAddress] = useState(null);

    const updateAddress = (newAddress) => {
        setAddress(newAddress);
    }

    const updateParams = (params) => {
        setParameters(params)
    }

    const addParams = (param) => {
        if (parameters.some(p => p.toString().startsWith(param.toString().split("=")[0] + "="))) {
            setParameters(prevParams =>
                prevParams.map(p =>
                    p.toString().startsWith(param.toString().split("=")[0] + "=") ? param : p
                )
            );
        } else {
            setParameters(prevParams => [...prevParams, param]);
        }
    };

    const updateCache = (newData) => {
        setCacheType(newData);
    };

    const updateRS = (r) => {
        setRS(r);
    };

    const updateResponse = (newResponse) => {
        setResponse(newResponse);
    };

    const updateActions = (newActions) => {
        setActions(newActions);
    }

    return (
        <GlobalContext.Provider
            value={{ cacheType,
                updateCache, rs, updateRS,
                parameters, updateParams, response, updateResponse, actions, updateActions, address, updateAddress, addParams }}
        >
            {children}
        </GlobalContext.Provider>
    );
};

const useGlobalContext = () => {
    return useContext(GlobalContext);
};

export { GlobalProvider, useGlobalContext };