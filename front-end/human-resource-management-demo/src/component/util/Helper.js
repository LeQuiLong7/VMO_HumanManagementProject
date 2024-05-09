export function handleChange(event, rowIndex, data, setData) {
    const { name, value } = event.target;
    const newData = [...data];
    newData[rowIndex][name] = value;
    setData(newData);
}

export function parseTimeString (timeString) {
    const [hours, minutes] = timeString.split(':').map(Number);
    const date = new Date();
    date.setHours(hours, minutes);
    return date;
};

export function handleUpdateStateOneObject(event, setData) {
    const { name, value } = event.target
    setData(prevleaveRequest => ({
        ...prevleaveRequest,
        [name]: value
    }));
}