
import { useState, useEffect } from "react";

import CreateNewButton from "../../general/CreateNewButton";
import CreateNewSalaryRequestDialog from "./CreateNewSalaryRaiseRequestDialog";
import SalaryRaiseRequesDataTable from "./SalaryRaiseRequestDataTable";
import useAxios from "../../../hooks/useAxios";
export default function SalaryPage() {

    const axios = useAxios();
    const [data, setData] = useState([])
    const [createState, setCreateState] = useState(false);

    useEffect(() => {
        fetchSalaryRaiseHistory()
    }, [])

    async function fetchSalaryRaiseHistory() {
        const response = await axios.get("/employee/salary?size=100");
        setData(response.data.content)
    }

    return (
        <>
            <CreateNewButton onClickEvent={e => setCreateState(!createState)}/>
            < CreateNewSalaryRequestDialog open={createState} setOpen={setCreateState} setData={setData}/>
            <SalaryRaiseRequesDataTable data={data} />
        </>
    )
}