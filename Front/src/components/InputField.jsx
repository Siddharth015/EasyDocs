import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const InputField = () => {
  const [code, setCode] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null); // Added error state
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null); // Reset error state
    if (code.trim() === "") return;

    setLoading(true);
    try {
      const response = await fetch(
        "http://localhost:8080/api/generate-documentation",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ repo_url: code }),
        }
      );

      if (!response.ok) {
        const errorData = await response.json(); // extract error from backend.
        throw new Error(
          errorData.error || "Failed to generate documentation."
        );
      }

      const data = await response.json();
      if (data.files) {
        navigate("/results", { state: { documentation: data.files } });
      } else if (data.documentation) {
        navigate("/results", { state: { documentation: [{documentation: data.documentation}]} }); // handles the single string case.
      } else {
        setError("No documentation found.");
      }

    } catch (err) {
      console.error("Error:", err);
      setError(err.message || "An unexpected error occurred.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="input-container">
      <form onSubmit={handleSubmit} className="input-form">
        <textarea
          className="input-field"
          placeholder="Paste code or a description for documentation..."
          value={code}
          onChange={(e) => setCode(e.target.value)}
          required
        />
        <button type="submit" className="generate-button" disabled={loading}>
          {loading ? "Generating..." : "Generate Docs"}
        </button>
      </form>
      {error && <p className="error-message">{error}</p>} {/* Display error */}
    </div>
  );
};

export default InputField;