import { LineChart } from '@mui/x-charts/LineChart';

export default function Chart({xAxisData, yAxisData, fullDate}) {


    function format(date) {
        let day = date.getDate();
        let month = date.getMonth() + 1; // Months are zero-based
        let year = date.getFullYear();

        // Step 3: Format the day and month to always be two digits
        day = day < 10 ? '0' + day : day;
        month = month < 10 ? '0' + month : month;

        // Step 4: Format the year to be two digits
        year = year.toString().slice(-2);

        if(fullDate)
        // Step 5: Combine the formatted parts into the desired string format
            return `${day}/${month}/${year}`;
        return `${month}/20${year}`;

    }
    return (
        <LineChart
            
            xAxis={[{
                data: xAxisData, scaleType:'point', valueFormatter: (date) => format(date)}]}
            series={[
                {
                    data: yAxisData
                
                },
                
            ]}
            yAxis={[{
                min: 0,
                max: 100
            }]}
            sx={{width: '100%'}}
            height={300}
        />
    );
}