import { useState } from "react";
import { deleteUrl } from "../api/urls";
import EditUrlModal from "./EditUrlModal";

export default function UrlCard({ url, onDeleted, onUpdated }) {
  const [showEdit, setShowEdit] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [copied, setCopied] = useState(false);
  const [deleting, setDeleting] = useState(false);

  const shortUrl = `http://localhost:8080/${url.code}`;

  const handleCopy = () => {
    navigator.clipboard.writeText(shortUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await deleteUrl(url.code);
      onDeleted(url.code);
    } catch (err) {
      setDeleting(false);
    }
  };

  const formatDate = (dateStr) => {
    return new Date(dateStr).toLocaleDateString("en-IN", {
      day: "numeric",
      month: "short",
      year: "numeric",
    });
  };

  const truncate = (str, max) =>
    str?.length > max ? str.slice(0, max) + "..." : str;

  return (
    <>
      <div className="url-card">
        <div className="url-card-top">
          <div className="url-info">
            <div className="url-short-row">

              <a className="short-link"
                href={shortUrl}
                target="_blank"
                rel="noreferrer"
                title={url.longUrl}
              >
                snip/{url.code}
              </a>
              <button className="copy-btn" onClick={handleCopy}>
                {copied ? "✓ Copied" : "Copy"}
              </button>
            </div>
            {url.description && (
              <p className="url-description">{url.description}</p>
            )}
          </div>

          <div className="url-card-actions">
            <button
              className="action-btn edit-btn"
              onClick={() => setShowEdit(true)}
              title="Edit"
            >
              ✏️
            </button>
            <button
              className="action-btn delete-btn"
              onClick={() => setConfirmDelete(true)}
              title="Delete"
            >
              🗑️
            </button>
          </div>
        </div>

        <div className="url-card-bottom">
          <div className="url-meta">
            <span className="meta-item">📅 {formatDate(url.createdAt)}</span>
          </div>
          <div className="click-badge">
            <span className="click-count">{url.clickCount}</span>
            <span className="click-label">clicks</span>
          </div>
        </div>
      </div>

      {confirmDelete && (
        <div className="modal-overlay" onClick={() => setConfirmDelete(false)}>
          <div className="modal-card confirm-card" onClick={(e) => e.stopPropagation()}>
            <h3 className="confirm-title">Delete this URL?</h3>
            <p className="confirm-msg">
              <strong>/{url.code}</strong> and all its click history will be permanently deleted.
            </p>
            <div className="modal-actions">
              <button className="cancel-btn" onClick={() => setConfirmDelete(false)}>
                Cancel
              </button>
              <button
                className="delete-confirm-btn"
                onClick={handleDelete}
                disabled={deleting}
              >
                {deleting ? "Deleting..." : "Yes, Delete"}
              </button>
            </div>
          </div>
        </div>
      )}

      {showEdit && (
        <EditUrlModal
          url={url}
          onClose={() => setShowEdit(false)}
          onUpdated={(updated) => {
            onUpdated(updated);
            setShowEdit(false);
          }}
        />
      )}
    </>
  );
}
