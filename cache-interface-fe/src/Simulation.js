import React, { useState, useEffect } from "react";
import Endpoints from "./Endpoints";
import {useGlobalContext} from "./GlobalContext";

const Simulation = () => {
    const {
        runSimulation,
        runSimulationApi,
        runCommand,
        viewAddressApi,
        seeMemory,
        viewAddressCommand,
        runCommandApi,
        getMemoryApi
    } = Endpoints();

    const {cacheType, updateCache, parameters, response, updateResponse, rs, updateRS, actions, updateActions, address, addParams, memory} = useGlobalContext();

    useEffect(() => {

    },[actions]);

    const handleRunTests = async () => {
        try {
            await runSimulationApi(cacheType,runSimulation);
        } catch (error) {
            console.error('Eroare la preluarea datelor:', error);
        }
        await viewMemory();
    };

    const handleRunTest = async () => {
        try {
            await runCommandApi(cacheType, runCommand);
        } catch (error) {
            console.error('Eroare la preluarea datelor:', error);
        }
        await viewMemory();
    };

    const viewAddress = async () => {
        try {
            await viewAddressApi(cacheType,viewAddressCommand);
        } catch (error) {
            console.error('Eroare la view address:', error);
        }
    }

    const viewMemory = async () => {
        try {
            await getMemoryApi(cacheType, seeMemory);
        } catch (error) {
            console.error('Eroare la view memory:', error);
        }
    }

    useEffect(() => {
        viewMemory()
    }, [cacheType, rs]);

    return (
        <div>
            <div className="cont">
                <div className="col1">
                    <div className="form-container">
                        <form
                            onSubmit={(e) => {
                                e.preventDefault();
                                handleRunTests();
                            }}
                        >
                            <div className="form-row">
                                <label className="form-label">
                                    Cache Type :
                                    <select
                                        onChange={(e) => {
                                            e.preventDefault();
                                            updateCache(e.target.value);
                                        }}
                                    >
                                        <option value="direct">Direct Cache</option>
                                        <option value="associative">Associative Cache</option>
                                        <option value="set-associative">Set Associative Cache</option>
                                    </select>
                                </label>
                            </div>
                            <div className="form-row">
                                <label className="form-label">
                                    Tests number :
                                    <input
                                        type="number"
                                        min="1"
                                        onChange={(e) => {
                                            e.preventDefault();
                                            addParams([`nrTests=${e.target.value}`]);
                                        }}
                                    />
                                </label>
                                <button className="form-button" type="submit">
                                    Run Simulation
                                </button>
                            </div>
                        </form>
                    </div>
                    <div className="form-container">
                        <form
                            onSubmit={(e) => {
                                e.preventDefault();
                                handleRunTest();
                            }}
                        >
                            <div className="form-row">
                                <label className="form-label">
                                    Cmd Type:
                                    <select
                                        onChange={(e) => {
                                            e.preventDefault();
                                            addParams([`cmd=${e.target.value}`])
                                        }}
                                    >
                                        <option value="READ">Read</option>
                                        <option value="WRITE">Write</option>
                                    </select>
                                </label>
                            </div>
                            <div className="form-row">
                                <label className="form-label">
                                    Data :
                                    <input
                                        type="number"
                                        min="0"
                                        onChange={(e) => {
                                            e.preventDefault();
                                            addParams([`data=${e.target.value}`])
                                        }}
                                    />
                                </label>
                            </div>
                            <div className="form-row">
                                <label className="form-label">
                                    Address :
                                    <input
                                        type="number"
                                        min="0"
                                        onChange={(e) => {
                                            e.preventDefault();
                                            addParams([`address=${e.target.value}`])
                                        }}
                                    />
                                </label>
                                <button className="form-button" type="submit">
                                    Run Command
                                </button>
                            </div>
                        </form>
                    </div>
                    <div className="form-container">
                <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        viewAddress();
                    }}
                >
                    <div className="form-row">
                        <label className="form-label">
                            Address :
                            <input
                                type="number"
                                min="0"
                                onChange={(e) => {
                                    e.preventDefault();
                                    addParams([`address=${e.target.value}`])
                                }}
                            />
                        </label>
                        <button className="form-button" type="submit">
                            View Address
                        </button>
                    </div>
                </form>
            </div>
                </div>
                <div className="col2">
                    { address  &&
                        <div className="add">
                            {address.address}
                        </div>
                    }

                    {
                        memory &&
                        <div className="memory">
                            { memory.map((entry, index) => (
                                <> <span style={{fontWeight : "bold"}}>{entry.address}</span>-{entry.data}; </>
                            )) }
                        </div>
                    }
                </div>
            </div>


            {actions && actions.length > 0 && (
                <table className="simulation-table">
                    <thead>
                    <tr>
                        <th>Test</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {actions.map((result, index) => (
                        <tr key={index}>
                            <td>{`Test ${index}`}</td>
                            <td style={{borderLeft: "1px solid black"}}>
                                <ul className="actions-list" >
                                    {result.actions.actions.map((action, index) => (
                                        <li key={index}>{action}</li>
                                    ))}
                                </ul>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default Simulation;