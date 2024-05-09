import { Button, Stack } from "@mui/material";
import AddCircleIcon from '@mui/icons-material/AddCircle';

export default function CreateNewButton({ onClickEvent, text}) {
    return (
        <Stack direction={'row-reverse'} m={2}>
            <Button variant="contained" onClick={e => {
                onClickEvent();
            }} startIcon={<AddCircleIcon />} size="large">
                {text || 'Create new'}
            </Button>
        </Stack>
    )
}