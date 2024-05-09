import * as React from 'react';
import { Stack, TextField } from '@mui/material';
import { handleUpdateStateOneObject } from '../../util/Helper';
import BasicDialog from '../../general/BasicDialog';

import { TextareaAutosize as BaseTextareaAutosize } from '@mui/base/TextareaAutosize';
import useAxios from '../../../hooks/useAxios';



export default function CreateNewSalaryRaiseRequestDialog({ open, setOpen, setData }) {
    const axios = useAxios();
    const [salaryRaiseRequests, setsalaryRaiseRequests] = React.useState({
        expectedSalary: '',
        description: ''
    })

    async function handleCreateNewLeaveRequest() {
        try {
            const response = await axios.post("/employee/salary", salaryRaiseRequests);
            setData(pre => [...pre, response.data])
        } catch (error) {
            throw error
        }

        setTimeout(() => {
            setsalaryRaiseRequests({
                expectedSalary: '',
                description: ''
            })
            setOpen(false)
        }, 2000)
    }


    function buildDialogContent() {
        return [
            <Stack key={1} spacing={3} sx={{width: '500px'}}>
                <TextField label='Expected salary' name='expectedSalary'  value={salaryRaiseRequests.expectedSalary} onChange={e => handleUpdateStateOneObject(e, setsalaryRaiseRequests)} required type="number" fullWidth sx={{ mb: 2 }}></TextField>
                <BaseTextareaAutosize placeholder='Description' name='description' onChange={e => handleUpdateStateOneObject(e, setsalaryRaiseRequests)} minRows={4} sx={{ mb: 2 }}
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
            </Stack>
        ]
    }

    return (
        <BasicDialog
            title={'Create new salary raise request'}
            open={open}
            handleClose={e => setOpen(false)}
            action={'Create'}
            content={buildDialogContent()}
            buttonText="Create"
            onButtonClick={handleCreateNewLeaveRequest}
        />
    );
}
