import { useState, useEffect } from "react";
import { useAuth } from "./context/AuthContext";
import AuthForm from "./components/AuthForm";
import Navbar from "./components/Navbar";
import UrlList from "./components/UrlList";
import CreateUrlModal from "./components/CreateUrlModal";
import { getAllUrls } from "./api/urls";

export default function App() {
  const { token } = useAuth();
  const [urls, setUrls] = useState([]);
  const [showCreate, setShowCreate] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (token) fetchUrls();
  }, [token]);

  const fetchUrls = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await getAllUrls();
      setUrls(data);
    } catch {
      setError("Failed to load URLs. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleCreated = (newUrl) => {
    setUrls((prev) => [newUrl, ...prev]);
  };

  const handleDeleted = (code) => {
    setUrls((prev) => prev.filter((u) => u.code !== code));
  };

  const handleUpdated = (updated) => {
    setUrls((prev) =>
      prev.map((u) => (u.code === updated.code ? { ...u, ...updated } : u))
    );
  };

  if (!token) return <AuthForm />;

  return (
    <div className="app">
      <Navbar onCreateClick={() => setShowCreate(true)} />

      <main className="main-content">
        {loading && <div className="loading-bar" />}
        {error && <p className="page-error">{error}</p>}

        <UrlList
          urls={urls}
          onDeleted={handleDeleted}
          onUpdated={handleUpdated}
        />
      </main>

      {showCreate && (
        <CreateUrlModal
          onClose={() => setShowCreate(false)}
          onCreated={handleCreated}
        />
      )}
    </div>
  );
}
