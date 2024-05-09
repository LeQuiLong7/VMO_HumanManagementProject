

import * as React from 'react';

import { Avatar, Box, Fade, Grid, Stack, TextField, Typography } from '@mui/material';

import BasicDialog from '../general/BasicDialog';
import Datatable from '../general/Datatable';
import TextArea from '../general/TextArea';


export default function ProjectDetailDialog({ project, open, setOpen }) {

    const columns = [
        {
            name: "employeeId",
            label: "ID",
        },
        {
            name: "avatarUrl",
            label: "AVATAR",
            options: {
                customBodyRender: (value, tableMeta) =>
                    <Avatar src={value} >{tableMeta.rowData[2]}</Avatar>
            },
        },
        {
            name: "employeeName",
            label: "NAME",
        },

        {
            name: "role",
            label: "ROLE",
        },
        {
            name: "assignAt",
            label: "ASSIGN AT",
            options: {
                customBodyRender: (value) => new Date(value).toLocaleString('vi-VN')
            }
        },

        {
            name: "assignBy",
            label: "ASSIGN BY",
        }
    ];

    const options = {
        selectableRows: "none",
    };

    function buildTableContent() {
        return [
            <Stack key={1} spacing={3} >
                <Typography variant='h5'>Project information</Typography>
                <Grid container spacing={2}>
                    <Grid item xs={4}>
                        <TextField label='Project id' name='name' disabled value={project.projectInfo.id} required type="text" fullWidth sx={{ mb: 2 }}></TextField>
                    </Grid>
                    <Grid item xs={4}>
                        <TextField label='Project name' name='name' disabled value={project.projectInfo.name} required type="text" fullWidth sx={{ mb: 2 }}></TextField>
                    </Grid>
                    <Grid item xs={4}>
                        <TextField label='Status' disabled value={project.projectInfo.state} InputLabelProps={{ shrink: true }} required fullWidth sx={{ mb: 2}}></TextField>
                    </Grid>
                    <Grid item xs={4}>
                        <TextField label='Created at' disabled value={new Date(project.projectInfo.createdAt).toLocaleString('vi-VN')} InputLabelProps={{ shrink: true }} required type="datetime" fullWidth sx={{ mb: 2 }}></TextField>
                    </Grid>
                    <Grid item xs={4}>
                    <TextField label='Created by' disabled value={project.projectInfo.createdBy} InputLabelProps={{ shrink: true }} required type="text" fullWidth sx={{ mb: 2 }}></TextField>
                    </Grid>
                    <Grid item xs={12}>
                        <TextArea value={project.projectInfo.description} minRows={4}/>
                    </Grid>
                    <Grid item xs={4}>
                        <TextField label='Expected start date' disabled value={project.projectInfo.expectedStartDate} InputLabelProps={{ shrink: true }} required type="date" fullWidth sx={{ mb: 2 }}></TextField>

                    </Grid>
                    <Grid item xs={4}>
                        <TextField label='Expected finish date' disabled value={project.projectInfo.expectedFinishDate} InputLabelProps={{ shrink: true }} required type="date" fullWidth sx={{ mb: 2 }}></TextField>
                    </Grid>
                    <Grid item xs={4}>
                        <TextField label='Actual start date' disabled value={project.projectInfo.actualStartDate} InputLabelProps={{ shrink: true }} required type="date" fullWidth sx={{ mb: 2 }}></TextField>

                    </Grid>
                    <Grid item xs={4}>
                        <TextField label='Actual finish date' disabled value={project.projectInfo.actualFinishDate} InputLabelProps={{ shrink: true }} required type="date" fullWidth sx={{ mb: 2 }}></TextField>

                    </Grid>
                </Grid>
                <Typography variant='h5'>Employees assign list</Typography>
                <Datatable columns={columns} options={options} data={project.assignHistory} />
            </Stack>
        ]
    }

    return (


        <BasicDialog
            fullScreen={true}
            noAction={true}
            title={'Project detail'}
            open={open}
            handleClose={e => setOpen(false)}
            action={'Create'}
            content={buildTableContent()}
            buttonText="Create"
            onButtonClick={null} />
    );
}