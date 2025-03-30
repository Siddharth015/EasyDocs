import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const InputField = () => {
  const [url, setUrl] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    if (url.trim() !== "") {
      navigate(`/results?repo=${encodeURIComponent(url)}`);
    }
  };

  return (
    <div className="input-container">
      <form onSubmit={handleSubmit} className="input-form">
        <input
          type="url"
          className="input-field"
          placeholder="Enter GitHub Repository URL..."
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          required
        />
        <button type="submit" className="generate-button">
          Generate Docs
        </button>
      </form>
    </div>
  );
};

export default InputField;
