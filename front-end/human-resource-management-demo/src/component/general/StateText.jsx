import { Typography } from "@mui/material"
export default function StateText({variant, bgcolor, text}) {
            
    return (
        <Typography variant={variant} sx={{backgroundColor: bgcolor ,display:'inline', p:1, borderRadius: 1, color:'white'}}>{text}</Typography>
    )        
    
}