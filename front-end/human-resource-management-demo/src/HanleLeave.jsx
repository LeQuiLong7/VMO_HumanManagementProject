import { useState, useEffect } from "react";
import axios from "axios";
export default function HandleLeave() {
    const [employeeList, setEmployeeList] = useState(null);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const [attendanceDetails, setAttendanceDetails] = useState([
        { requestId: '', status: '' }
    ]);


    useEffect(() => {
        getLeaveRequestList()
    }, [])

    async function getLeaveRequestList() {
        try {
            const response = await axios.get('http://localhost:8080/pm/leave?size=1000', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            console.log(response.data);
            setEmployeeList(response.data.content.filter(a => a.status == 'PROCESSING'))
        } catch (error) {
            console.log(error);
        }
    }

    const handleAddAttendance = () => {
        setEmployeeList(employeeList.filter(e => e.id != attendanceDetails.at(-1).requestId))
        setAttendanceDetails([...attendanceDetails, { requestId: '', status: '' }]);
    };


    async function handleSubmit() {

        const a = [...attendanceDetails];
        a.pop();
        const data = a ;
        try {
            const response = await axios.put('http://localhost:8080/pm/leave', data, {
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
            <h1>Handle leave request</h1>
            {attendanceDetails.map((attendance, index) => (
                <div key={index} className="attendance-details">
                    {employeeList && (
                        attendance.requestId != '' ? (
                            <input
                                type="text"
                                value={attendance.requestId}
                                disabled
                            >
                            </input>
                        ) : (

                            <select

                                name="requestId"
                                onChange={(event) => handleChange(index, event)}
                            >
                                <option value="">Select Request ID</option>
                                {employeeList.map((e) => (
                                    <option key={e.id} value={e.id}>
                                        {e.id}
                                    </option>
                                ))}
                            </select>
                        )



                    )}
                    <select
                        name="status"
                        onChange={(event) => handleChange(index, event)}
                    >
                        <option value="">Select status</option>
                        <option value="REJECTED">REJECT</option>
                        <option value="ACCEPTED">ACCEPT</option>
                    </select>
                </div>
            ))}
            <button onClick={handleAddAttendance}>Add Attendance</button>
            <button onClick={handleSubmit}>Submit Attendance</button>
            {error && <h3 className="red">{error}</h3>}
            {success && <h3 className="blue">{success}</h3>}
        </div>
    );

}