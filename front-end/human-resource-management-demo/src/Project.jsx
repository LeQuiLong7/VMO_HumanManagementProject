import { useEffect, useState } from "react"
import axios from "axios";
import ProjectDetail from "./ProjectDetail";


export default function Project() {
    const [projects, setprojects] = useState(null);
    const [createState, setCreateState] = useState(false);


    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [expectedStartDate, setExpectedStartDate] = useState("");
    const [expectedFinishDate, setExpectedFinishDate] = useState("");
    const [error, setError] = useState(null);

    useEffect(() => {
        getAllProjects();
    }, [])


    async function getAllProjects() {
        try {
            const response = await axios.get('http://localhost:8080/admin/project', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            console.log(response.data);
            setprojects(response.data)
        } catch (error) {
            console.log(error);
        }
    }

    async function createNewprojectRequest() {
        const data = {
            name, description, expectedStartDate, expectedFinishDate
        }
        try {
            const response = await axios.post('http://localhost:8080/admin/project', data, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            setCreateState(false);
            getAllProjects();
            setName("")
            setError(false)
            setDescription("")
            setExpectedFinishDate(null)
            setExpectedStartDate(null)

        } catch (error) {
            console.log(error);
            setError(error.response.data.details)
        }
    }

    return (
        <>
            <h1 className="padding-10">project Request</h1>
            <table id="leave-table">
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>State</th>
                    <th>Expected start date</th>
                    <th>Expected finish date</th>
                    <th>Actual start date</th>
                    <th>Actual finish date</th>
                    <th></th>
                </tr>
                {projects ? projects.content.map(project => (
                   <ProjectDetail projectt={project}/>
                )) : (<h3>Empty!</h3>)}
            </table>
            <button id="leave-create-button" onClick={e => setCreateState(true)}>Create new</button>
            {
                createState && (

                    <div className="pop-up">
                        <button className="close-button" onClick={e => setCreateState(false)}>&times;</button>
                        <h4 >Name: <input value={name} onChange={e => setName(e.target.value)} type="text" /></h4>
                        <h4 >Description: <input value={description} onChange={e => setDescription(e.target.value)} type="text" /></h4>
                        <h4>Expected Start Date: <input type="date" value={expectedStartDate} onChange={e => setExpectedStartDate(e.target.value)} /></h4>
                        <h4>Expected Finish Date: <input type="date" value={expectedFinishDate} onChange={e => setExpectedFinishDate(e.target.value)} /></h4>
                        {error && <h3 className="red">{JSON.stringify(error)}</h3>}
                        <button id="create-inside" onClick={createNewprojectRequest} >Create</button>
                    </div>
                )
            }
        </>

    )
}