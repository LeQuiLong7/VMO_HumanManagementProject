import { Stack, TextField } from "@mui/material"
import BasicDialog from "../../general/BasicDialog"


export default function AssignDialog({open, setOpen, employee, setEmployee, setEmployeesInsideProject}) {
    function buildDialogContent() {
        return [
            <Stack key={1} spacing={3} sx={{ width: '500px' }}>
                <TextField label='Employee name' value={employee.name} name='oldPassword' required disabled fullWidth sx={{ mb: 2 }}></TextField>
                <TextField label='Current effort' value={employee.currentEffort} name='newPassword' disabled fullWidth sx={{ mb: 2 }}></TextField>
                <TextField label='Set effort' value={employee.effort} onChange={e => setEmployee({
                    ...employee,
                    effort: e.target.value
                })} required type="number" fullWidth></TextField>
            </Stack>
        ]
    }

    function handleSelect() {
        setEmployeesInsideProject( pre => [...pre, {
          employeeId: employee.id,
          employeeName: employee.name,
          effort: employee.effort
        }])
        setOpen(false);
      }

    return <BasicDialog
        title={"Assign"}
        open={open}
        normalButton={true}
        handleClose={e => setOpen(false)}
        content={buildDialogContent()}
        buttonText={'Add'}
        onButtonClick={handleSelect}
    />
}