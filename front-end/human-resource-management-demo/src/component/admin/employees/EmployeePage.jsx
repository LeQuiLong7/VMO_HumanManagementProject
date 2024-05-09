
import { useState } from "react";
import CreateNewButton from "../../general/CreateNewButton";
import CreateNewEmployeeDialog from "./CreateNewEmployeeDialog";
import EmployeeDataTable from "./EmployeeDatatable";

export default function EmployeePage() {
  const[createState, setCreateState] = useState(false)

  return (
    <>
      <CreateNewButton onClickEvent={(e) => setCreateState(true)} />
      <EmployeeDataTable/>
      <CreateNewEmployeeDialog createState={createState} setCreateState={setCreateState} />
    </>
  );
}
