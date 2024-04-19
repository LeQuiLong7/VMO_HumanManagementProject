export default function LeftSideBar() {
    return (
        <div className="sidebar">
            <div className="left-sidebar">
                <div className="logo">HR Management</div>
                <nav className="nav-menu">
                    <ul>
                        <li><a href="/">Dashboard</a></li>
                        <li><a href="/home/profile">Profile</a></li>
                        <li><a href="/home/leave">Leaves</a></li>
                        <li><a href="/home/salary">Salary</a></li>
                        <li><a href="/home/employees">Employees</a></li>
                        <li><a href="/departments">Departments</a></li>
                        <li><a href="/projects">Projects</a></li>
                        <li><a href="/reports">Reports</a></li>
                    </ul>
                </nav>
            </div>
        </div>
    )
}