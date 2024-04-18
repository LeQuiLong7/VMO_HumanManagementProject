import { useEffect, useState } from "react"
import axios from "axios";
import image from "./image.png"

export default function Topbar({ employee, setEmployee }) {


    
    return <div className="top-bar">
        <h3 className="company-logo">COMPANY-LOGO</h3>
        <input type="text" placeholder="Search..." className="search-input" />
        {employee && (
            <div className="current-user">
                <img src={`${employee.avatarUrl || image}?${new Date().getTime()}`} alt="User Avatar"></img>
                <h5>{employee.firstName + " " + employee.lastName}</h5>
            </div>
        )
        }
    </div>
}