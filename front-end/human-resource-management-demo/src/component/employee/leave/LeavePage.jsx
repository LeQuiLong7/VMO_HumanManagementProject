
import { useState, useEffect } from "react";

import CreateNewButton from "../../general/CreateNewButton";
import CreateNewLeaveRequestDialog from "./CreateNewLeaveRequestDialog";
import LeaveRequestDataTable from "./LeaveRequestDataTable";
import useAxios from "../../../hooks/useAxios";
export default function LeavePage() {
    const axios = useAxios();
    const [createState, setCreateState] = useState(false);
    const [data, setData] = useState([])

    useEffect(() => {
        fetchLeave()
    }, [])

    async function fetchLeave() {
        const response = await axios.get("/profile/leave?size=100");
        setData(response.data.content)
    }



    return (
        <>
            <CreateNewButton onClickEvent={e => setCreateState(!createState)} />
            <CreateNewLeaveRequestDialog open={createState} setOpen={setCreateState} setData={setData} />
            <LeaveRequestDataTable data={data} />
        </>
    )
}