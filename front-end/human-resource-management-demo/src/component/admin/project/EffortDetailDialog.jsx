import { Box, Card, CardHeader, Divider, Typography } from "@mui/material";
import useAxios from "../../../hooks/useAxios";
import BasicDialog from "../../general/BasicDialog"
import { useState, useEffect } from "react";
import Chart from "../../test/Chart";

const axios = useAxios();
export default function EffortDetailDialog({ open, setOpen, employeeId }) {

    const [thisMonthEffort, setThisMonthEffort] = useState([])
    const [thisYearEffort, setThisYearEffort] = useState([])

    useEffect(() => {
        fetchThisMonthEffortHistory();
        fetchThisYearEffortHistory();
    }, [employeeId])

    async function fetchThisMonthEffortHistory() {
        const response = await axios.get(`/admin/employee/${employeeId}/effort`);
        setThisMonthEffort(response.data);
    }
    async function fetchThisYearEffortHistory() {
        const response = await axios.get(`/admin/employee/${employeeId}/effort?year=true`);
        setThisYearEffort(response.data);
    }

    const content = [
        <Card  sx={{width: '50vw'}}>
            <Box>
                <Box>
                    <Typography textAlign='center' variant='h6' gutterBottom>This month effort</Typography>
                    <Chart xAxisData={thisMonthEffort.map(e => new Date(e.date))} yAxisData={thisMonthEffort.map(e => e.effort)} fullDate={true} />
                </Box>
                <Divider />
                <Box>
                    <Typography textAlign='center' variant='h6' marginTop={5}>This year effort</Typography>
                    <Chart xAxisData={thisYearEffort.map(e => new Date(e.date))} yAxisData={thisYearEffort.map(e => e.effort)} fullDate={false} />
                </Box>
            </Box>
        </Card>
    ]


    return (
        <BasicDialog
            title={'Effort history'}
            open={open}
            handleClose={e => setOpen(false)}
            noAction={true}
            content={content}
        />
    )
}