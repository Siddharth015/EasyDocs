import React from "react";

const Footer = () => {
  return (
    <footer className="w-full text-center py-4 px-6 bg-black/20 border-t border-white/10">
      <p className="text-sm text-[--text-secondary]">
        © {new Date().getFullYear()} EasyDocs. All rights reserved.
      </p>
    </footer>
  );
};

export default Footer;