import React from "react";

const DocGenerator = ({ documentation }) => {
  return (
    <div className="documentation-container">
      {documentation?.map((file, index) => (
        <div key={index} className="file-documentation">
          <h3>{file.name}</h3>
          <div className="docs-content"> {/* Added scroll container */}
            <pre>{file.documentation}</pre>
          </div>
        </div>
      ))}
    </div>
  );
};

export default DocGenerator;