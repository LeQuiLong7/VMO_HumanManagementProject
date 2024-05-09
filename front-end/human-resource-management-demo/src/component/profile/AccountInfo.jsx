import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Divider from '@mui/material/Divider';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useAlertContext } from '../../context/AlertContext';
import useAxios from '../../hooks/useAxios';


export default function AccountInfo({ avatarUrl, name, email, role}) {

    const axios = useAxios();
    const avatarInputRef = React.useRef()
    const { displayAlert, setDisplayAlert, setResponseData } = useAlertContext();

    async function handleUpload(e) {
        if(displayAlert) 
            setDisplayAlert(false)

        const file = e.target.files[0];

        const formData = new FormData();
        formData.append('file', file);
        try {
            const response = await axios.put("/profile/avatar", formData);
            setResponseData({
                success: true,
                message: 'Upload avatar successfully'
            })
            setTimeout(() => {
                window.location.reload()
            }, 2)
        } catch(error) {
            setResponseData({
                success: false,
                message: 'Upload avatar failed ' + (error.response.data.error ? error.response.data.error : '')
            })
        } finally {
            setDisplayAlert(true)
        }
        

    }
    return (
        <>
            <Card>
                <CardContent>
                    <Stack spacing={2} sx={{ alignItems: 'center' }}>
                            <Avatar src={`${avatarUrl}?${new Date().getTime()}`}  alt={name} sx={{ height: '200px', width: '200px' }} />
                        <Stack spacing={1} sx={{ textAlign: 'center' }}>
                            <Typography variant="h5">{name}</Typography>
                            <Typography color="text.secondary" variant="body1">
                                {email}
                            </Typography>
                            <Typography color="text.secondary" variant="body1">
                                {role}
                            </Typography>
                        </Stack>
                    </Stack>
                </CardContent>
                <Divider />
                <CardActions> 
                    <input ref={avatarInputRef} onChange={handleUpload} type='file' style={{display:'none'}}/>
                    <Button fullWidth variant="text" onClick={() => avatarInputRef.current.click()}>
                        Upload picture
                    </Button>
                </CardActions>
            </Card>

        </>
    )
}