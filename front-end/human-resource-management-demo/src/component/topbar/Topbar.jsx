import React from 'react';
import { useState } from 'react';
import AppBar from '@mui/material/AppBar';
import { Divider, ListItemIcon, MenuList, alpha } from '@mui/material';
import Box from '@mui/material/Box';
import LogoutIcon from '@mui/icons-material/Logout';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import PasswordIcon from '@mui/icons-material/Password';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import { InputAdornment, TextField } from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';
import Container from '@mui/material/Container';
import Avatar from '@mui/material/Avatar'
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import AdbIcon from '@mui/icons-material/Adb';
import ChangePaswordDialog from './ChangePasswordDialog';
import { useNavigate } from 'react-router-dom';
import useAxios from '../../hooks/useAxios';


export default function Topbar() {

    const axios = useAxios();

    const [user, setUser] = React.useState({
        avatarUrl: '',
        firstName: '',
        lastName: ''
    })
  
    const [open, setOpen] = React.useState(false);
    const navigate = useNavigate();
  
    React.useEffect(() => {
      fetchProfile();
    }, [])
  
    async function fetchProfile() {
  
      const response = await axios.get("/profile");
      setUser(response.data);
    }


    const [anchorElUser, setAnchorElUser] = React.useState(null);

    const handleOpenUserMenu = (event) => {
        setAnchorElUser(event.currentTarget);
    };


    const handleCloseUserMenu = () => {
        setAnchorElUser(null);
    };

    return (
        <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
            <Toolbar disableGutters sx={{
                mx: 5
            }}>
                <Typography
                    variant="h6"
                    component="a"
                    sx={{
                        mr: 2,
                        display: { xs: 'none', md: 'flex' },
                        fontFamily: 'monospace',
                        fontWeight: 700,
                        letterSpacing: '.3rem',
                        color: 'inherit',
                        textDecoration: 'none',
                    }}
                >
                    LOGO
                </Typography>

                <Container sx={{ display: 'flex', alignItems: 'center', flexGrow: 1, justifyContent: 'flex-end' }}>
                    <TextField
                        id="search"
                        type="search"
                        placeholder='Search...'
                        sx={{
                            width: 300,
                            '& .MuiInputBase-input': {
                                height: '15px',
                            },
                            borderRadius: 2,
                            bgcolor: 'white'
                        }}
                        InputProps={{
                            endAdornment: (
                                <InputAdornment position="end">
                                    <SearchIcon />
                                </InputAdornment>
                            ),
                        }}
                    />
                </Container>


                <Box sx={{ flexGrow: 0 }}>
                    <Tooltip title="Open settings">
                        <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                            <Avatar alt="Remy Sharp" src={`${user.avatarUrl}?${new Date().getTime()}`} />
                        </IconButton>
                    </Tooltip>

                    <Menu
                        sx={{ mt: '45px', pr:5 }}
                        id="menu-appbar"
                        anchorEl={anchorElUser}
                        anchorOrigin={{
                            vertical: 'top',
                            horizontal: 'right',
                        }}
                        keepMounted
                        transformOrigin={{
                            vertical: 'top',
                            horizontal: 'right',
                        }}
                        open={Boolean(anchorElUser)}
                        onClose={handleCloseUserMenu}
                    >
                        <Box sx={{ p: '16px 20px ' }}>
                            <Typography variant="subtitle1">{user.lastName + ' ' + user.firstName}</Typography>
                            <Typography color="text.secondary" variant="body2">
                                {user.email}
                            </Typography>
                        </Box>
                        <Divider />
                        <MenuList disablePadding sx={{ p: '8px', '& .MuiMenuItem-root': { borderRadius: 1 } }}>
                            <MenuItem onClick={e => {
                               navigate("/home/profile");
                               setAnchorElUser(null)
                            }}>
                                <ListItemIcon>
                                    <AccountCircleIcon fontSize="var(--icon-fontSize-md)" />
                                </ListItemIcon>
                                Profile
                            </MenuItem>
                            <MenuItem onClick={e => setOpen(!open)} >
                                <ListItemIcon> 
                                    <PasswordIcon fontSize="var(--icon-fontSize-md)" />
                                </ListItemIcon>
                                Change password
                            </MenuItem>
                            <MenuItem onClick={async (e) => { 
                                const token = localStorage.getItem("access-token")
                                const response = await axios.post("/sign-out", {
                                    token: token.substring(token.indexOf(' ') + 1)
                                });
                                console.log(response.data);
                                localStorage.removeItem('access-token');
                                navigate('/login')
                            }}>
                                <ListItemIcon>
                                    <LogoutIcon fontSize="var(--icon-fontSize-md)" />
                                </ListItemIcon>
                                Sign out
                            </MenuItem>
                        </MenuList>
                    </Menu>
                </Box>
            </Toolbar>
            <ChangePaswordDialog open={open} setOpen={setOpen} />
        </AppBar >
    )
}