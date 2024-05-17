
import * as React from 'react';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Unstable_Grid2';
import { useEffect } from 'react';
// import axios from 'axios';
import AccountInfo from './AccountInfo';
import { useState } from 'react';
import { AccountDetailsForm } from './AccountDetailsForm';
import { Box, Card, CardHeader, Divider, Paper, Skeleton } from '@mui/material';
import ProjectHistoryDataTable from './ProjectHistoryDataTable';
import Datatable from '../general/Datatable';
import useAxios from '../../hooks/useAxios';
import Chart from '../test/Chart';
export default function Profile() {

  const axios = useAxios()

  const [user, setUser] = useState(null)
  const [tech, setTech] = useState(null)
  const [thisMonthEffort, setThisMonthEffort] = useState([])
  const [thisYearEffort, setThisYearEffort] = useState([])

  useEffect(() => {
    fetchProfile();
    fetchTechInfo();
    fetchThisMonthEffortHistory();
    fetchThisYearEffortHistory();
  }, [])

  async function fetchProfile() {
    const response = await axios.get("/profile");
    setUser(response.data);
  }

  async function fetchTechInfo() {
    const response = await axios.get("/profile/tech");
    setTech(response.data);
  }
  async function fetchThisMonthEffortHistory() {
    const response = await axios.get("/profile/effort");
    console.log(response.data);
    setThisMonthEffort(response.data);
  }
  async function fetchThisYearEffortHistory() {
    const response = await axios.get("/profile/effort?year=true");
    console.log(response.data);
    setThisYearEffort(response.data);
  }

  return (

    <>
      {user == null ? (
        <Skeleton animation='wave' variant="rectangular" width={'100%'} height={'80vh'} ></Skeleton>
      ) : (

        <Stack spacing={1}>
          <Typography variant="h4">Account</Typography>
          <Grid container spacing={3} >
            <Grid item={+true} lg={4} md={6} xs={12} flexGrow={1}>
              <AccountInfo avatarUrl={user.avatarUrl} name={user.lastName + ' ' + user.firstName} email={user.email} role={user.role} />
            </Grid>
            <Grid item={+true} lg={8} md={6} xs={12}>
              <AccountDetailsForm user={user} setUser={setUser} />
            </Grid>
            <Grid item={+true} xs={12}>
              <Paper sx={{ width: '100%', mt: 5 }}>
                <CardHeader title="Project history" />
                <Divider />
                <ProjectHistoryDataTable url={`http://localhost:8080/employee/project`} />
              </Paper>
            </Grid>
          </Grid>

          <Card  >
            <CardHeader title='Effort history'/>
            <Box>
              <Box>
                <Typography textAlign='center' variant='h6' gutterBottom>This month effort</Typography>
                <Chart xAxisData={thisMonthEffort.map(e => new Date(e.date))} yAxisData={thisMonthEffort.map(e => e.effort)} fullDate={true} />
              </Box>
              <Divider/>
              <Box>
                <Typography textAlign='center' variant='h6' marginTop={5}>This year effort</Typography>
                <Chart xAxisData={thisYearEffort.map(e => new Date(e.date))} yAxisData={thisYearEffort.map(e => e.effort)} fullDate={false} />
              </Box>
            </Box>
          </Card>
        </Stack>

      )}

    </>
  )
}