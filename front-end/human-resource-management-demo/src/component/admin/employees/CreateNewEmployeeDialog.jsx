
import { useState, useEffect } from "react";
import {
  Avatar,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import BasicDialog from "../../general/BasicDialog";
import { handleUpdateStateOneObject } from "../../util/Helper";
import useAxios from "../../../hooks/useAxios";



export default function CreateNewEmployeeDialog({ createState, setCreateState }) {
  const axios = useAxios()

  const [pmList, setPMList] = useState([]);
  const [userInfo, setUserInfo] = useState({ 
    firstName: '',
    lastName: '',
    birthDate: '', 
    phoneNumber: '',
    personalEmail: '',
    currentSalary: '',
    role: 'EMPLOYEE', 
    managedBy: '' 
  })
  useEffect(() => {
    fetchPMList();
  }, [])

  async function fetchPMList() {
    const response = await axios.get("/admin/pm?size=100");
    setPMList(response.data.content);
  }

  function handleUserInfoUpdate(event) {
      handleUpdateStateOneObject(event, setUserInfo)
  }

  async function handleCreateNewEmployee() {
    try {
      await axios.post(`/admin/employees`, userInfo);
      setTimeout(() => {
        setCreateState(false);
        setUserInfo({ 
          firstName: '',
          lastName: '',
          birthDate: '', 
          phoneNumber: '',
          personalEmail: '',
          currentSalary: '',
          role: 'EMPLOYEE', 
          managedBy: '' 
        });
        window.location.reload()
      }, 2000);

    } catch (error) {
      throw error
    }
  }



  function buildTableContent() {

    return [
      <Grid key={1} container p={2}>
        <Grid xs={5} item m={2}>
          <TextField label='First name' onChange={handleUserInfoUpdate} name='firstName' value={userInfo.firstName} fullWidth required />
        </Grid>
        <Grid xs={5} item m={2} >
          <TextField label='Last name' name='lastName' onChange={handleUserInfoUpdate} fullWidth value={userInfo.lastName} required />
        </Grid>
        <Grid xs={5} item m={2}>
          <TextField label='Phone number' name='phoneNumber' onChange={handleUserInfoUpdate} fullWidth value={userInfo.phoneNumber} required />
        </Grid>
        <Grid xs={5} item m={2}>
          <TextField label='Birth date' InputLabelProps={{ shrink: true }} name='birthDate' onChange={handleUserInfoUpdate} type='date' fullWidth value={userInfo.birthDate} required />
        </Grid>
        <Grid xs={5} item m={2}>
          <TextField label='Personal email' name='personalEmail' onChange={handleUserInfoUpdate} fullWidth value={userInfo.personalEmail} required />
        </Grid>
        <Grid xs={5}item m={2}>
          <TextField label='Current salary' fullWidth name='currentSalary'  onChange={handleUserInfoUpdate}  type="number" value={userInfo.currentSalary} required />
        </Grid>
        <Grid xs={5} item m={2}>
          <FormControl fullWidth>
            <InputLabel >Managed by</InputLabel>
            <Select
              fullWidth
              label='Manged by'
              name="managedBy"
              value={userInfo.managedBy}
              onChange={handleUserInfoUpdate}
            >
              {pmList
                .map((e) => (
                  <MenuItem value={e.id} key={e.id}>
                    <Stack direction={"row"} spacing={3}>
                      <Typography>{e.id}</Typography>
                      <Avatar src={e.avatarUrl + "?" +Math.random().toString(36)}></Avatar>
                      <Typography>{e.lastName + " " + e.firstName}</Typography>
                    </Stack>
                  </MenuItem>
                ))}
            </Select>
          </FormControl >
        </Grid>
        <Grid xs={5} item m={2}>
          <FormControl fullWidth>
            <InputLabel >Role</InputLabel>
            <Select
              value={userInfo.role}
              fullWidth
              label="Role"
              name='role'
              onChange={handleUserInfoUpdate}
            >
              <MenuItem value='EMPLOYEE'>EMPLOYEE</MenuItem>
              <MenuItem value='PM'>PM</MenuItem>
              <MenuItem value='ADMIN'>ADMIN</MenuItem>
            </Select>
          </FormControl>
        </Grid>
      </Grid>
    ]
  }


  return (
    <>
      <BasicDialog
        title={'Create new employee'}
        open={createState}
        handleClose={e => setCreateState(false)}
        action={'Create'}
        content={buildTableContent()}
        buttonText="Create"
        onButtonClick={handleCreateNewEmployee}
      />
    </>

  );
}