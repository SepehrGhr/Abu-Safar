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
            <div className="relative text-gray-800 dark:text-gray-200 font-sans antialiased transition-colors duration-300">

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