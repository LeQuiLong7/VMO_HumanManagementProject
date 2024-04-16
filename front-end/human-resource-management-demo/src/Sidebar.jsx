import { Outlet, useLocation } from "react-router-dom";
import Topbar from "./Topbar";

export default function Sidebar() {
    return (
        <>
            <Topbar/>
            <div className="main">
                <div className="sidebar">
                    <div className="left-sidebar">
                        <div className="logo">HR Management</div>
                        <nav className="nav-menu">
                            <ul>
                                <li><a href="/">Dashboard</a></li>
                                <li><a href="/employees">Employees</a></li>
                                <li><a href="/departments">Departments</a></li>
                                <li><a href="/projects">Projects</a></li>
                                <li><a href="/reports">Reports</a></li>
                            </ul>
                        </nav>
                    </div>
                </div>
                <div className="content">
                    <Outlet></Outlet>
                </div>
            </div>

        </>
    )
}