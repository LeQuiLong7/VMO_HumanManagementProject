
import { useState, useEffect } from "react";
import {
  Avatar,
  Button,
  Chip,
  Grid,
  Typography,
} from "@mui/material";
import Box from "@mui/material/Box";
import BasicDialog from "../../general/BasicDialog";
import Datatable from "../../general/Datatable";
import AssignDialog from "./AssignDialog";
import useAxios from "../../../hooks/useAxios";
import SearchComponent from "./SearchComponent";

export default function AssignEmployeeToProjectDialog({ selectedProject, assignState, setAssignSate, allTech }) {

  const axios = useAxios()
  const [popup, setPopup] = useState(false);
  const [data, setData] = useState([]);
  const [tableState, setTableState] = useState({
    total: -1,
    lastPage: 0
  });
  const [employeesInsideProject, setEmployeesInsideProject] = useState([]);

  const fetchAll = {
    logicOperator: 'AND',
    logics: [
      {
        column: 'id',
        values: employeesInsideProject.map(e => e.employeeId),
        queryOperator: 'NOT_IN'
      }
    ]

  }
  useEffect(() => {
    fetchEmployeeInsideProject();
  }, [selectedProject]);


  useEffect(() => {
    fetchEmployees(0, 2, fetchAll);
  }, [employeesInsideProject]);



  async function fetchEmployeeInsideProject() {
    const employeeResponse = await axios.get(`/admin/project/${selectedProject}/employees?size=100`);
    setEmployeesInsideProject(employeeResponse.data);
  }

  function handleDelete(empId) {
    console.log(employeesInsideProject, empId);
    setEmployeesInsideProject(
      employeesInsideProject.filter((e) => e.employeeId != empId)
    );
  }


  function buildDialogContent() {
    const chip = (
      <Box key={1} >
        <Typography variant="h6" gutterBottom>
          Already assigned
        </Typography>
        <Grid container sx={{ width: "100%" }}>
          {employeesInsideProject.map((t, index) => (
            <Grid key={index} item xs={2} >
              <Chip color="primary"
                sx={{ width: '200px' }}
                label={t.employeeId + " - " + t.employeeName + " - " + t.effort + "%"}
                onDelete={e => handleDelete(t.employeeId)}
              />
            </Grid>
          ))}
        </Grid>
      </Box>
    );

    return [chip, 
    <SearchComponent key={4} 
    searchQuery={searchQuery}
    setSearchQuery={setSearchQuery}
    convertToSearchRequest={convertToSearchRequest}
      allTech={allTech} 
      fetchEmployees={fetchEmployees} 
      fetchAll={fetchAll}
      />,
      <Datatable key={23}
        data={data
          .map(e => ({
            ...e.employeeInfo,
            techInfo: e.techInfo,
            projects: e.projectInfo
          }))}
        columns={columns}
        setData={setData}
        options={options}
        header={"Employee list"}
      />];
  }
  const [searchQuery, setSearchQuery] = useState({
    roles: [],
    techs: [],
    numberOfOnGoingProjects: '',
    currentEffort: ''
})


function convertToSearchRequest() {
    const logics = [];
    if (searchQuery.numberOfOnGoingProjects != '') {
      logics.push({
        column: 'numberOfOnGoingProjects',
        value: searchQuery.numberOfOnGoingProjects,
        queryOperator: 'LTE'
      });
    }
    if (searchQuery.currentEffort != '') {
      logics.push({
        column: 'currentEffort',
        value: searchQuery.currentEffort,
        queryOperator: 'LTE'
      });
    }
    if (searchQuery.roles.length !== 0) {
      logics.push({
        column: 'role',
        values: searchQuery.roles,
        queryOperator: 'IN'
      });
    }
  
    if (searchQuery.techs.length !== 0) {
      const techLogics = searchQuery.techs.map(tech => ({
        logicOperator: 'AND',
        logics: [
          {
            column: 'techs.id.techId',
            value: tech.techId,
            queryOperator: 'EQ'
          },
          {
            column: 'techs.yearOfExperience',
            value: tech.yearOfExperience,
            queryOperator: 'GTE'
          }
        ]
      }));
      
      logics.push({
        logicOperator: 'OR',
        logics: techLogics
      });
    }
  
    logics.push({
      column: 'id',
      values: employeesInsideProject.map(e => e.employeeId),
      queryOperator: 'NOT_IN'
    });
  
    return {
      logicOperator: 'AND',
      logics: logics
    };
  }





  const columns = [
    {
      name: "id",
      label: "ID",
    },
    {
      name: "avatarUrl",
      label: "AVATAR",
      options: {
        customBodyRender: (value, tableMeta) =>
          <Avatar src={value + "?" +Math.random().toString(36)} >{tableMeta.rowData[2]}</Avatar>
      },
    },
    {
      name: "firstName",
      label: "FIRST NAME",
    },
    {
      name: "lastName",
      label: "LAST NAME",
    },

    {
      name: "role",
      label: "ROLE",
    },
    {
      name: "techInfo",
      label: "TECH INFO",
      options: {
        customBodyRender: (techs) => (
          techs.map(t => (
            <Chip sx={{ margin: 1 }}
              label={t.techName + " " + t.yearOfExperience}
            />
          ))
        ),
      },
    },

    {
      name: "projects",
      label: "TECH INFO",
      options: {
        customBodyRender: (projects) => (
          <Chip
            label={'On going projects: ' + projects.filter(p => p.state === 'ON_GOING').length}
          />
        ),
      },
    },
    {
      name: "currentEffort",
      label: "CURRENT EFFORT",
    },
    {
      name: "",
      label: "ACTION",
      options: {
        customBodyRender: (e, tableMeta) => (
          <Button variant="contained" size="small" onClick={e => handleAdd(tableMeta.rowData)}>Add</Button>
        ),
      },
    },
  ];

  async function fetchEmployees(pageNumber, pageSize, searchRequest) {
    const response = await axios.post(`/search/employees?page=${pageNumber}&size=${pageSize}`, searchRequest);
    if (pageNumber == 0) {
      setData(response.data.content);
    } else {
      setData([...data, ...response.data.content]);

    }
    setTableState({
      lastPage: pageNumber == 0 ? 0 : pageNumber > tableState.lastPage ? pageNumber : tableState.lastPage,
      total: response.data.totalElements
    })
  }

  async function handlePageChange(page) {
    if (page > tableState.lastPage) {
        fetchEmployees(page, 2, convertToSearchRequest())
    }
  }

  const options = {
    selectableRows: "none",
    rowsPerPageOptions: [2],
    rowsPerPage: 2,
    count: tableState.total,
    onChangePage: handlePageChange
  };

  const [selectedEmployee, setSelectedEmployee] = useState({
    id: '',
    name: '',
    currentEffort: '',
    effort: ''
  })


  function handleAdd(rowData) {
    setSelectedEmployee({
      id: rowData[0],
      name: rowData[3] + ' ' + rowData[2],
      currentEffort: rowData[7],
      effort: ''
    })
    setPopup(true);
  }


  async function handleSendUpdate() {
    const data = {
      projectId: selectedProject,
      assignList: employeesInsideProject.map((e) => ({
        employeeId: e.employeeId,
        effort: e.effort
      })),
    };
    try {
      await axios.put(`/admin/project/assign`,data);

    } catch (error) {
      throw error
    }
    setTimeout(() => {
      setAssignSate(false);
    }, 2000);
  }



  return (
    <>
      <AssignDialog open={popup} setOpen={setPopup} employee={selectedEmployee} setEmployee={setSelectedEmployee} setEmployeesInsideProject={setEmployeesInsideProject}/>
      <BasicDialog
        fullScreen={true}
        title="Assign employees to project"
        open={assignState}
        handleClose={(e) => setAssignSate(false)}
        action="Update"
        content={buildDialogContent()}
        buttonText="Update"
        onButtonClick={handleSendUpdate}
      />
    </>
  );
}
