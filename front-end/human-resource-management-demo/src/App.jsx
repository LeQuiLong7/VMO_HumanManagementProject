
// import Login from './component/Login'
import Login from './component/login/Login'
import './App.css'
import ForgotPassword from './component/login/ForgotPassword'
import Topbar from './component/topbar/Topbar'
import Sidebar from './component/sidebar/Sidebar'
import { createContext, useState } from 'react'
import Layout from './component/layout/Layout'
import { Routes } from 'react-router-dom'
import {  Route } from 'react-router-dom'
import Profile from './component/profile/Profile'
import EmployeePage from './component/admin/employees/EmployeePage'
import ProjectPage from './component/admin/project/ProjectPage'
import AttendancePage from './component/employee/attendance/AttendancePage'
import PMAttendancePage from './component/pm/AttendancePage'
import LeavePage from './component/employee/leave/LeavePage'
import SalaryPage from './component/employee/salary/SalaryPage'
import HandleLeavePage from './component/pm/HandleLeavePage'
import HandleSalaryRaisePage from './component/admin/salary/HanleSalaryRaisePage'
import AlertBox from './component/general/AlertBox'
import { AlertContext } from './context/AlertContext'

function App() {

  const [displayAlert, setDisplayAlert] = useState(false)
  const [responseData, setResponseData] = useState({
      success: true,
      message: ''
  });


  return (
    <>  
      <AlertContext.Provider value={{displayAlert, setDisplayAlert, responseData, setResponseData}}>
            <AlertBox />
          <Routes>
            <Route path='/login'  element={<Login />} />
            <Route path='/'  element={<Login />} />
            <Route path="/forgot-password" element={<ForgotPassword  displayAlert={displayAlert} setDisplayAlert={setDisplayAlert} responseData={responseData} setResponseData={setResponseData} />} />
            <Route path="/home" element={<Layout/>} >
              <Route path="profile" element={<Profile/>} />
              <Route path="admin" >
                <Route path="employees" element={<EmployeePage/>} />
                <Route path="projects" element={<ProjectPage/>} />
                <Route path="salary" element={<HandleSalaryRaisePage/>} />
              </Route>
              <Route path="employee" >
                <Route path="attendance" element={<AttendancePage/>} />
                <Route path="leave" element={<LeavePage/>} />
                <Route path="salary" element={<SalaryPage/>} />
              </Route>
              <Route path="pm" >
                <Route path="attendance" element={<PMAttendancePage/>} />
                <Route path="leave" element={<HandleLeavePage/>} />
              </Route>
            </Route>
            <Route path="/*" element={<Login/>} />
          </Routes>
          {/* <useAxios/> */}
      </AlertContext.Provider>
    </>
  )
}

export default App
