import { useEffect, useState } from "react"
import axios from "axios";

export default function Salary({ employee, setEmployee }) {



    const [raiseRequest, setRaiseRequest] = useState(null);
    const [createState, setCreateState] = useState(false);
    
    const [description, setDescription] = useState("");
    const [expectedSalary, setExpectedSalary] = useState("");

    const [error, setError] = useState(null);

    useEffect(() => {
        getAllRaiseRequest();
    }, [])


    async function getAllRaiseRequest() {
        try {
            const response = await axios.get('http://localhost:8080/employee/salary', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            console.log(response.data);
            setRaiseRequest(response.data)
        } catch (error) {
            console.log(error);
        }
    }

    async function createNewRaiseRequest() {
        const data = {
            expectedSalary, description
        }
        try {
            const response = await axios.post('http://localhost:8080/employee/salary', data, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            setCreateState(false);
            getAllRaiseRequest();
            setExpectedSalary("")
            setDescription("")

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
                    <th>Created at</th>
                    <th>Old salary</th>
                    <th>Expected salary</th>
                    <th>Description</th>
                    <th>Status</th>
                    <th>New salary</th>
                    <th>Approved by</th>
                </tr>
                {raiseRequest ? raiseRequest.content.map(raiseRequest => (
                    <tr>
                        <td>{raiseRequest.id}</td>
                        <td>{raiseRequest.createdAt}</td>
                        <td>{raiseRequest.expectedSalary}</td>
                        <td>{raiseRequest.oldSalary}</td>
                        <td>{raiseRequest.description}</td>
                        {raiseRequest.status == "PROCESSING" ? (
                            <td className="yellow">{raiseRequest.status}</td>
                        ) : raiseRequest.status === "REJECTED" ? (
                            <td className="red">{raiseRequest.type}</td>
                        ) : (
                            <td className="green">{raiseRequest.type}</td>
                        )
                        }
                        <td>{raiseRequest.newSalary}</td>
                        <td>{raiseRequest.approvedBy}</td>
                    </tr>
                )) : (<h3>Empty!</h3>)}
            </table>
            <button id="leave-create-button" onClick={e => setCreateState(true)}>Create new</button>
            {
                createState && (

                    <div className="pop-up">
                        <button class="close-button" onClick={e => setCreateState(false)}>&times;</button>
                        <h4 >Expected salary: <input type="number" value={expectedSalary} onChange={e => setExpectedSalary(e.target.value)} /></h4>
                        <h4>Description: <input type="text" value={description} onChange={e => setDescription(e.target.value)} /></h4>
                        {error && <h3 className="red">{error}</h3>}
                        <button id="create-inside" onClick={createNewRaiseRequest} >Create</button>
                    </div>
                )
            }
        </>

    )
}