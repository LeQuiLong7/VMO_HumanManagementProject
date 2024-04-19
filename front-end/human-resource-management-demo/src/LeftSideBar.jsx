export default function LeftSideBar() {
    return (
        <div className="sidebar">
            <div className="left-sidebar">
                <div className="logo">HR Management</div>
                <nav className="nav-menu">
                    <ul>
                        <li><a href="/home/profile">Profile</a></li>
                        <li><a href="/home/leave">Leaves</a></li>
                        <li><a href="/home/salary">Salary</a></li>
                        <li><a href="/home/employees">Employees</a></li>
                        <li><a href="/home/attendance">Attendance</a></li>
                        <li><a href="/home/handle-leave">Handle leave</a></li>
                        <li><a href="/home/admin-employee">Admin employee</a></li>
                        <li><a href="/home/project">Projects</a></li>
                    </ul>
                </nav>
            </div>
        </div>
    )
}