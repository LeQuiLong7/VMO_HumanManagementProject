import React, { useState } from 'react';
import axios from 'axios';
import './style.css';
import { useNavigate } from 'react-router-dom';

function ForgotPassword() {
    const [email, setEmail] = useState('');
    const [token, setToken] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [fail, setFail] = useState(false);
    const [resetState, setResetState] = useState(1);
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate()

    async function handleSubmit(e) {

        e.preventDefault();
        var response = null;

        try {
            if (resetState == 1) {
                response = await axios.post('http://localhost:8080/reset-password', {
                    email
                });
            } else  {
                response = await axios.put('http://localhost:8080/reset-password', {
                    token, newPassword, confirmPassword
                });
            }
            setResetState(old => old + 1)

            setSuccess(response.data.message)
            setFail(null)
        } catch (error) {
            setSuccess(null)
            setFail(error.response.data.error);
        }

    };



    return (

        <div className="login-container">
            <form onSubmit={handleSubmit} className="login-form" autoComplete='off'>
                <h2>Forgot password</h2>
                <input
                    type="text"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                {resetState == 2 && (
                    <>
                        <input
                            type="text"
                            placeholder="Token"
                            value={token}
                            onChange={(e) => setToken(e.target.value)}
                        />
                        <input
                            type="password"
                            placeholder="New Password"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                        />
                        <input
                            type="password"
                            placeholder="Confim password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                        />
                    </>
                )}

                {fail && <h4 className='red'>{fail}</h4>}
                {success && <h4 className='blue'>{success}</h4>}
                {resetState == 3 ? (
                    <button onClick={() => navigate("/login")}>Back to login</button>
                ) :
                (
                    <button type="submit">{resetState == 2 ? 'Reset password' : 'Send token'}</button>
                )
                }
            </form>
        </div>
    );
};

export default ForgotPassword;
