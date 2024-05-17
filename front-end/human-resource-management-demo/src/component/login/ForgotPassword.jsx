import { Box, Button, Container, Paper, Stack, TextField, Typography } from "@mui/material";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { handleUpdateStateOneObject } from "../util/Helper";
import MyLoadingButton from "../general/MyLoadingButton";
import useAxios from "../../hooks/useAxios";
export default function ForgotPassword() {
    const axios = useAxios();
    const [state, setState] = useState(0);
    const [email, setEmail] = useState('');
    const [resetPasswordRequest, setResetPasswordRequest] = useState({
        token: '',
        newPassword: '',
        confirmPassword: ''
    })

    const navigate = useNavigate()

    async function handleSendToken() {
        const data = {
            email
        }
        try {
            const response = await axios.post('/reset-password', data);
            setState(1)
            return response.data;
        } catch (error) {
            throw error
        } 
    }
    async function handleResetPassword() {
        const data = resetPasswordRequest
        try {
            const response = await axios.put('/reset-password', data);
            console.log(response);
            setState(2)
        } catch (error) {
            throw error
        } 
    }


    return (
        <Box sx={{
            width: 400,
            margin: "auto"
        }}>
            <Paper elevation={5} sx={{
                p: 5,
                mt: 10
            }}>
                <Stack direction={"column"} spacing={3}>
                    <Typography variant="h4" my={20} textAlign="center">Forgot password</Typography>
                    {state == 0 &&
                        <TextField label='Email' value={email} onChange={e => setEmail(e.target.value)} required type="text" fullWidth />}
                    {state == 1 &&
                        <>
                            <TextField label='Token' name="token" onChange={e => handleUpdateStateOneObject(e, setResetPasswordRequest)} required type="text" fullWidth></TextField>
                            <TextField label='New password' name="newPassword" onChange={e => handleUpdateStateOneObject(e, setResetPasswordRequest)} required type="password" fullWidth></TextField>
                            <TextField label='Confirm password' name="confirmPassword" onChange={e => handleUpdateStateOneObject(e, setResetPasswordRequest)} required type="password" fullWidth></TextField>
                        </>
                    }
                    {state == 0 &&
                        <>
                            <MyLoadingButton text={'Send token'} variant="contained" customMessage={true} messagePath={'message'} onClick={handleSendToken}></MyLoadingButton>
                            <Typography variant="body2" component='a' color="primary.main" sx={{
                                '&:hover': {
                                    cursor: "pointer",
                                    textDecoration: 'underline'
                                }
                            }}
                                onClick={e => setState(1)}
                            >Already have a token?</Typography>
                        </>}
                    {state == 1 && <MyLoadingButton text='Reset password'  variant="contained" onClick={handleResetPassword}></MyLoadingButton>}
                    {state == 2 && <Button variant="contained" onClick={e => navigate('/login')}>Back to login</Button>}
                </Stack>
            </Paper>
        </Box>
    )
}