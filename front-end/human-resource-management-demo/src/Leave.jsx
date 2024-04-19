import { useEffect, useState } from "react"
import axios from "axios";

export default function Leave({ employee, setEmployee }) {



    const [leaves, setLeaves] = useState(null);
    const [createState, setCreateState] = useState(false);
    const [leaveDate, setLeaveDate] = useState("");
    const [reason, setReason] = useState("");
    const [type, setLeaveType] = useState("UNPAID");
    const [error, setError] = useState(null);

    useEffect(() => {
        getAllLeaveRequest();
    }, [])


    async function getAllLeaveRequest() {
        try {
            const response = await axios.get('http://localhost:8080/leave', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            console.log(response.data);
            setLeaves(response.data)
        } catch (error) {
            console.log(error);
        }
    }

    async function createNewLeaveRequest() {
        const data = {
            leaveDate, reason, type
        }
        try {
            const response = await axios.post('http://localhost:8080/leave', data, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            setCreateState(false);
            getAllLeaveRequest();
            setLeaveDate("");
            setReason("");
            setLeaveType("UNPAIDs");

        } catch (error) {
            setError(error.response.data.error)
        }
    }

    return (
        <>
            <h1 className="padding-10">Leave Request</h1>
            <table id="leave-table">
                <tr>
                    <th>Id</th>
                    <th>Leave date</th>
                    <th>Leave type</th>
                    <th>Created at</th>
                    <th>Reason</th>
                    <th>Status</th>
                    <th>Approved by</th>
                </tr>
                {leaves ? leaves.content.map(leave => (
                    <tr>
                        <td>{leave.id}</td>
                        <td>{leave.date}</td>
                        {leave.type == "PAID" ? (
                            <td className="green">{leave.type}</td>
                        ) : (
                            <td className="red">{leave.type}</td>
                        )}
                        <td>{leave.createdAt}</td>
                        <td>{leave.reason}</td>
                        {leave.status === "PROCESSING" ? (
                            <td className="yellow">{leave.status}</td>
                        ) : leave.status === "REJECTED" ? (
                            <td className="red">{leave.status}</td>
                        ) : (
                            <td className="green">{leave.status}</td>
                        )}
                        <td>{leave.approvedBy}</td>
                    </tr>
                )) : (<h3>Empty!</h3>)}
            </table>
            <button id="leave-create-button" onClick={e => setCreateState(true)}>Create new</button>
            {
                createState && (

                    <div className="pop-up">
                        <button class="close-button" onClick={e => setCreateState(false)}>&times;</button>
                        <h4 >Leave date: <input value={leaveDate} onChange={e => setLeaveDate(e.target.value)} type="date" /></h4>
                        <h4>Reason: <input type="text" value={reason} onChange={e => setReason(e.target.value)} /></h4>
                        <h4>Type:
                            <select value={type} onChange={e => setLeaveType(e.target.value)}>
                                <option value="PAID">PAID</option>
                                <option value="UNPAID">UNPAID</option>
                            </select>
                        </h4>
                        {error && <h3 className="red">{error}</h3>}
                        <button id="create-inside" onClick={createNewLeaveRequest} >Create</button>
                    </div>
                )
            }
        </>

    )
}