import { useEffect } from "react";
import Datatable from "../../general/Datatable";
import StateText from "../../general/StateText";
export default function AttendanceDataTable({data}) {
    const header = "Attendance history"
    const columns = [
        {
            name: 'id',
            label: 'ID'
        },
        {
            name: 'employee',
            label: 'EMPLOYEE ID'
        },
        {
            name: 'date',
            label: 'DATE'

        },
        {
            name: 'timeIn',
            label: 'TIME IN'
        },
        {
            name: 'timeOut',
            label: 'TIME OUT'
        },
        {
            name: 'status',
            label: "STATUS",
            options: {
                customBodyRender: (value) => {
                    return (
                        <StateText text={value} variant={'body1'} bgcolor={value==='On time' || value.endsWith('leave')? 'success.main' : 'error.main'}/>
                    );
                }
            }
        }
    ];
    
    const options = {
        selectableRows: 'none',
        rowsPerPage: 5,
        rowsPerPageOptions: [5, 10, 20]
    };
    return (
        <>
            {data && <Datatable data={data} columns={columns} options={options} header={header} />}
        </>
    )
}