import * as React from 'react';
import {  Box,  } from '@mui/material';
import { useState } from 'react';
import AttendancePage from '../employee/attendance/AttendancePage';
import CreateNewButton from '../general/CreateNewButton';
import CheckAttendanceDialog from './CheckAttendanceDialog';

export default function PMAttendancePage() {
    const [checkAttendanceState, setCheckAttendanceState] = useState(false);


    return (
        <Box>
            <CreateNewButton onClickEvent={e => setCheckAttendanceState(true)} text={'Check attendance'}></CreateNewButton>
            {checkAttendanceState && <CheckAttendanceDialog open={checkAttendanceState} setOpen={setCheckAttendanceState} />}
            <AttendancePage/>
        </Box>
    )
}