import React, { useState } from "react";
import { useLocation, Link } from "react-router-dom";
import { Clipboard, Check, Home } from "lucide-react";
import Header from "../components/Header";
import Footer from "../components/Footer";

const ResultsPage = () => {
  const location = useLocation();
  const documentationFiles = location.state?.documentation || [];
  const [copiedStates, setCopiedStates] = useState({});

  const handleCopy = (text, index) => {
    navigator.clipboard.writeText(text).then(() => {
      setCopiedStates({ ...copiedStates, [index]: true });
      setTimeout(() => {
        setCopiedStates((prev) => ({ ...prev, [index]: false }));
      }, 2000);
    });
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <main className="flex-grow flex flex-col items-center px-4 py-8">
        <div className="w-full max-w-4xl">
          <h1 className="text-4xl font-bold text-text-primary mb-2">
            Generated Documentation
          </h1>
          <p className="text-text-secondary mb-8">
            Here are the generated documents for your repository.
          </p>

          {documentationFiles.length > 0 ? (
            <div className="space-y-6">
              {documentationFiles.map((file, index) => (
                <div key={index} className="bg-glass border border-glass-border rounded-lg">
                  <div className="flex justify-between items-center bg-black/20 px-4 py-2 border-b border-glass-border">
                    <h3 className="font-mono text-text-primary">
                      {file.file_path || `Documentation ${index + 1}`}
                    </h3>
                    <button
                      onClick={() => handleCopy(file.documentation, index)}
                      className="flex items-center gap-2 text-xs text-text-secondary hover:text-white transition-colors"
                      disabled={copiedStates[index]}
                    >
                      {copiedStates[index] ? (
                        <><Check size={14} className="text-green-400" /> Copied!</>
                      ) : (
                        <><Clipboard size={14} /> Copy</>
                      )}
                    </button>
                  </div>
                  
                  {/* --- THIS IS THE REVERTED CODE --- */}
                  {/* It now uses a simple <pre> tag to show the raw text */}
                  <div className="p-4 max-h-[40rem] overflow-y-auto">
                    <pre className="whitespace-pre-wrap text-left text-sm font-mono text-text-secondary">
                      {file.documentation}
                    </pre>
                  </div>
                  {/* --- END OF REVERTED CODE --- */}

                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-16 border border-dashed border-glass-border rounded-lg">
              <p className="text-text-secondary mb-4">No documentation was generated or found.</p>
              <Link to="/" className="flex items-center justify-center gap-2 bg-gradient-primary text-white font-semibold py-2 px-4 rounded-lg
                     hover:opacity-90 transition-opacity transform hover:scale-105">
                <Home size={16} /> Go Back Home
              </Link>
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default ResultsPage;