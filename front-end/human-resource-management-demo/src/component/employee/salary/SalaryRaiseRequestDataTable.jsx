import Datatable from "../../general/Datatable";

import { Avatar} from "@mui/material";

import StateText from "../../general/StateText";
export default function SalaryRaiseRequesDataTable({data}) {

    const header = "Salary raise request history"

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
                customBodyRender: (value, tableMeta) => (
                    <Avatar src={value + "?" +Math.random().toString(36)} >{tableMeta.rowData[3]}</Avatar>
                )
            }

        },
        {
            name: "employeeName",
            label: "NAME"
        },
        {
            name: "oldSalary",
            label: "OLD SALARY"
        },
        {
            name: "expectedSalary",
            label: "EXPECTED SALARY"
        },
        {
            name: "newSalary",
            label: "NEW SALARY"
        },
        {
            name: 'status',
            label: "STATUS",
            options: {
                customBodyRender: (value, tableMeta) => (
                    <StateText 
                     text={value}
                     variant={'body1'}
                     bgcolor={ value === 'PROCESSING' ? 'primary.main' : value === 'REJECTED' ? 'error.main' : value === 'FULLY_ACCEPTED' ? 'success.main' : 'success.light'}
                     
                    />
                ),
            },
        }, 
        {
            name: "approvedBy",
            label: "APPROVED BY"
        },
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