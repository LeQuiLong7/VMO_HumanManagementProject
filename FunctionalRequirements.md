# Functional Requirements

## Employees Account

### New Account

1. The application allows admin to create new accounts for employees
   1. The account should base on employee’s name (Le Qui Long -> <longlq@company.com>)

### Reset Password

1. The application should allow employees to reset their password
2. The application should send a link to employee’s email to reset their password

### Log In

1. The application should allow employees to login using their account

### Profile

1. The application should allow employees to update the basic info while logged in
2. The application should allow users to update their password while logged 

### Attendance and Leave
1. The application should keep track of employees attendance
2. The application should allow employees to make leave request

## Projects Management

### Project List

1. Admin can view list of all projects
2. Employees can view list of projects they were in, are in, are assigned to join in the future

### Project Details

1. Employees can view restricted information about projects
2. Admin can view all details about projects

### New Project

1. The application allows admin to create new projects 
2. The application allows admin, project managers to assign employees to projects


## Access Control

### Role Control

1. 3 roles: EMPLOYEE, PM and ADMIN
2. EMPLOYEE can log in, view basic info, change basic info, make leave requests, make salary raise request
3. PM manages multiple EMPLOYEES: manage their attendance, accept or reject their leave request, accept or reject their salary raise request, assign them to projects  
4. ADMIN can create new projects, assign PM or EMPLOYEES to projects

## Audit Trail

1. The application should keep track of who created an entity(account, projects)
2. The application should keep track of when an entity(account, projects) was created.
3. The application should keep track of who updated an entity(account, projects).
4. The application should keep track of when an entity(account, projects) was updated.





