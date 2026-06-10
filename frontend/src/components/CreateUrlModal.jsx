import { useState } from "react";
import { createUrl } from "../api/urls";

export default function CreateUrlModal({ onClose, onCreated }) {
  const [longUrl, setLongUrl] = useState("");
  const [description, setDescription] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const created = await createUrl(longUrl, description);
      onCreated(created);
      onClose();
    } catch (err) {
      setError(err.response?.data?.longUrl || err.response?.data?.error || "Failed to create URL");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">Shorten a URL</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit} className="modal-form">
          <div className="field-group">
            <label className="field-label">Long URL</label>
            <input
              className="field-input"
              type="url"
              value={longUrl}
              onChange={(e) => setLongUrl(e.target.value)}
              placeholder="https://example.com/very/long/url"
              required
            />
          </div>
          <div className="field-group">
            <label className="field-label">Description <span className="optional">(optional)</span></label>
            <input
              className="field-input"
              type="text"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="What is this link?"
            />
          </div>
          {error && <p className="error-msg">{error}</p>}
          <div className="modal-actions">
            <button type="button" className="cancel-btn" onClick={onClose}>Cancel</button>
            <button type="submit" className="submit-btn" disabled={loading}>
              {loading ? "Creating..." : "Create Short URL"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
