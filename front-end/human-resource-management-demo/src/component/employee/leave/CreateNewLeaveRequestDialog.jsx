import * as React from 'react';
import { Box, Fade, FormControl, InputLabel, MenuItem, Select, Stack, TextField } from '@mui/material';
import BasicDialog from '../../general/BasicDialog';
import { handleUpdateStateOneObject } from '../../util/Helper';
import useAxios from '../../../hooks/useAxios';

export default function CreateNewLeaveRequestDialog({ open, setOpen , setData}) {
    const axios = useAxios();

    const [leaveRequest, setleaveRequest] = React.useState({
        leaveDate: '',
        type: 'UNPAID',
        reason: ''
    })

    async function handleCreateNewLeaveRequest() {
        try {
            const response = await axios.post("/employee/leave", leaveRequest);
            setData(pre => [...pre, response.data])
        } catch(error) {
            throw error
        }

        setTimeout(() => {
            setleaveRequest({
                leaveDate: '',
                type: 'UNPAID',
                reason: ''
            })
            setOpen(false)
        }, 2000)
    }


    function buildDialogContent() {
        return [
            <Stack key={1} spacing={3}>
                <TextField label='Leave date' value={leaveRequest.leaveDate} InputLabelProps={{ shrink: true }} name='leaveDate' onChange={e => handleUpdateStateOneObject(e, setleaveRequest)} required type="date" fullWidth sx={{ mb: 2 }}></TextField>
                <Box width={500}>
                    <FormControl fullWidth sx={{ mb: 2 }}>
                        <InputLabel id="demo-simple-select-label">Type</InputLabel>
                        <Select
                            labelId="demo-simple-select-label"
                            id="demo-simple-select"
                            label="Age"
                            name='type'
                            value={leaveRequest.type}
                            onChange={e => handleUpdateStateOneObject(e, setleaveRequest)}
                            fullWidth
                            required
                        >
                            <MenuItem value={'UNPAID'}>UNPAID</MenuItem>
                            <MenuItem value={'PAID'}>PAID</MenuItem>
                        </Select>
                    </FormControl>
                </Box>
                <TextField label='Reason' name='reason' value={leaveRequest.reason} fullWidth onChange={e => handleUpdateStateOneObject(e, setleaveRequest)} sx={{ mb: 2 }}></TextField>
            </Stack>
        ]
    }

    return (
            <BasicDialog
                title={'Create new leave request'}
                open={open}
                handleClose={e => setOpen(false)}
                action={'Create'}
                content={buildDialogContent()}
                buttonText="Create"
                onButtonClick={handleCreateNewLeaveRequest}
            />
    );
}
