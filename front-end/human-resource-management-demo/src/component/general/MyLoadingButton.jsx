import LoadingButton from '@mui/lab/LoadingButton';
import { useState } from 'react';
import { useAlertContext } from '../../context/AlertContext';

export default function MyLoadingButton({startIcon, fullwidth,  endIcon,  disabled,  size, variant, text, onClick, customMessage, messagePath, hide}) {
    const [loading, setLoading] = useState(false)
    const { displayAlert, setDisplayAlert, responseData, setResponseData } = useAlertContext();

    async function handleClick() {
        setLoading(true);
        if (displayAlert)
            setDisplayAlert(false)
        try {
            const response = await onClick(); 
            if(customMessage) {
                setResponseData({
                    success: true,
                    message: response[messagePath]
                })
            } else {
                setResponseData({
                    success: true,
                    message: text + ' successfully!'
                })
            }
        } catch(error) {
            console.log(error);
            try {
                setResponseData({
                    success: false,
                    message: error.response.data.error
                })
            } catch(error) {
                console.log(error);
                setResponseData({
                    success: false,
                    message: "Some error happened. Check the log for more detail!"
                })
            }
        } finally {
            setDisplayAlert(true)
            setTimeout(() => {
                setLoading(false); 
            }, 500);
        }
    }

    return (
        <LoadingButton sx={{display: hide? 'none': ''}} disabled={disabled} fullWidth={fullwidth} loading={loading} startIcon={startIcon} endIcon={endIcon} size={size} variant={variant} onClick={handleClick} >
            {text}
        </LoadingButton>

    )
}