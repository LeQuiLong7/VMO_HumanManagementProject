import { useState, useEffect } from "react";
import axios from "axios";

export default function EmployeeManage() {
    const [employeeList, setEmployeeList] = useState(null);
    useEffect(() => {
        getEmployeesList()
    }, [])
    async function getEmployeesList() {
        try {
            const response = await axios.get('http://localhost:8080/pm/employees', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            console.log(response.data);
            setEmployeeList(response.data.content)
        } catch (error) {
            console.log(error);
        }
    }


}