import { useEffect, useState } from "react";
import axios from "axios";
import image from "./image.png"
import { useNavigate } from "react-router-dom";
export default function Profile({ employee, setEmployee }) {

    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [birthDate, setBirthDate] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [personalEmail, setPersonalEmail] = useState("");
    const [error, setError] = useState(null);

    useEffect(() => {
        if (employee) {
            setFirstName(employee.firstName || "");
            setLastName(employee.lastName || "");
            setBirthDate(employee.birthDate || "");
            setPhoneNumber(employee.phoneNumber || "");
            setPersonalEmail(employee.personalEmail || "");
            setError(null)
        }
    }, [employee]);


    useEffect(() => {
        getProfile();
    }, [])

    async function handleImageSelect(event) {
        const selectedFile = event.target.files[0];

        if (selectedFile) {
            const formData = new FormData();
            formData.append('file', selectedFile);

            try {

                const response = await axios.put('http://localhost:8080/profile/avatar', formData, {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('access-token')}`,
                        'Content-Type': 'multipart/form-data'
                    }
                });
                getProfile();
            } catch (error) {
                
                
            }

        }
    }


    async function handleUpdateProfile() {

        const data = {
            firstName,
            lastName,
            birthDate,
            phoneNumber,
            personalEmail
        }

        try {

            const response = await axios.put('http://localhost:8080/profile', data, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('access-token')}`
                }
            });
            getProfile();
        } catch (error) {
            console.log(error);
            setError(error.response.data.details);
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
                            <label htmlFor="fileInput">
                                <img className="avt" src={`${employee.avatarUrl || image}?${new Date().getTime()}`} alt="User Avatar" />
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
                                    <h3>Employee id: <input type="text" readOnly disabled value={employee.id} /></h3>
                                </td>
                                <td>
                                    <h3>Role: <input type="text" readOnly disabled value={employee.role} /></h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h3>FirstName: <input type="text" onChange={e => setFirstName(e.target.value)} value={firstName} /></h3>
                                </td>
                                <td>
                                    <h3>LastName: <input type="text" onChange={e => setLastName(e.target.value)} value={lastName} /></h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h3>Birth date: <input type="date" onChange={e => setBirthDate(e.target.value)} value={birthDate} /></h3>
                                </td>
                                <td>
                                    <h3>Personal email: <input type="text" onChange={e => setPersonalEmail(e.target.value)} value={personalEmail} /></h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h3>Phone number: <input type="text" onChange={e => setPhoneNumber(e.target.value)} value={phoneNumber} /></h3>
                                </td>
                                <td>
                                    <h3>Leave days: <input type="text" readOnly disabled value={employee.leaveDays} /></h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h3>Current salary: <input type="text" readOnly disabled value={employee.currentSalary} /></h3>
                                </td>
                                <td>
                                    <h3>Created at: <input type="text" readOnly disabled value={employee.createdAt} /></h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h3>Created by: <input type="text" readOnly disabled value={employee.createdBy} /></h3>
                                </td>
                                <td>
                                    <h3>Managed by: <input type="text" readOnly disabled value={employee.manageBy} /></h3>
                                </td>
                            </tr>
                        </table>
                        {error && <h3 className="red">{error}</h3>}
                        <button className="update-bt" onClick={handleUpdateProfile}>Update</button>
                    </div>
                </div>
            )}

        </>
    );
}