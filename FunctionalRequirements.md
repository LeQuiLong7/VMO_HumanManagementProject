# Functional Requirements

## Employees Account

### New Account

1. The application allows admin to create new accounts for employees
   1. The account should base on employee’s name (Le Qui Long -> <longlq@company.com>)

### Reset Password

1. The application should allow employees to reset their password
2. The application should send a link to employee’s email to reset their password
3. If a password is reset successfully, use should be able to login using the new password

### Log In

1. Normal employees are only able to view, change their basic info, view projects info
2. The application should allow employees to login using their account
3. After 5 failed login attempts, the account should be locked for 15 minutes

### Profile

1. The application should allow employees to update the basic info while logged in
2. The application should allow users to update their password while logged 

## Projects Management

### Project List

1. Admin can view list of all projects
2. Employees can view list of projects they were in, are in, are assigned to join in the future

### Project Details

1. Employees can view restricted information about projects
2. Admin can view all details about projects

### New Project

1. The application allows admin to create new projects 
2. The application allows admin to assign employees to projects



## Access Control

### Role Control

1. Two roles: EMPLOYEE and ADMIN
2. EMPLOYEE role have restricted access on resources
3. ADMIN role have full access on resources 
4. Only ADMIN can assign EMPLOYEE to projects

## Audit Trail

1. The application should keep track of who created an entity(account, projects)
2. The application should keep track of when an entity(account, projects) was created.
3. The application should keep track of who updated an entity(account, projects).
4. The application should keep track of when an entity(account, projects) was updated.





