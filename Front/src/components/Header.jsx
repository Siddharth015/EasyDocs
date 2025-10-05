import React from 'react';
import { Github } from 'lucide-react'; 

const Header = () => {
  return (
    <header className="w-full py-6 px-4">
      <nav 
        className="max-w-fit mx-auto flex items-center gap-6 bg-black/20 border border-white/10 
                   rounded-full px-6 py-3"
      >
        <a href="/" className="text-xl font-bold text-[--text-primary] hover:opacity-80 transition-opacity">
          Easy<span className="text-gradient-effect">Docs</span>
        </a>

        {/* GitHub Icon Button */}
        <a 
          href="https://github.com/Siddharth015/EasyDocs" 
          target="_blank" 
          rel="noopener noreferrer"
          aria-label="GitHub Repository"
          className="bg-[--glass] border border-[--accent-start] rounded-full p-2 text-[--text-secondary] 
                     hover:border-[--accent-end] hover:text-white transition-all"
        >
          <Github size={20} />
        </a>
      </nav>
    </header>
  );
};

export default Header;