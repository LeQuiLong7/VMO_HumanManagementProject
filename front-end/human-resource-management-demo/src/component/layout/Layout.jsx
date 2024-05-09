import { Box, Typography, Toolbar } from "@mui/material";
import Topbar from "../topbar/Topbar";
import Sidebar from "../sidebar/Sidebar";
import { useState, useEffect } from "react";
import { Outlet } from "react-router-dom";

export default function Layout() {

    return(
        <Box sx={{ display: 'flex' }}>
            <Topbar/>
            <Sidebar/>
            <Box component="main" sx={{ flexGrow: 1, p: 2 }}>
                <Toolbar />
                <Outlet/>
            </Box>
        </Box>

    )
}