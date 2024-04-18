import React, { useState } from 'react';
import axios from 'axios';
import './style.css';
import { useNavigate } from 'react-router-dom';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loggedIn, setLoggedIn] = useState(false);
  const [fail, setFail] = useState(false);
  const [failMessage, setFailMessage] = useState("");
  const navigate = useNavigate()

  async function handleSubmit(e) {

    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/login', {
        email,
        password
      });


      localStorage.setItem('access-token', response.data.token)

      navigate("/home/profile");

    } catch (error) {
      setFail(true);
      setFailMessage(error.response.data.error);
    }
  };

  return (

    <div className="login-container">
      <form onSubmit={handleSubmit} className="login-form">
        <h2>Login</h2>
        <input
          type="text"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        {fail && <h4 className='red'>{failMessage}</h4>}
        <button type="submit">Login</button>
      </form>
    </div>
  );
};

export default Login;
