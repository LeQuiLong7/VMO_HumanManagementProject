import Datatable from "../../general/Datatable";
import { useState, useEffect } from "react";
import SendIcon from "@mui/icons-material/Send";
import {
  Button,
  Stack,
  TextField,
} from "@mui/material";
import MyLoadingButton from "../../general/MyLoadingButton";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";

import AssignEmployeeToProjectDialog from "./AsignEmployeeToProjectDialog";
import { handleChange as handleChangeArray } from "../../util/Helper";
import useAxios from "../../../hooks/useAxios";

export default function ProjectDataTable({ data, setData }) {

  const axios = useAxios();

  const header = "Project list";

  const [assignState, setAssignSate] = useState(false);
  const [selectedProject, setSelectedProject] = useState(-1);
  const [allTech, setAllTech] = useState([]);

  async function fetchTechStack() {
    const response = await axios.get("/admin/techStack?size=100");
    setAllTech(response.data.content);
  }

  useEffect(() => {
    fetchTechStack()
  }, [])


  const handleChange = (event, rowIndex) => {
    handleChangeArray(event, rowIndex, data, setData)
  };

  async function handleUpdate(rowData, rowIndex) {
    const newState = {
      id: rowData[0],
      newState: rowData[2],
      actualStartDate: rowData[5],
      actualFinishDate: rowData[6],
    };
    try {
      const response = await axios.put("/admin/project?sort=id,desc", newState);
      const newData = [...data];
      newData[rowIndex] = response.data;
      setData(newData);
    } catch (error) {
      throw error
    }
  }

  const columns = [
    {
      name: "id",
      label: "ID",
    },
    {
      name: "name",
      label: "NAME",
    },
    {
      name: "state",
      label: "STATE",
      options: {
        customBodyRender: (value, tableMeta) => (
          <FormControl
            sx={{ m: 1, minWidth: 120 }}
            size="small"
          >
            <Select
              sx={{
                color: 'white',
                bgcolor: value === 'FINISHED' ? 'success.light' : value === 'ON_GOING' ? 'primary.main' : 'primary.light'
              }}
              label="State"
              value={value}
              name="state"
              onChange={(e) => handleChange(e, tableMeta.rowIndex)}
            >
              <MenuItem value={"INITIATION"}>INITIATION</MenuItem>
              <MenuItem value={"ON_GOING"}>ON_GOING</MenuItem>
              <MenuItem value={"FINISHED"}>FINISHED</MenuItem>
            </Select>
          </FormControl>
        ),
      },
    },
    {
      name: "expectedStartDate",
      label: "EXPECTED START DATE",
      options: {
        customBodyRender: (value) => (
          <TextField
            variant="outlined"
            sx={{ width: "150px" }}
            type="date"
            size="small"
            disabled
            value={value}
          />
        ),
      },
    },
    {
      name: "expectedFinishDate",
      label: "EXPECTED FINISH DATE",
      options: {
        customBodyRender: (value) => (
          <TextField
            variant="outlined"
            sx={{ width: "150px" }}
            size="small"
            type="date"
            disabled
            value={value}
          />
        ),
      },
    },
    {
      name: "actualStartDate",
      label: "ACTUAL START DATE",
      options: {
        customBodyRender: (value, tableMeta) => (
          <TextField
            variant="outlined"
            sx={{ width: "150px" }}
            value={value}
            name="actualStartDate"
            onChange={(e) => handleChange(e, tableMeta.rowIndex)}
            size="small"
            type="date"
          />
        ),
      },
    },
    {
      name: "actualFinishDate",
      label: "ACTUAL FINISH DATE",
      options: {
        customBodyRender: (value, tableMeta) => (
          <TextField
            variant="outlined"
            sx={{ width: "150px" }}
            value={value}
            name="actualFinishDate"
            size="small"
            type="date"
            onChange={(e) => {
              console.log(tableMeta);
              handleChange(e, tableMeta.rowIndex);
            }}
          />
        ),
      },
    },
    {
      name: "",
      label: "",
      options: {
        customBodyRender: (value, tableMeta) => (
          <Stack direction={"row"} spacing={1}>
            <MyLoadingButton
              startIcon={<SendIcon />}
              text={"Update"}
              size="small"
              variant="contained"
              onClick={() =>
                handleUpdate(tableMeta.rowData, tableMeta.rowIndex)
              }
            />
            <Button
              variant="contained"
              size="small"
              onClick={(e) => assignButtonOnClick(tableMeta.rowData[0])}
            >
              Assign
            </Button>
          </Stack>
        ),
      },
    },
  ];

  function assignButtonOnClick(projectId) {
    setSelectedProject(projectId);
    setAssignSate(true)
  }
  const options = {
    selectableRows: 'none',
    rowsPerPage: 5,
    rowsPerPageOptions: [5, 10, 20]
  };

  return (
    <>

      {data && (
        <Datatable
          data={data}
          columns={columns}
          setData={setData}
          options={options}
          header={header}
        />
      )}
      {allTech.length != 0 && selectedProject != -1 && (
        <AssignEmployeeToProjectDialog allTech={allTech} assignState={assignState} selectedProject={selectedProject} setAssignSate={setAssignSate} />
      )}
    </>
  );
}
