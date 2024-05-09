import { useState } from "react";
import SendIcon from '@mui/icons-material/Send';
import { useEffect } from "react";
import { Avatar, FormControl, MenuItem, Select, TextField, Typography } from "@mui/material";
import Datatable from "../../general/Datatable";
import MyLoadingButton from "../../general/MyLoadingButton";

import useAxios from "../../../hooks/useAxios";

const action = 'Update'


export default function HandleSalaryRaisePage() {
    const axios = useAxios()
    const [salaryRaiseRequests, setsalaryRaiseRequests] = useState([]);

    useEffect(() => {
        fetchsalaryRaiseRequests()
    }, [])

    async function fetchsalaryRaiseRequests() {
        const response = await axios.get("/admin/salary?size=100");
        setsalaryRaiseRequests(response.data.content)
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
                customBodyRender: (value, tableMeta) => (
                    <Avatar src={value} >{tableMeta.rowData[3]}</Avatar>
                )
            }

        },
        {
            name: "employeeName",
            label: "NAME"
        },
        {
            name: "oldSalary",
            label: "OLD SALARY"
        },
        {
            name: "expectedSalary",
            label: "EXPECTED SALARY"
        },
        {
            name: "newSalary",
            label: "NEW SALARY",
            options: {
                customBodyRender: (value, tableMeta) => (
                    <TextField
                        value={value ? value : ''}
                        size="small"
                        disabled={tableMeta.rowData[7] !== 'PARTIALLY_ACCEPTED'}
                        onChange={e => handleChange(e, tableMeta.rowIndex)}
                        type="number" name="newSalary"></TextField>
                )
            }
        },
        {
            name: 'status',
            label: "STATUS",
            options: {
                customBodyRender: (value, tableMeta) => (
                    <div style={{ width: '220px' }}>
                        <FormControl
                            sx={{
                                m: 1
                            }}
                            size="small"
                            fullWidth
                        >
                            <Select
                                sx={{
                                    color: 'white',
                                    bgcolor: value === 'PROCESSING' ? 'primary.main' : value === 'REJECTED' ? 'error.main' : value === 'FULLY_ACCEPTED' ? 'success.main' : 'success.light'
                                }}
                                label="State"
                                size="small"
                                value={value}
                                name="status"
                                onChange={(e) => handleChange(e, tableMeta.rowIndex, tableMeta)}
                            >
                                <MenuItem value={'PROCESSING'}>PROCESSING</MenuItem>
                                <MenuItem value={'PARTIALLY_ACCEPTED'}>  PARTIALLY_ACCEPTED</MenuItem>
                                <MenuItem value={'FULLY_ACCEPTED'}>  FULLY_ACCEPTED</MenuItem>
                                <MenuItem value={'REJECTED'}>  REJECTED</MenuItem>
                            </Select>
                        </FormControl>
                    </div>
                ),
            },
        },
        {
            name: '',
            label: "ACTION",
            options: {
                customBodyRender: (value, tableMeta) => (
                    <MyLoadingButton startIcon={<SendIcon />}  text={"Update"} size="small"  variant="contained" onClick={() => handleUpdate(tableMeta.rowData, tableMeta.rowIndex)} />
                )
            }
        }
    ];



    const handleChange = (event, rowIndex) => {
        const { name, value } = event.target;
        const newData = [...salaryRaiseRequests];
        newData[rowIndex][name] = value;
        if (name === 'status') {
            if (value === 'FULLY_ACCEPTED') {
                newData[rowIndex]['newSalary'] = [newData[rowIndex]['expectedSalary']]
            }
            if (value === 'REJECTED') {
                newData[rowIndex]['newSalary'] = [newData[rowIndex]['oldSalary']]
            }
        }
        setsalaryRaiseRequests(newData);
    };

    const options = {
        selectableRows: 'none',
        rowsPerPage: 5,
        rowsPerPageOptions: [5, 10, 20]
    };

    async function handleUpdate(rowData, rowIndex) {
        const data = {
            requestId: rowData[0],
            status: rowData[7],
            newSalary: rowData[6][0]
        };

        try {
            const response = await axios.put("/admin/salary", data);
            const newData = [...salaryRaiseRequests]
            newData[rowIndex] = response.data;
            setsalaryRaiseRequests(newData);

        } catch(error) {
            throw error
        }

    }
    return (
        <>
             <Datatable columns={columns} data={salaryRaiseRequests} options={options} />
        </>
    )
}