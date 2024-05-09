import { TextareaAutosize as BaseTextareaAutosize } from '@mui/base/TextareaAutosize';

export default function TextArea({value, minRows}) {
    return (<>
                            <BaseTextareaAutosize placeholder='Description'  disabled name='description' value={value} minRows={minRows} sx={{ mb: 2 }}
                            style={{
                                boxSizing: 'border-box',
                                width: '100%',
                                fontFamily: 'IBM Plex Sans, sans-serif',
                                fontSize: '0.875rem',
                                fontWeight: 400,
                                lineHeight: 1.5,
                                padding: '8px 12px',
                                borderRadius: '8px',
                                border: '1px solid #DAE2ED '
                            }}
                        ></BaseTextareaAutosize>
    </>)
}