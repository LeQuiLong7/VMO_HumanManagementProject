import { Outlet, useLocation } from "react-router-dom";
import Topbar from "./Topbar";
import LeftSideBar from "./LeftSideBar";

export default function Sidebar({ employee, setEmployee }) {
    
    return (
        <>
            <Topbar employee={employee}/>
            <div className="main">
                <LeftSideBar/>
                <div className="content">
                    <Outlet></Outlet>
                </div>
            </div>

        </>
    )
}