import React from "react";
import { useLocation } from "react-router-dom";
import DocGenerator from "../components/DocGenerator";

const ResultsPage = () => {
  const location = useLocation();
  const documentation = location.state?.documentation || [];

  return (
    <div className="results-page">
     
      <div className="scroll-container"> {/* Added scroll wrapper */}
        <DocGenerator documentation={documentation} />
      </div>
    </div>
  );
};

export default ResultsPage;