import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Wand2 } from 'lucide-react';

const InputField = () => {
  const [code, setCode] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (code.trim() === "") return;

    setLoading(true);
    try {
      const response = await fetch("http://localhost:8080/api/generate-documentation", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ repo_url: code }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || "Failed to generate documentation.");
      }

      const data = await response.json();
      if (data.files) {
        navigate("/results", { state: { documentation: data.files } });
      } else if (data.documentation) {
        navigate("/results", { state: { documentation: [{ documentation: data.documentation }] } });
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
    <div className="w-full max-w-2xl mt-8">
      <form onSubmit={handleSubmit} className="flex flex-col sm:flex-row items-center gap-4">
        <textarea
          placeholder="Paste your public GitHub repo URL here..."
          rows="1"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          required
          className="w-full flex-grow bg-transparent border-2 border-[--glass-border] rounded-lg py-3 px-4 text-[--text-primary] resize-none
                     focus:outline-none focus:ring-2 focus:ring-[--accent-end] transition-all"
        />
        <button
          type="submit"
          disabled={loading}
          className="bg-gradient-primary text-white w-full sm:w-auto flex items-center justify-center gap-2 font-semibold py-3 px-6 rounded-lg
                     hover:opacity-90 transition-opacity transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {loading ? (
            "Generating..."
          ) : (
            <>
              <Wand2 size={18} />
              Generate Docs
            </>
          )}
        </button>
      </form>
      {error && <p className="text-red-400 mt-2 text-sm">{error}</p>}
    </div>
  );
};

export default InputField;