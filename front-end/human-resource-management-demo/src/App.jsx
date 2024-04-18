import { useState } from 'react'
import {Routes, Route} from 'react-router-dom'
import Login from './Login'
import Profile from './Profile'
import ForgotPassword from './ForgotPassword'
import Sidebar from './Sidebar'

function App() {

  const [employee, setEmployee] = useState(null);

  return (
    <Routes>
      <Route path='/login' element={<Login/>}/>
      <Route path='/forgot-password' element={<ForgotPassword/>}/>

      <Route path='/home' element={<Sidebar employee={employee}/>}>
        <Route path='profile' element={<Profile employee={employee} setEmployee={setEmployee} />}/>
      </Route>
      <Route path='*' element={<Login/>}/>

    </Routes>
  )
}

export default App
