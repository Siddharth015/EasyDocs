import React from "react";
import { useLocation } from "react-router-dom";
import Results from "../components/Results";

const ResultsPage = () => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const repoUrl = queryParams.get("repo");

  return (
    <div>
      <h1>Generated Documentation</h1>
      <Results repoUrl={repoUrl} />
    </div>
  );
};

export default ResultsPage;
