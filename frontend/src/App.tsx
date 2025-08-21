import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import HomePage from './pages/HomePage';
import TicketSearchResultPage from './pages/TicketSearchResultPage';
import AuthPage from './pages/AuthPage';
import ProfilePage from './pages/ProfilePage';
import ProtectedRoute from './components/common/ProtectedRoute';
import { ReservationPage } from './pages/ReservationPage';

function App() {
    const [theme, setTheme] = useState('dark');

    useEffect(() => {
        document.documentElement.classList.toggle('dark', theme === 'dark');
    }, [theme]);

    return (
        <div className="relative text-gray-800 dark:text-gray-200 font-sans antialiased transition-colors duration-300">
            <Router>
                <Header theme={theme} setTheme={setTheme} />
                <main>
                    <Routes>
                        {/* Public Routes */}
                        <Route path="/" element={<HomePage />} />
                        <Route path="/auth" element={<AuthPage />} />
                        <Route path="/ticket-search-result" element={<TicketSearchResultPage />} />


                        {/* Protected Routes */}
                        <Route element={<ProtectedRoute />}>
                            <Route path="/profile" element={<ProfilePage />} />
                            <Route path="/reservation" element={<ReservationPage />} />

                        </Route>
                    </Routes>
                </main>
                <Footer />
            </Router>
        </div>
    );
}

export default App;