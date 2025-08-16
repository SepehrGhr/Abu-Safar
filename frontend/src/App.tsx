import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import HomePage from './pages/HomePage';
import TicketSearchResultsPage from './pages/TicketSearchResultPage';

function App() {
    const [theme, setTheme] = useState('dark');

    useEffect(() => {
        document.documentElement.classList.toggle('dark', theme === 'dark');
    }, [theme]);

    return (
        <>
            <style>
              {`@import url('https://fonts.googleapis.com/css2?family=Kameron:wght@700&display=swap'); .font-kameron { font-family: 'Kameron', serif; }`}
            </style>
            <div className="relative bg-white dark:bg-slate-900 text-gray-800 dark:text-gray-200 font-sans antialiased transition-colors duration-300">
                <div
                    className="fixed inset-0 z-[-1] transition-opacity duration-500"
                    style={{
                        backgroundImage: `linear-gradient(to bottom, #0f172a 20%, #4a2f27 75%, #8c7646 100%)`,
                        opacity: theme === 'dark' ? 1 : 0,
                    }}
                />
                <Router>
                    <Header theme={theme} setTheme={setTheme} />
                    <main>
                        <Routes>
                            <Route path="/" element={<HomePage />} />
                            <Route path="/results" element={<TicketSearchResultsPage />} />
                        </Routes>
                    </main>
                    <Footer />
                </Router>
            </div>
        </>
    );
}

export default App;