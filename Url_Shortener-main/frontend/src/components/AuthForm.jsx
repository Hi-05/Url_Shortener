import { useState } from "react";
import { login, register } from "../api/auth";
import { useAuth } from "../context/AuthContext";

export default function AuthForm() {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { saveToken } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      if (isLogin) {
        const token = await login(username, password);
        saveToken(token, username);
      } else {
        await register(username, password);
        const token = await login(username, password);
        saveToken(token, username);
      }
    } catch (err) {
      setError(isLogin ? "Invalid credentials" : "Registration failed. Username may already exist.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <div className="logo-mark">⚡</div>
          <h1 className="auth-title">Snip</h1>
          <p className="auth-subtitle">Shorten. Share. Track.</p>
        </div>

        <div className="tab-switcher">
          <button
            className={`tab-btn ${isLogin ? "active" : ""}`}
            onClick={() => { setIsLogin(true); setError(""); }}
          >
            Login
          </button>
          <button
            className={`tab-btn ${!isLogin ? "active" : ""}`}
            onClick={() => { setIsLogin(false); setError(""); }}
          >
            Register
          </button>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="field-group">
            <label className="field-label">Username</label>
            <input
              className="field-input"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="your_username"
              required
            />
          </div>
          <div className="field-group">
            <label className="field-label">Password</label>
            <input
              className="field-input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          {error && <p className="error-msg">{error}</p>}

          <button className="submit-btn" type="submit" disabled={loading}>
            {loading ? "Please wait..." : isLogin ? "Sign In" : "Create Account"}
          </button>
        </form>
      </div>
    </div>
  );
}
