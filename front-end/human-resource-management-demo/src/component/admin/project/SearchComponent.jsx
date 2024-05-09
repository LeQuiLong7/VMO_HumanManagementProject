
import { useState, useEffect } from "react";
import {
    Avatar,
    Button,
    Chip,
    FormControl,
    Grid,
    InputLabel,
    Stack,
    TextField,
    Typography,
} from "@mui/material";

import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import { handleUpdateStateOneObject } from "../../util/Helper";
export default function SearchComponent({allTech, fetchEmployees, employeesInsideProject, fetchAll }) {
    const roles = ['ADMIN', 'PM', 'EMPLOYEE']
    
  async function handleFilter() {
    fetchEmployees(0, 2, convertToSearchRequest());
  }

  const [tech, setTech] = useState({
    techId: '',
    techName: '',
    yearOfExperience: ''
  })

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
    



    return (
            <Stack key={5} spacing={2} mb={2} mt={2}>
              <Typography variant="h6" gutterBottom>Search</Typography>
              <Grid container spacing={2}>

                {searchQuery.roles.map((r, index) =>
                  <Grid item key={index}>
                    <Chip label={r} color="primary" onDelete={e => {
                      setSearchQuery({
                        ...searchQuery,
                        roles: searchQuery.roles.filter(role => role != r)
                      })
                    }} />
                  </Grid>
                )}
                {searchQuery.techs.map((t, index) =>
                  <Grid item  key={index} >
                    <Chip label={t.techName + ' >= ' + t.yearOfExperience + ' years of experience'} key={index} color="primary" 
                    onDelete={e => {
                      setSearchQuery({
                        ...searchQuery,
                        techs: searchQuery.techs.filter(te => te.techId != t.techId)
                      })}}
                    />
                  </Grid>
      
                )}
              </Grid>
              <Stack direction={'row'} spacing={2} >
                <FormControl>
                  <InputLabel id="demo-simple-select-label">Role</InputLabel>
                  <Select 
                  value={searchQuery.roles.length == 0 ? '' : searchQuery.roles[searchQuery.roles.length - 1]}
                  labelId="demo-simple-select-label" label={'ROLE'} sx={{ width: '200px' }} onChange={e => {
                    setSearchQuery({
                      ...searchQuery,
                      roles: searchQuery.roles.includes(e.target.value) ? searchQuery.roles : [...searchQuery.roles, e.target.value]
                    })
                  }}>
                    {
                      roles.map((role, index) => (
                        <MenuItem key={index} value={role}>
                          {role}
                        </MenuItem>
                      ))
                    }
                  </Select>
                </FormControl>
                <FormControl>
                  <InputLabel id="demo-simple-select-label2">Tech</InputLabel>
                  <Select
                    value={tech.techId}
                    onChange={e => {
                      setTech({ ...tech, techId: e.target.value, techName: allTech.filter(a => a.id == e.target.value)[0].name })
                    }}
                    name="techId" labelId="demo-simple-select-label2" label={'Tech'} sx={{ width: '200px' }} >
                    {
                      allTech.map((tech, index) => (
                        <MenuItem key={index} value={tech.id}>
                          {tech.name}
                        </MenuItem>
                      ))
                    }
                  </Select>
                </FormControl>
                <TextField
                  onChange={e => setTech({ ...tech, yearOfExperience: e.target.value })}
                  name="yearOfExperience"
                  label="Year of experience"
                  type="number"
                />
                <Button size="small" variant="outlined" onClick={e => {
                  if (searchQuery.techs.filter(t => t.techId == tech.techId).length == 0) {
                    setSearchQuery({
                      ...searchQuery,
                      techs: [...searchQuery.techs, tech]
                    })
                  } else {
                    const newTech = [...searchQuery.techs]
                    let i = -1
                    newTech.forEach((e, index) => {
                      if (e.techId === tech.techId) {
                        i = index;
                      }
                    })
                    newTech[i]['yearOfExperience'] = tech.yearOfExperience
                    
                      setSearchQuery({
                        ...searchQuery,
                        techs: newTech
                      })
                  }
                }}>Add</Button>
                <TextField
                  value={searchQuery.numberOfOnGoingProjects}
                  onChange={e => handleUpdateStateOneObject(e, setSearchQuery)}
                  name="numberOfOnGoingProjects"
                  label="Number of on going projects"
                  type="number"
                />
                  <TextField
                  value={searchQuery.currentEffort}
                  onChange={e => handleUpdateStateOneObject(e, setSearchQuery)}
                  name="currentEffort"
                  label="CurrentEffort"
                  type="number"
                />
                <Button variant="contained" onClick={handleFilter}>Filter</Button>
                <Button variant="outlined" onClick={e => {
                  setSearchQuery({
                    roles: [],
                    techs: [],
                    numberOfOnGoingProjects: '',
                    currentEffort: ''
                })
                  fetchEmployees(0, 2, fetchAll)
                }
                }>Clear</Button>
              </Stack>
            </Stack>      
    )

}