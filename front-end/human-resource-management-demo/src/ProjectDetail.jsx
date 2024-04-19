import { useState } from "react"

export default function ProjectDetail({ projectt }) {

    const [project, setProject] = useState(projectt);


    return (

        <tr>
            <td>{project.id}</td>
            <td>{project.name}</td>

            <td>{project.description}</td>
            <td>
                <select
                    onChange={e => setProject(prev => ({
                        ...prev,
                        state: e.target.value
                    }))}
                    value={project.state}
                >
                    {project.state === "INITIATION" ? (
                        <>
                            <option value="INITIATION">INITIATION</option>
                            <option value="ON_GOING">ON_GOING</option>
                        </>
                    ) : project.state === "ON_GOING" ? (
                        <>
                            <option value="ON_GOING">ON_GOING</option>
                            <option value="FINISHED">FINISH</option>
                        </>
                    ) : (
                        <option value={project.state}>{project.state}</option>
                    )}
                </select>
            </td>
            <td>{project.expectedStartDate}</td>
            <td>{project.expectedFinishDate}</td>
            <td><input type="date" value={project.actualStartDate} onChange={e => setProject(prev => ({
                        ...prev,
                        actualStartDate: e.target.value
                    }))}/>
            </td>
            <td><input type="date" value={project.actualFinishDate} onChange={e => setProject(prev => ({
                        ...prev,
                        actualFinishDate: e.target.value
                    }))}/>
            </td>
            <td><button>Update</button></td>
        </tr>
    )
}