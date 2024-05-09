import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import { IconButton } from '@mui/material';
import Dialog from '@mui/material/Dialog';
import { Button } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import MyLoadingButton from './MyLoadingButton';
import React, { useState } from 'react';
export default function BasicDialog({title, open, noAction, normalButton, handleClose, fullScreen, content, disabledButton, buttonText, buttonIcon, onButtonClick}) {
        
    return (
        <Dialog
                onClose={handleClose}
                aria-labelledby="customized-dialog-title"
                open={open}
                fullScreen={fullScreen}
                maxWidth='md'
                sx={{minWidth:' 500px'}}
            >
                <DialogTitle sx={{ m: 0, p: 2 }} bgcolor={'primary.main'} color={'white'} id="customized-dialog-title">
                    {title}
                </DialogTitle>
                <IconButton
                    aria-label="close"
                    onClick={handleClose}
                    sx={{
                        position: 'absolute',
                        right: 8,
                        top: 8,
                        color: 'white'
                    }}
                >
                    <CloseIcon />
                </IconButton>
                <DialogContent dividers>

                    {content.map(content => (content))}
                </DialogContent>
                {
                    noAction ?? 
                    <DialogActions>
                        <Button sx={{ color: 'primary.light' }} onClick={handleClose}>
                            Cancel
                        </Button>
                        {
                            normalButton ? 
                            <Button  variant="contained"  text={buttonText} onClick={onButtonClick}>Add</Button> :
                            <MyLoadingButton disabled={disabledButton}  text={buttonText} startIcon={buttonIcon} size="medium"  variant="contained" onClick={onButtonClick} />

                        }
                    </DialogActions>
                }

            </Dialog>
    )
}