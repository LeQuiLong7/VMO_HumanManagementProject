import Datatable from "../../general/Datatable";
import { useState, useEffect } from "react";
import {
    Avatar,

    Button,

    Stack,

} from "@mui/material";

import UpdateEmployeeTechStackDialog from "./UpdateEmployeeTechStackDialog";
import EmployeeProjectDialog from "./EmployeeProjectDialog";
import useAxios from "../../../hooks/useAxios";

export default function EmployeeDataTable() {
    const header = "Employee list";
    const axios = useAxios();


    const [data, setData] = useState([]);
    const [selectedEmployeeId, setSelectedEmployeeId] = useState(-1);
    const [viewProjectHistory, setViewProjectHistory] = useState(false)
    const [updateTechState, setUpdateTechState] = useState(false);

    const [tableState, setTableState] = useState({
        total: -1,
        lastPage: 0
    });

    useEffect(() => {
        fetchEmployees(0, 2);
    }, []);

    async function fetchEmployees(pageNumber, pageSize) {
        const response = await axios.get(`/admin/employees?page=${pageNumber}&size=${pageSize}`);

        const combined = response.data.content.map((e) => ({
            ...e,
            name: e.lastName + " " + e.firstName,
        }));
        setData([...data, ...combined]);
        setTableState({
            lastPage: pageNumber > tableState.lastPage ? pageNumber : tableState.lastPage,
            total: response.data.totalElements
        })
    }


    const columns = [
        {
            name: "id",
            label: "ID",
        },
        {
            name: "avatarUrl",
            label: "AVATAR",
            options: {
                customBodyRender: (value, tableMeta) =>
                    <Avatar  src={value + "?" +Math.random().toString(36)} >{tableMeta.rowData[2]}</Avatar>
            },
        },
        {
            name: "name",
            label: "NAME",
        },
        {
            name: "email",
            label: "EMAIL",
        },
        {
            name: "phoneNumber",
            label: "PHONE",
        },
        {
            name: "personalEmail",
            label: "PERSONAL EMAIL",
        },
        {
            name: "role",
            label: "ROLE",
        },
        {
            name: "managedById",
            label: "MANAGE BY",
        },
        {
            name: "",
            label: "ACTION",
            options: {
                customBodyRender: (e, tableMeta) => (
                    <Stack direction={'row'} spacing={1}>
                        <Button onClick={() => handleUpdateTechStack(tableMeta.rowData)} variant="outlined" size="small">Tech</Button>
                        <Button variant="outlined" onClick={() => handleViewProjectHistory(tableMeta.rowData)} size="small">Project</Button>
                    </Stack>
                ),
            },
        },
    ];
    async function handleUpdateTechStack(rowData) {
        setSelectedEmployeeId(rowData[0])
        setUpdateTechState(true);
    }
    async function handleViewProjectHistory(rowData) {
        setSelectedEmployeeId(rowData[0])
        setViewProjectHistory(true);
    }

    async function handlePageChange(page) {
        if (page > tableState.lastPage) {
            fetchEmployees(page, 2)
        }
    }

    const options = {
        selectableRows: "none",
        rowsPerPageOptions: [2],
        rowsPerPage: 2,
        count: tableState.total,
        onChangePage: handlePageChange
    };




    return (
        <>

            <Datatable
                data={data}
                columns={columns}
                setData={setData}
                options={options}
                header={header}
            />

            <UpdateEmployeeTechStackDialog setUpdateTechState={setUpdateTechState} updateTechState={updateTechState} employeeId={selectedEmployeeId}
            />

            {selectedEmployeeId != -1 && <EmployeeProjectDialog open={viewProjectHistory} setOpen={setViewProjectHistory} employeeId={selectedEmployeeId} />}
        </>
    );
}
