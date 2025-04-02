import React, { useState, useEffect } from "react";

import { Clipboard } from "lucide-react"; // Clipboard icon

const Results = ({ repoUrl }) => {
    const [documentation, setDocumentation] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [copied, setCopied] = useState(false);

    useEffect(() => {
        if (!repoUrl) return;

        fetch(`/api/generate-docs?repoUrl=${encodeURIComponent(repoUrl)}`, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("github_token")}`, // Replace with your token logic
            },
        })
        .then(response => response.json())
        .then(data => {
            if (data.documentation) {
                setDocumentation(data.documentation);
            } else {
                setError("Failed to generate documentation.");
            }
        })
        .catch(() => setError("Error fetching documentation."))
        .finally(() => setLoading(false));
    }, [repoUrl]);

    const handleCopy = () => {
        navigator.clipboard.writeText(documentation).then(() => {
            setCopied(true);
            setTimeout(() => setCopied(false), 2000); // Reset after 2 seconds
        });
    };

    if (loading) return <p>Loading documentation...</p>;
    if (error) return <p className="text-red-500">{error}</p>;

    return (
        <div className="max-w-4xl mx-auto p-6 bg-white shadow-md rounded-lg">
            <h2 className="text-2xl font-semibold mb-4">Generated Documentation</h2>

            <div className="relative border p-4 bg-gray-100 rounded-md">
                <pre className="whitespace-pre-wrap text-gray-800">{documentation}</pre>

                {/* Copy ButtSon */}
                <Button 
                    className="absolute top-2 right-2 p-2" 
                    variant="outline" 
                    onClick={handleCopy}
                >
                    <Clipboard className="h-5 w-5 mr-2" /> {copied ? "Copied!" : "Copy"}
                </Button>
            </div>
        </div>
    );
};

export default Results;
