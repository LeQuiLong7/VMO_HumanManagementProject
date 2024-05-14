import Datatable from "../../general/Datatable";
import { Alert, Avatar, Button, Slide, Stack, TextField, Typography } from "@mui/material";


import StateText from "../../general/StateText";
export default function LeaveRequestDataTable({data}) {

    const header = "Leave request history"

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
            label: "TYPE",
            options: {
                customBodyRender: (value) => (
                    <StateText text={value} variant={'body1'} bgcolor={value==='PAID'? 'success.main' : 'error.main'}/>
                )
            }
        },
        {
            name: 'status',
            label: "STATUS",
            options: {
                customBodyRender: (value) => (
                    <StateText text={value} variant={'body1'} 
                    bgcolor={ value === 'PROCESSING' ? 'primary.main' : value === 'REJECTED' ? 'error.main' :'success.main'}
                    />
                ),
            },
        }
    ];
    
    const options = {
        selectableRows: 'none',
        rowsPerPage: 5,
        rowsPerPageOptions: [5, 10, 20]
    };
    return (
        <Datatable data={data} columns={columns}  options={options} header={header} />
    )
}