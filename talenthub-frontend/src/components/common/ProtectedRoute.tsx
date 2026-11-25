
import { Navigate, Outlet, useLocation } from "react-router-dom";

function ProtectedRoute() {
    const location = useLocation();

    const currentUser = sessionStorage.getItem("currentUser");

    if (!currentUser) {
        return (
            <Navigate
                to="/login"
                replace
                state={{ from: location }}
            />
        );
    }

    return <Outlet />;
}

export default ProtectedRoute;
