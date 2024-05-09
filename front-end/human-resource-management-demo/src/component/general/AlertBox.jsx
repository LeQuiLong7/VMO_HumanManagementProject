import { Alert, Box, Slide } from "@mui/material";
import { useEffect } from "react";
import { useAlertContext } from "../../context/AlertContext";

export default function AlertBox() {
    const { displayAlert, setDisplayAlert, responseData, setResponseData } = useAlertContext();

    useEffect(() => {
        if (displayAlert) {
            const timer = setTimeout(() => {
                setDisplayAlert(false);
            }, responseData.success ? 3000 : 10000);
            return () => clearTimeout(timer);
        }
    }, [displayAlert, setDisplayAlert]);


    return (
        <Box sx={{ position: 'fixed', right: '0', top: '100px', width: '500px', zIndex: 9999 }}>
            <Slide in={displayAlert} direction="left" mountOnEnter unmountOnExit >
                <Alert variant="filled" severity={ responseData.success ? 'success' : 'error'} sx={{ mx: 2 }} >
                    {responseData.message}
                </Alert>
            </Slide>
        </Box>
    )
}