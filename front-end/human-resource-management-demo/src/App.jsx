import { useState } from 'react'
import {Routes, Route} from 'react-router-dom'
import Login from './Login'
import Profile from './Profile'
import Sidebar from './Sidebar'

function App() {


  return (
    <Routes>
      <Route path='/login' element={<Login/>}/>
      <Route path='/sidebar' element={<Sidebar/>}/>
      <Route path='/home' element={<Sidebar/>}>
        <Route path='profile' element={<Profile/>}/>
        
      </Route>
    </Routes>
  )
}

export default App
