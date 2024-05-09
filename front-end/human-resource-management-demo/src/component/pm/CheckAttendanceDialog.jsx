import * as React from 'react';
import { Avatar, Box, Fade, Stack, TextField } from '@mui/material';

import BasicTable from '../general/BasicTable';
import BasicDialog from '../general/BasicDialog';
import Send from '@mui/icons-material/Send';
import { useState } from 'react';
import { handleChange } from '../util/Helper';
import useAxios from '../../hooks/useAxios';

export default function CheckAttendanceDialog({open, setOpen}) {
    const axios = useAxios();
    const [allCheck, setAllCheck] = useState(false);
    const [employees, setEmployees] = React.useState(null);

    React.useEffect(() => {
        fetchEmployees()
    }, [])

    async function fetchEmployees() {
        const response = await axios.get("/pm/employees");
        const mapped = response.data.content.map(employee => ({
            employeeId: employee.id,
            name: employee.lastName + ' ' + employee.firstName,
            avatarUrl: employee.avatarUrl,
            timeIn: '',
            timeOut: ''
        }
        ))
        setEmployees(mapped)
    }


    function mapToDataTable() {
        return employees.map((employee, index) => {
            return {
                id: employee.employeeId,
                name: employee.name,
                avatar: <Avatar src={employee.avatarUrl} />,
                timeIn: (
                    <TextField
                        name='timeIn'
                        type="time"
                        size="small"
                        onChange={e => handleTimeChange(e, index)}
                    />
                ),
                timeOut: (
                    <TextField
                        name='timeOut'
                        type="time"
                        size="small"
                        onChange={e => handleTimeChange(e, index)}
                    />
                )
            };
        });

    }

    function handleTimeChange(event, index) {
        handleChange(event, index, employees, setEmployees);
        setAllCheck(employees.every(e => e.timeIn != '' && e.timeOut != ''));
    };


    async function handleSendAttendance() {
        const data = {
            attendanceDetails: employees.map(e => ({
                employeeId: e.employeeId,
                timeIn: e.timeIn,
                timeOut: e.timeOut
            }))
        }
        try {
            const response = await axios.post("/pm/attendance", data);
        } catch (error) {
            throw error
        }
        setTimeout(() => {
            setOpen(false)
            window.location.reload()
        }, 2000)
    }

    const columns = [
        {
            name: "id",
            label: "EMPLOYEE ID"
        },
        {
            name: "name",
            label: "NAME"

        },
        {
            name: "avatar",
            label: "AVATAR",

        },
        {
            name: 'timeIn',
            label: "TIME IN",
        },
        {
            name: 'timeOut',
            label: "TIME OUT",

        }
    ];


    return (
        <>
            {employees && (
                <BasicDialog open={open} handleClose={e => setOpen(false)}
                    content={[<BasicTable key={1} columns={columns} data={mapToDataTable()} />]}
                    action={'Check attendance'}
                    disabledButton={!allCheck}
                    buttonIcon={<Send />}
                    buttonText={'Send'}
                    onButtonClick={handleSendAttendance}
                />

            )}
        </>

    )
}