import BasicDialog from "../../general/BasicDialog";
import ProjectHistoryDataTable from "../../profile/ProjectHistoryDataTable";
export default function EmployeeProjectDialog({ open, setOpen, employeeId }) {
    return (
        <>
                <BasicDialog
                    open={open}
                    handleClose={e => setOpen(false)}
                    noAction={true}
                    fullScreen={true}
                    title={'Project history'}
                    content={[<ProjectHistoryDataTable key={1} url={`http://localhost:8080/admin/employee/${employeeId}/projects`}/>]}
                >
                </BasicDialog>
        </>
    )
}