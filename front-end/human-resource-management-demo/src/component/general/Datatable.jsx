import MUIDataTable from "mui-datatables";
import { Avatar, Typography } from "@mui/material";


export default function Datatable({data, columns, options, header}) {

    return (
        <>
            {/* <Typography variant="h4" gutterBottom>{header}</Typography> */}
            <MUIDataTable 

                title={header}
                data={data}
                columns={columns}
                options={options}
             />
        </>
    )
}