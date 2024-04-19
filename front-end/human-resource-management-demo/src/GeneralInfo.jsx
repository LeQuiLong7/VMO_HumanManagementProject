import axios from "axios";
import image from "./image.png"

export default function GeneralInfo({employee, getProfile}) {

    
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
                console.log(error);  
            }

        }
    }
    return (
        <div className="general-info">
            <div className="left">
                {handleImageSelect && <input type="file" id="fileInput" className="none" accept="image/*" onChange={handleImageSelect} />}
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
    )
}