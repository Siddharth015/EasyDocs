import React from 'react';
import Header from '../components/Header';
import InputField from '../components/InputField';
import Steps from '../components/Steps';
import Footer from '../components/Footer';
import { Github } from 'lucide-react';

const Home = () => {
  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      
      <main className="relative flex-grow flex flex-col items-center justify-center text-center px-4 overflow-hidden">
        
        <div 
          className="absolute right-[-8rem] top-1/2 -translate-y-1/2 
                     flex items-center justify-center -z-10 opacity-10"
        >
          <Github className="h-[35rem] w-[35rem] text-white animate-tail-wag" />
        </div>
        
        {/* Main Content */}
        <div className="relative z-10">
          <div className="max-w-3xl w-full">
            <h1 className="text-4xl md:text-6xl font-bold text-[--text-primary] tracking-tight">
              Instant Documentation for Your{' '}
              <span className="text-gradient-effect">
                Code.
              </span>
            </h1>
            <p className="mt-4 text-lg text-[--text-secondary] max-w-2xl mx-auto">
              EasyDocs transforms your GitHub repository into professional documentation with a single click. Save time and effort, get clear, structured docs instantly.
            </p>
          </div>

          <Steps />
          <InputField />
        </div>

      </main>

      <Footer />
    </div>
  );
};

export default Home;