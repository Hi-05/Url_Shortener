import { useState } from "react";
import { updateUrl } from "../api/urls";

export default function EditUrlModal({ url, onClose, onUpdated }) {
  const [longUrl, setLongUrl] = useState(url.longUrl);
  const [description, setDescription] = useState(url.description || "");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const updated = await updateUrl(url.code, longUrl, description);
      onUpdated(updated);
      onClose();
    } catch (err) {
      setError(err.response?.data?.error || "Failed to update URL");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">Edit URL</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <div className="modal-code-badge">
          <span className="code-label">Short code</span>
          <span className="code-value">/{url.code}</span>
        </div>
        <form onSubmit={handleSubmit} className="modal-form">
          <div className="field-group">
            <label className="field-label">Long URL</label>
            <input
              className="field-input"
              type="url"
              value={longUrl}
              onChange={(e) => setLongUrl(e.target.value)}
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
            />
          </div>
          {error && <p className="error-msg">{error}</p>}
          <div className="modal-actions">
            <button type="button" className="cancel-btn" onClick={onClose}>Cancel</button>
            <button type="submit" className="submit-btn" disabled={loading}>
              {loading ? "Saving..." : "Save Changes"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
