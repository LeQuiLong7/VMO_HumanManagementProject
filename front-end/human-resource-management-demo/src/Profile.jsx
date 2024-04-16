import { useEffect, useState } from "react";
import axios from "axios";
import image from "./image.png"
export default function Profile() {
    const [employee, setEmployee] = useState(null);

    useEffect(() => {
        getProfile();
    }, [])
    async function handleImageSelect(event) {
        const selectedFile = event.target.files[0];

        if (selectedFile) {
            const formData = new FormData();
            formData.append('file', selectedFile);


            const response = await axios.put('http://localhost:8080/profile/avatar', formData, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('access-token')}`,
                    'Content-Type': 'multipart/form-data'
                }
            });

            getProfile();
        }




    }

    async function getProfile() {
        try {
            const response = await axios.get('http://localhost:8080/profile', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            setEmployee(response.data)
            console.log(response.data);
        } catch (error) {
            console.log(error);
        }
    }
    return (
        <>
            <h1 className="padding-10">Personal Information</h1>
            {employee && (
                <div className="employee-container">
                    <h2>Gerenal infomation</h2>
                    <div className="general-info">
                        <div className="left">
                            <input type="file" id="fileInput" className="none" accept="image/*" onChange={handleImageSelect} />
                            <label for="fileInput">
                                <img className="avt" src={employee.avatarUrl == null ? image : employee.avatarUrl} />
                            </label>

                            <div>
                                <h3>{employee.firstName + " " + employee.lastName}</h3>
                                <h4>Employee id: {employee.id}</h4>
                                <h4>Email: {employee.email}</h4>
                                <h4>Role: {employee.role}</h4>
                            </div>
                        </div>
                    </div>
                    <h2>Detail infomation</h2>

                    <div className="detail-info">
                        <table className="right">
                            <tr>
                                <td>
                                    <h3>Employee id: <input type="text" disabled value={employee.id} /></h3>
                                </td>
                                <td>
                                    <h3>Role: <input type="text" disabled value={employee.role} /></h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h3>FirstName: <input type="text" value={employee.firstName} /></h3>
                                </td>
                                <td>
                                    <h3>LastName: <input type="text" value={employee.lastName} /></h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h3>Birth date: <input type="text" value={employee.birthDate} /></h3>
                                </td>
                                <td>
                                    <h3>Phone number: <input type="text" value={employee.phoneNumber} /></h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h3>Personal email: <input type="text" value={employee.personalEmail} /></h3>
                                </td>
                                <td>
                                    <h3>Leave days: <input type="text" disabled value={employee.leaveDays} /></h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h3>Current salary: <input type="text" disabled value={employee.currentSalary} /></h3>
                                </td>
                                <td>
                                    <h3>Created at: <input type="text" disabled value={employee.createdAt} /></h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h3>Created by: <input type="text" disabled value={employee.createdBy} /></h3>
                                </td>
                                <td>
                                    <h3>Managed by: <input type="text" disabled value={employee.manageBy} /></h3>
                                </td>
                            </tr>
                        </table>
                        <button className="update-bt">Update</button>
                    </div>
                </div>
            )}

        </>
    );
}