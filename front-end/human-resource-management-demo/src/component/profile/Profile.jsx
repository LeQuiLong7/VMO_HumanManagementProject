
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

  useEffect(() => {
    fetchProfile();
    fetchTechInfo();
  }, [])

  async function fetchProfile() {

    const response = await axios.get("/profile");
    setUser(response.data);
  }

  async function fetchTechInfo() {

    const response = await axios.get("/profile/tech");
    setTech(response.data);
  }

  const techColumns = [
    {
      name: 'techId',
      label: 'TECH ID'
    },
    {
      name: 'techName',
      label: 'TECH NAME'
    },
    {
      name: 'yearOfExperience',
      label: 'YEAR OF EXPERIENCE'
    }
  ]

  const options = {
    selectableRows: 'none',
    rowsPerPage: 5,
    rowsPerPageOptions: [5, 10, 20]
  };

  const date = [new Date(2024, 4, 1), new Date(2024, 4, 2), new Date(2024, 4, 3), new Date(2024, 4, 4), new Date(2024, 4, 5), new Date(2024, 4, 6)];
  const data = [30, 50, 40, 60, 90, 90]

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
            {/* <Grid item={+true} xs={12}>
              <Paper sx={{ width: '100%', mt: 5 }}>
                <CardHeader title="Tech details" />
                <Divider />
                {tech && <Datatable data={tech.techInfo} options={options} columns={techColumns} />}
              </Paper>
            </Grid> */}
            <Grid item={+true} xs={12}>
              <Paper sx={{ width: '100%', mt: 5 }}>
                <CardHeader title="Project history" />
                <Divider />
                <ProjectHistoryDataTable url={`http://localhost:8080/employee/project`} />
              </Paper>
            </Grid>
          </Grid>
            <Card sx={{ flexDirection: 'row', alignItems:'center', justifyContent:'space-between', display:'flex', padding:2}}>
              <Box>
                <Chart xAxisData={date} yAxisData={data} fullDate={true}/>
                <Typography textAlign='center' variant='h6'>This month effort</Typography>
              </Box>
              <Box>
                <Chart xAxisData={date} yAxisData={data} fullDate={false}/>
                <Typography textAlign='center'  variant='h6'>This year effort</Typography>
              </Box>
            </Card>
        </Stack>

      )}

    </>
  )
}