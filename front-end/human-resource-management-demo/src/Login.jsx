import React, { useState } from 'react';
import './login.css';

function Login () {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loggedIn, setLoggedIn] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();

    console.log(`Logged in as: ${username}`);
    setLoggedIn(true);
  };

  return (
    <div className="login-container">
      {!loggedIn ? (
        <form onSubmit={handleSubmit} className="login-form">
          <h2>Login</h2>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button type="submit">Login</button>
        </form>
      ) : (
        <div className="logged-in-message">
          <h2>Welcome, {username}!</h2>
          <p>You are now logged in.</p>
        </div>
      )}
    </div>
  );
};

export default Login;
