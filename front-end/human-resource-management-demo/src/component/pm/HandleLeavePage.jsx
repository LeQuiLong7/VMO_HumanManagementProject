import { useState } from "react";
import SendIcon from '@mui/icons-material/Send';
import { useEffect } from "react";
import { Avatar, FormControl, MenuItem, Select, TextField, Typography } from "@mui/material";
import Datatable from "../general/Datatable";
import MyLoadingButton from "../general/MyLoadingButton";
import { handleChange } from "../util/Helper";
import useAxios from "../../hooks/useAxios";

export default function HandleLeavePage() {

    const axios = useAxios();
    const[leaveRequests, setLeaveRequests] = useState([]);

    useEffect(() => {
        fetchLeaveRequests()
    }, [])

    async function fetchLeaveRequests() {
        const response = await axios.get("/pm/leave");
        setLeaveRequests(response.data.content)
    }

    const columns = [
        {
            name: "id",
            label: "ID"
        },
        {
            name: "employeeId",
            label: "EMPLOYEE ID"
        },
        {
            name: "avatarUrl",
            label: "AVATAR",
            options: {
                customBodyRender: (value) => (
                    <Avatar src={value} />
                )
            }
        }, 
        {
            name: "employeeName",
            label: "NAME"
        },
        {
            name: "date",
            label: "LEAVE DATE", 
            options: {
                customBodyRender: (value) => (
                    <TextField type="date" value={value} disabled />
                )
            }
        }, 
        {
            name: 'type',
            label: "TYPE"
        },
        {
            name: 'status',
            label: "STATUS",
            options: {
                customBodyRender: (value, tableMeta) => (
                    <FormControl
                        sx={{
                            m: 1,
                            minWidth: 120
                        }}
                        size="small"
                    >

                        <Select
                            label="State"
                            value={value}
                            name="status"
                             bgcolor={ value === 'PROCESSING' ? 'primary.main' : value === 'REJECTED' ? 'error.main' : 'success.main' }

                            sx={{color: 'white', bgcolor: value === 'PROCESSING' ? 'primary.main' : value === 'REJECTED' ? 'error.main' : 'success.main' }}
                            onChange={(e) => handleChange(e, tableMeta.rowIndex, leaveRequests, setLeaveRequests)}
                        >
                            <MenuItem value={'PROCESSING'}>PROCESSING</MenuItem>
                            <MenuItem value={'ACCEPTED'}>ACCEPTED</MenuItem>
                            <MenuItem value={'REJECTED'}>REJECTED</MenuItem>
                        </Select>
                    </FormControl>
                ),
            },
        },
        {
            name: '',
            label: "ACTION",
            options: {
                customBodyRender: (value, tableMeta) => (
                    <MyLoadingButton 
                    startIcon={<SendIcon />}text={"Update"} size="small" variant="contained" onClick={() => handleUpdate(tableMeta.rowData, tableMeta.rowIndex)} />
                )
            }
        }
    ];
    
    const options = {
        selectableRows: 'none',
        rowsPerPage: 5,
        rowsPerPageOptions: [5, 10, 20]
    };
    
    async function handleUpdate(rowData, rowIndex) {
        const data = [{
            requestId: rowData[0],
            status: rowData[6]
        }];
        try {

            const response = await axios.put("/pm/leave", data);
            const newData = [...leaveRequests]
            newData[rowIndex] = response.data[0];
            setLeaveRequests(newData);
        } catch(error) {
            throw error
        }

    }

    return (
        <>
            <Datatable columns={columns} data={leaveRequests} options={options}/>
        </>
    )
}