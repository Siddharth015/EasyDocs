import React from "react";

const Results = ({ repoUrl }) => {
  return (
    <div className="results-container">
      <h2>Documentation for:</h2>
      <p>{repoUrl}</p>
      {/* AI-generated documentation output will be displayed here */}
    </div>
  );
};

export default Results;
