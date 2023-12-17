import './App.css';
import CacheTable from "./CacheTable";
import Simulation from "./Simulation";

function App() {
    return (
        <>
                <div className="container">
                    <div className="col">
                        <Simulation/>
                    </div>
                    <div className="col">
                        <CacheTable/>
                    </div>
                </div>
        </>
        );
}

export default App;
