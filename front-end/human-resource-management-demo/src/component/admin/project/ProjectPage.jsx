
import { useState, useEffect } from "react";
import CreateNewProjectDialog from "./CreateNewProjectDialog";
import ProjectDataTable from "./ProjectDataTable";
import CreateNewButton from "../../general/CreateNewButton";
import useAxios from "../../../hooks/useAxios";

export default function ProjectPage() {
  const axios = useAxios();

  const [createState, setCreateState] = useState(false);
  const [data, setData] = useState([]);

  useEffect(() => {
    fetchProjects();
  }, []);

  async function fetchProjects() {
    const reponse = await  axios.get("/admin/project?size=100&sort=id,desc");
      
    setData(
      reponse.data.content.map((project) => ({
        ...project,
        actualStartDate: project.actualStartDate || "",
        actualFinishDate: project.actualFinishDate || "",
      }))
    );
  }




  return (
    <>
      <CreateNewButton onClickEvent={(e) => setCreateState(!createState)} />
      <CreateNewProjectDialog setData={setData} open={createState} setOpen={setCreateState} />
       <ProjectDataTable data={data} setData={setData}/>
    </>
  );
}
