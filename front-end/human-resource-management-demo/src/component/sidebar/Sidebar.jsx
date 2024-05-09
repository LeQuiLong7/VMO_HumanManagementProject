import * as React from 'react';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import AppBar from '@mui/material/AppBar';
import CssBaseline from '@mui/material/CssBaseline';
import Toolbar from '@mui/material/Toolbar';
import List from '@mui/material/List';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import InboxIcon from '@mui/icons-material/MoveToInbox';
import MailIcon from '@mui/icons-material/Mail';
import { Container } from '@mui/material';
import AccountBoxIcon from '@mui/icons-material/AccountBox';
import PeopleOutlineIcon from '@mui/icons-material/PeopleOutline';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import BadgeIcon from '@mui/icons-material/Badge';
import TaskIcon from '@mui/icons-material/Task';
import { useNavigate } from 'react-router-dom';
const drawerWidth = 240;
export default function Sidebar() {
  const navigate = useNavigate();
  return (

    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box' },
      }}
    >
      <Toolbar />
      <Box sx={{ overflow: 'auto' }}>
        <List >
          <ListItem  >
            <ListItemButton onClick={e => navigate("/home/profile")}>
              <ListItemIcon>
                <AccountBoxIcon />
              </ListItemIcon>
              <ListItemText primary={'Profile'} />
            </ListItemButton>
          </ListItem>
          
          {localStorage.getItem('role') === 'ADMIN' && (
            <>
              <ListItem  >
                <ListItemButton onClick={e => navigate("/home/admin/employees")}>
                  <ListItemIcon>
                    <BadgeIcon />
                  </ListItemIcon>
                  <ListItemText primary={'Employees'} />
                </ListItemButton>
              </ListItem>
              <ListItem  >
                <ListItemButton onClick={e => navigate("/home/admin/salary")}>
                  <ListItemIcon>
                    <BadgeIcon />
                  </ListItemIcon>
                  <ListItemText primary={'Salary'} />
                </ListItemButton>
              </ListItem>
              <ListItem  >
                <ListItemButton onClick={e => navigate("/home/admin/projects")}>
                  <ListItemIcon>
                    <TaskIcon />
                  </ListItemIcon>
                  <ListItemText primary={'Projects'} />
                </ListItemButton>
              </ListItem>
            </>
          )}
          {localStorage.getItem('role') === 'EMPLOYEE' && (
            <>
              <ListItem  >
                <ListItemButton onClick={e => navigate("/home/employee/attendance")}>
                  <ListItemIcon>
                    <PeopleOutlineIcon />
                  </ListItemIcon>
                  <ListItemText primary={'Attendance'} />
                </ListItemButton>
              </ListItem>
              <ListItem  >
                <ListItemButton onClick={e => navigate("/home/employee/salary")}>
                  <ListItemIcon>
                    <AttachMoneyIcon />
                  </ListItemIcon>
                  <ListItemText primary={'Salary'} />
                </ListItemButton>
              </ListItem>
              <ListItem  >
                <ListItemButton onClick={e => navigate("/home/employee/leave")}>
                  <ListItemIcon>
                    <ExitToAppIcon />
                  </ListItemIcon>
                  <ListItemText primary={'Leave requext'} />
                </ListItemButton>
              </ListItem>
            </>)}

          {localStorage.getItem('role') === 'PM' && (
            <>
              <ListItem  >
                <ListItemButton onClick={e => navigate("/home/pm/leave")}>
                  <ListItemIcon>
                    <ExitToAppIcon />
                  </ListItemIcon>
                  <ListItemText primary={'Handle leave'} />
                </ListItemButton>
              </ListItem>
              <ListItem  >
                <ListItemButton onClick={e => navigate("/home/pm/attendance")}>
                  <ListItemIcon>
                    <ExitToAppIcon />
                  </ListItemIcon>
                  <ListItemText primary={'Check attendance'} />
                </ListItemButton>
              </ListItem>

            </>)}
        </List>
      </Box>
    </Drawer>
  );
}