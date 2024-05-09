
import * as React from 'react';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import SendIcon from '@mui/icons-material/Send';
import CardContent from '@mui/material/CardContent';
import CardHeader from '@mui/material/CardHeader';
import Divider from '@mui/material/Divider';
import { useEffect } from 'react';
import Grid from '@mui/material/Unstable_Grid2';
import { TextField, useStepContext } from '@mui/material';
import MyLoadingButton from '../general/MyLoadingButton';
import { handleUpdateStateOneObject } from '../util/Helper';
import useAxios from '../../hooks/useAxios';

export function AccountDetailsForm({ user, setUser }) {
  const axios = useAxios();

  const [userInfo, setUserInfo] = React.useState(user)

  function handleUpdateState(event) {
      handleUpdateStateOneObject(event, setUserInfo);
  }

  async function handleUpdateInfo() {
    const data = userInfo;
    try {
        const response = await axios.put("/profile", data);
        setUser(response.data)
    }catch(error) {
      throw error
    }
  }

  return (
    <Card>
      <CardHeader subheader="The information can be edited" title="Profile" />
      <Divider />
      <CardContent>
        <Grid container spacing={3}>
          <Grid md={6} xs={12}>
            <TextField label='Employee id' InputLabelProps={{ shrink: true }} disabled fullWidth value={userInfo.id} />
          </Grid>
          <Grid md={6} xs={12}>
            <TextField label='Email' InputLabelProps={{ shrink: true }} disabled fullWidth value={userInfo.email} />
          </Grid>
          <Grid md={6} xs={12}>
            <TextField label='First name' InputLabelProps={{ shrink: true }} onChange={handleUpdateState} name='firstName' value={userInfo.firstName} fullWidth required />
          </Grid>
          <Grid md={6} xs={12}>
            <TextField label='Last name' name='lastName' InputLabelProps={{ shrink: true }} onChange={handleUpdateState} fullWidth value={userInfo.lastName} required />
          </Grid>
          <Grid md={6} xs={12}>
            <TextField label='Phone number' name='phoneNumber' InputLabelProps={{ shrink: true }} onChange={handleUpdateState} fullWidth value={userInfo.phoneNumber} required />
          </Grid>
          <Grid md={6} xs={12}>
            <TextField label='Birth date' name='birthDate' InputLabelProps={{ shrink: true }} onChange={handleUpdateState} type='date' fullWidth value={userInfo.birthDate} required />
          </Grid>
          <Grid md={6} xs={12}>
            <TextField label='Personal email' name='personalEmail' InputLabelProps={{ shrink: true }} onChange={handleUpdateState} fullWidth value={userInfo.personalEmail} required />
          </Grid>
          <Grid md={6} xs={12}>
            <TextField label='Leave days' disabled InputLabelProps={{ shrink: true }} fullWidth value={userInfo.leaveDays} required />
          </Grid>
          <Grid md={6} xs={12}>
            <TextField label='Current salary' disabled InputLabelProps={{ shrink: true }} fullWidth value={userInfo.currentSalary} required />
          </Grid>
          <Grid md={6} xs={12}>
            <TextField label='Managed by' disabled InputLabelProps={{ shrink: true }} fullWidth required />
          </Grid>
        </Grid>
      </CardContent>
      <Divider />
      <CardActions sx={{ justifyContent: 'flex-end' }}>
        <MyLoadingButton  startIcon={<SendIcon/>}text={"Update info"} size="medium"variant="contained" onClick={handleUpdateInfo} />
      </CardActions>
    </Card>
  );
}
