import { useState } from "react";
import UrlCard from "./UrlCard";

export default function UrlList({ urls, onDeleted, onUpdated }) {
  const [search, setSearch] = useState("");

  const filtered = urls.filter(
    (u) =>
      u.longUrl?.toLowerCase().includes(search.toLowerCase()) ||
      u.code?.toLowerCase().includes(search.toLowerCase()) ||
      u.description?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="url-list-container">
      <div className="list-header">
        <h2 className="list-title">Your Links <span className="count-badge">{urls.length}</span></h2>
        <input
          className="search-input"
          type="text"
          placeholder="Search by URL, code, or description..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {filtered.length === 0 ? (
        <div className="empty-state">
          {urls.length === 0 ? (
            <>
              <div className="empty-icon">⚡</div>
              <p className="empty-title">No links yet</p>
              <p className="empty-sub">Click "Shorten URL" to create your first link</p>
            </>
          ) : (
            <>
              <div className="empty-icon">🔍</div>
              <p className="empty-title">No results found</p>
              <p className="empty-sub">Try a different search term</p>
            </>
          )}
        </div>
      ) : (
        <div className="url-grid">
          {filtered.map((url) => (
            <UrlCard
              key={url.code}
              url={url}
              onDeleted={onDeleted}
              onUpdated={onUpdated}
            />
          ))}
        </div>
      )}
    </div>
  );
}
