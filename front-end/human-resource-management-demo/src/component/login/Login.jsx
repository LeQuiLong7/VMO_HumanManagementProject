import { Box, Button, Container, Paper, Stack, TextField, Typography } from "@mui/material";
// import axios from "axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import MyLoadingButton from "../general/MyLoadingButton";
import useAxios from "../../hooks/useAxios";
const axios = useAxios();

export default function Login () {
    const navigate = useNavigate();

    useEffect(() => {
        async function loggedInCheck() {
            if(localStorage.getItem('access-token') != null) {
                const response = await axios.get(`/profile`)
                if(response.status == 200) {
                    navigate('/home/profile')
                }
            }
        }
        loggedInCheck()
    }, [])


    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');


    async function handleLogin() {
        const data = {
            email, password
        }
        try {
            const response = await axios.post('http://localhost:8080/login', data);
            localStorage.setItem('access-token',response.data.type + ' '+  response.data.token)
            localStorage.setItem('role',response.data.role)
            navigate('/home/profile')
        } catch(error) {
            throw error
        }
    }

    return(
        <Box sx={{
            width:400,
            margin: "auto"
        }}>
            <Paper elevation={5}  sx={{
                p: 5,
                mt: 10
            }}>
                <Stack direction={"column"} spacing={2}>
                    <Typography variant="h4" gutterBottom textAlign="center">Login</Typography>
                    <TextField label='Email' value={email} onChange={e => setEmail(e.target.value)} required type="text" fullWidth></TextField>
                    <TextField label='Password'   value={password} onChange={e => setPassword(e.target.value)} required type="password" fullWidth></TextField>
                    <Typography variant="body2" component='a' color="primary.main" sx={{
                        '&:hover': {
                            cursor: "pointer",
                            textDecoration: 'underline'
                        }
                    }}
                        onClick={e => navigate("/forgot-password")}
                    >Forgot password?</Typography>

                    <MyLoadingButton text={'Login'} variant="contained" onClick={handleLogin}></MyLoadingButton>
                </Stack>
            </Paper>
        </Box>
    )
}