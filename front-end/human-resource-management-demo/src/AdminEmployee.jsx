import { useState, useEffect } from "react";
import axios from "axios";
import GeneralInfo from "./GeneralInfo";

export default function AdminEmployee() {
    const [employeeList, setEmployeeList] = useState(null);

    useEffect(() => {
        getEmployeesList()
    }, [])

    async function getEmployeesList() {
        try {
            const response = await axios.get('http://localhost:8080/admin/employees', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            console.log(response.data);
            setEmployeeList(response.data)
        } catch (error) {
            console.log(error);
        }
    }
    return (
        <>
            <h1>Employees in manage</h1>
            <div className="employee-container">
            {employeeList && (employeeList.content.map(e => (
                <GeneralInfo employee={e}/>
            )))}
        </div>
        </>
    )

}