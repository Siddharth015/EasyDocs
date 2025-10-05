import React from 'react';
import { Link, Sparkles, FileText, ChevronRight } from 'lucide-react';

const Step = ({ icon, title }) => (
  <div className="flex flex-col items-center gap-2 text-center w-28"> {/* Added fixed width */}
    <div className="bg-[--glass] border border-[--glass-border] rounded-full p-3">
      {icon}
    </div>
    <p className="text-sm text-[--text-secondary]">{title}</p>
  </div>
);

const Steps = () => {
  return (
    <div className="flex items-center justify-center gap-2 md:gap-4 my-16">
      <Step icon={<Link className="text-[--accent-start]" />} title="1. Paste Repo URL" />
      <ChevronRight className="text-[--glass-border] mt-[-20px]" />
      <Step icon={<Sparkles className="text-[--accent-end]" />} title="2. AI Magic Happens" />
      <ChevronRight className="text-[--glass-border] mt-[-20px]" />
      <Step icon={<FileText className="text-[--text-primary]" />} title="3. Get Your Docs" />
    </div>
  );
};

export default Steps;