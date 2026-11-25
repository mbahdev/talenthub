
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import MainLayout from "./components/layout/MainLayout";
import ProtectedRoute from "./components/common/ProtectedRoute";

import DashboardPage from "./pages/dashboard/DashboardPage";
import MissionsPage from "./pages/missions/MissionsPage";
import ApplicationsPage from "./pages/applications/ApplicationsPage";
import FavoritesPage from "./pages/favorites/FavoritesPage";
import LoginPage from "./pages/auth/LoginPage";
import ProfilePage from "./pages/profile/ProfilePage";
import RegisterPage from "./pages/auth/RegisterPage";
import MissionDetailsPage from "./pages/missions/MissionDetailsPage";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />

                <Route element={<MainLayout />}>
                    <Route path="/" element={<Navigate to="/missions" replace />} />
                    <Route path="/missions" element={<MissionsPage />} />

                    <Route element={<ProtectedRoute />}>
                        <Route path="/dashboard" element={<DashboardPage />} />
                        <Route path="/applications" element={<ApplicationsPage />} />
                        <Route path="/favorites" element={<FavoritesPage />} />
                        <Route path="/profile" element={<ProfilePage />} />
                    </Route>
                </Route>

                <Route path="*" element={<Navigate to="/missions" replace />} />
                <Route path="/missions" element={<MissionsPage />} />
                <Route path="/missions/:id" element={<MissionDetailsPage />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
