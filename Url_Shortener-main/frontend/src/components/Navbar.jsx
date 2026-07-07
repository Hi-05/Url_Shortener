import { useAuth } from "../context/AuthContext";

export default function Navbar({ onCreateClick }) {
  const { username, logout } = useAuth();

  return (
    <nav className="navbar">
      <div className="nav-brand">
        <span className="nav-logo">⚡</span>
        <span className="nav-title">Snip</span>
      </div>
      <div className="nav-actions">
        <button className="create-btn" onClick={onCreateClick}>
          + Shorten URL
        </button>
        <div className="nav-user">
          <span className="user-badge">{username?.charAt(0).toUpperCase()}</span>
          <span className="username">{username}</span>
        </div>
        <button className="logout-btn" onClick={logout}>
          Logout
        </button>
      </div>
    </nav>
  );
}
