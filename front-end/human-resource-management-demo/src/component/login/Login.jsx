import { Avatar, Box, Button, Container, IconButton, Paper, Stack, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import MyLoadingButton from "../general/MyLoadingButton";
import useAxios from "../../hooks/useAxios";
import { useAlertContext } from "../../context/AlertContext";
import gg from '../../../src/google.jpg'
const axios = useAxios();

export default function Login() {
    const navigate = useNavigate();
    const { displayAlert, setDisplayAlert, responseData, setResponseData } = useAlertContext();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    useEffect(() => {
        if (window.location.search != ''){
            const params = new URLSearchParams(window.location.search);
            const errorParam = params.get('error');
            if (errorParam !== null) {
                if (displayAlert)
                    setDisplayAlert(false)
                setResponseData({
                    success: false,
                    message: errorParam
                })
                setDisplayAlert(true)
                return;
            } else {
                localStorage.setItem('access-token', params.get('type') + ' ' + params.get('token'))
                localStorage.setItem('role', params.get('role'))
                navigate('/home/profile')
                return;
            }
        }
        async function loggedInCheck() {
            if (localStorage.getItem('access-token') != null) {
                const response = await axios.get(`/profile`, {
                    headers: {
                        Authorization: localStorage.getItem('access-token')
                    }
                })
                if (response.status == 200) {
                    navigate('/home/profile')
                }
            }
        }

        loggedInCheck()
    }, [])

    async function handleLogin() {
        const data = {
            email, password
        }
        try {
            const response = await axios.post('/login', data);
            localStorage.setItem('access-token', response.data.type + ' ' + response.data.token)
            localStorage.setItem('role', response.data.role)
            navigate('/home/profile')
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
                <Stack direction={"column"} spacing={2}>
                    <Typography variant="h4" gutterBottom textAlign="center">Login</Typography>
                    <TextField label='Email' value={email} onChange={e => setEmail(e.target.value)} required type="text" fullWidth></TextField>
                    <TextField label='Password' value={password} onChange={e => setPassword(e.target.value)} required type="password" fullWidth></TextField>
                    <Typography variant="body2" component='a' color="primary.main" sx={{
                        '&:hover': {
                            cursor: "pointer",
                            textDecoration: 'underline'
                        }
                    }}
                        onClick={e => navigate("/forgot-password")}
                    >Forgot password?</Typography>

                    <MyLoadingButton text={'Login'} variant="contained" onClick={handleLogin}></MyLoadingButton>
                    <Stack alignItems={'center'}>
                    <IconButton size="small" sx={{width: '50px'}}  component={Link} to='http://localhost:8080/oauth2/authorization/google'>
                        <Avatar src={gg} sizes="small"></Avatar>
                    </IconButton>
                    </Stack>
                </Stack>
            </Paper>
        </Box>
    )
}