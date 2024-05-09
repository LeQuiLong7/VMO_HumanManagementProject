import { useEffect, useState } from "react";
import Datatable from "../general/Datatable";
import { Button } from "@mui/material";
import ProjectDetailDialog from "./ProjectDetailDialog";
import useAxios from "../../hooks/useAxios";

export default function ProjectHistoryDataTable({url}) {
  const axios = useAxios()

  const [open, setOpen] = useState(false);
  const [selected, setSelected] = useState(-1);
  const [projectHistory, setProjectHistory] = useState([])

    useEffect(() => {
        fetchProjectHistory()
    }, []) 

  async function fetchProjectHistory() {

    const response = await axios.get(url);
    setProjectHistory(response.data.content);
  }
    const techColumns = [
        {
          name: 'id',
          label: 'ID'
        },
        {
          name: 'name',
          label: 'NAME'
        },
        {
          name: 'state',
          label: 'STATE'
        }
        ,
        {
          name: 'expectedStartDate',
          label: 'EXPECTED START DATE'
        }
        ,
        {
          name: 'expectedFinishDate',
          label: 'EXPECTED FINISH DATE'
        }
        ,
        {
          name: 'actualStartDate',
          label: 'ACTUAL START DATE'
        }
        ,
        {
          name: 'actualFinishDate',
          label: 'ACTUAL FINISH DATE'
        }
        ,
        {
          name: '',
          label: 'ACTION', 
          options: {
            customBodyRender: (value, tableMeta) => (
                  <Button
                    variant="outlined"
                    size="small"
                    onClick={e=>handleClick(tableMeta.rowIndex)}
                  >
                    Detail
                  </Button>
              ),
          }
        }
      ]
    
          
    const options = {
        selectableRows: 'none',
        rowsPerPage: 5,
        rowsPerPageOptions: [5, 10, 20]
    };

    function handleClick(rowIndex) {

        setSelected(rowIndex);
        setOpen(true)
    }

      return (
        <>
                {projectHistory.length != 0 && <Datatable data={projectHistory.map(e => e.projectInfo)} options={options} columns={techColumns} />}
                {open  && <ProjectDetailDialog project={projectHistory[selected]} open={open} setOpen={setOpen}/>}
        </>
      )
}