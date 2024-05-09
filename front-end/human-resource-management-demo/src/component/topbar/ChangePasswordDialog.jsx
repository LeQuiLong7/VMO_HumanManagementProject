import * as React from 'react';
import { Fade, Stack, TextField } from '@mui/material';

import BasicDialog from '../general/BasicDialog';
import { handleUpdateStateOneObject } from '../util/Helper';
import useAxios from '../../hooks/useAxios';


export default function ChangePaswordDialog({ open, setOpen }) {

  const axios = useAxios()

  const [newPassword, setNewPassword] = React.useState({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })

  function handleChange(event) {
    handleUpdateStateOneObject(event, setNewPassword)
  }
  function buildDialogContent() {
    return [
      <Stack key={1} spacing={3} sx={{ width: '500px' }}>
        <TextField label='Current password' onChange={handleChange} name='oldPassword' required type="password" fullWidth sx={{ mb: 2 }}></TextField>
        <TextField label='New password' onChange={handleChange} name='newPassword' required type="password" fullWidth sx={{ mb: 2 }}></TextField>
        <TextField label='Confirm password' onChange={handleChange} name='confirmPassword' required type="password" fullWidth></TextField>
      </Stack>
    ]
  }

  async function  handleChangePassword() {
    try {
      await axios.put('profile/password', newPassword);
      setOpen(false)
      
    } catch (error) {
      throw error
    }
  }
  return (
    <BasicDialog
      title={"Change password"}
      open={open}
      handleClose={e => setOpen(false)}
      content={buildDialogContent()}
      buttonText={'Save'}
      onButtonClick={handleChangePassword}
    />
  );
}