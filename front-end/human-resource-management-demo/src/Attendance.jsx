import { useState, useEffect } from "react";
import axios from "axios";
export default function Attendance() {

    const [employeeList, setEmployeeList] = useState(null);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    useEffect(() => {
        getEmployeesList()
    }, [])

    async function getEmployeesList() {
        try {
            const response = await axios.get('http://localhost:8080/pm/employees?size=1000', {
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

    const [attendanceDetails, setAttendanceDetails] = useState([
        { employeeId: '', timeIn: '', timeOut: '' }
    ]);

    const handleAddAttendance = () => {
        setEmployeeList(employeeList.filter(e => e.id != attendanceDetails.at(-1).employeeId))
        setAttendanceDetails([...attendanceDetails, { employeeId: '', timeIn: '', timeOut: '' }]);
    };


    async function handleSubmit () {
        console.log(employeeList);
        if(employeeList.length > 0) {
            setError("Add attendance for all employees")
        } else {
            const a = [...attendanceDetails];
            a.pop();
            const data = {
                "attendanceDetails" : a
            }
            try {
                const response = await axios.post('http://localhost:8080/pm/attendance', data, {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('access-token')}`
                    }
                });
                setSuccess("Submitted successfully")
                setError(null)
            } catch (error) {
                setSuccess(null)
                setError(error.response.data.error)
                console.log(error);
            }
        }
    };


    const handleChange = (index, event) => {
        const { name, value } = event.target;
        const updatedAttendance = [...attendanceDetails];
        updatedAttendance[index][name] = value;
        console.log(updatedAttendance);
        setAttendanceDetails(updatedAttendance);
    };
    return (
        <div className="attendance-form">
            <h1>Check Attendance</h1>
            {attendanceDetails.map((attendance, index) => (
                <div key={index} className="attendance-details">
                    {employeeList && (
                        attendance.employeeId != '' ? (
                            <input
                                type="text"
                                value={attendance.employeeId}
                                disabled
                            >
                            </input>
                        ) : (

                            <select

                                name="employeeId"
                                onChange={(event) => handleChange(index, event)}
                            >
                                <option value="">Select Employee ID</option>
                                {employeeList.map((e) => (
                                    <option key={e.id} value={e.id}>
                                        {e.id}
                                    </option>
                                ))}
                            </select>
                        )



                    )}
                    <input
                        type="time"
                        name="timeIn"
                        value={attendance.timeIn}
                        onChange={(event) => handleChange(index, event)}
                    />
                    <input
                        type="time"
                        name="timeOut"
                        value={attendance.timeOut}
                        onChange={(event) => handleChange(index, event)}
                    />
                </div>
            ))}
            <button onClick={handleAddAttendance}>Add Attendance</button>
            <button onClick={handleSubmit}>Submit Attendance</button>
            {error && <h3 className="red">{error}</h3>}
            {success && <h3 className="blue">{success}</h3>}
        </div>
    );
}