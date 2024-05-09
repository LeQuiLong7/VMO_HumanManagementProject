import * as React from 'react';
import { Box, Fade, Stack, TextField } from '@mui/material';
import { TextareaAutosize as BaseTextareaAutosize } from '@mui/base/TextareaAutosize';
import BasicDialog from '../../general/BasicDialog';
import { handleUpdateStateOneObject } from '../../util/Helper';
import useAxios from '../../../hooks/useAxios';

export default function CreateNewProjectDialog({ open, setOpen, setData }) {
    const axios = useAxios();
    const [project, setProject] = React.useState({
        name: '',
        description: '',
        expectedStartDate: '',
        expectedFinishDate: ''
    })

    function handleUpdateState(event) {
        handleUpdateStateOneObject(event, setProject)
    }
    async function handleCreateNewProject() {
        try {
            const response = await axios.post("/admin/project", project);
            setData(pre => [...pre, response.data])
            setTimeout(() => {
                setProject({
                    name: '',
                    description: '',
                    expectedStartDate: '',
                    expectedFinishDate: ''
                })
                setOpen(false)
            }, 2000)
        } catch (error) {
            throw error
        }


    }

    function buildTableContent() {
        return [
            <Stack key={1} spacing={3} sx={{ width: '500px' }}>
                <TextField label='Project name' name='name' onChange={handleUpdateState} required type="text" fullWidth sx={{ mb: 2 }}></TextField>
                <BaseTextareaAutosize placeholder='Description' name='description' onChange={handleUpdateState} minRows={4} sx={{ mb: 2 }}
                    style={{
                        boxSizing: 'border-box',
                        width: '100%',
                        fontFamily: 'IBM Plex Sans, sans-serif',
                        fontSize: '0.875rem',
                        fontWeight: 400,
                        lineHeight: 1.5,
                        padding: '8px 12px',
                        borderRadius: '8px',
                        border: '1px solid #DAE2ED '
                    }}
                ></BaseTextareaAutosize>
                <TextField label='Expected start date' name='expectedStartDate' onChange={handleUpdateState} InputLabelProps={{ shrink: true }} required type="date" fullWidth sx={{ mb: 2 }}></TextField>
                <TextField label='Expected finish date' name='expectedFinishDate' onChange={handleUpdateState} InputLabelProps={{ shrink: true }} required type="date" fullWidth sx={{ mb: 2 }}></TextField>
            </Stack>
        ]
    }
    return (
        <BasicDialog
            title={'Create new project'}
            open={open}
            handleClose={e => setOpen(false)}
            action={'Create'}
            content={buildTableContent()}
            buttonText="Create"
            onButtonClick={handleCreateNewProject} />
    );
}
