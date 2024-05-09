
import { useState, useEffect } from "react";
import AttendanceDataTable from "./AttendanceDataTable";
import useAxios from "../../../hooks/useAxios";
import { parseTimeString } from "../../util/Helper";
export default function AttendancePage() {

    const axios = useAxios();

    const referenceMorningTime = new Date();
    referenceMorningTime.setHours(9, 30, 0, 0);
    const referenceEveningTime = new Date();
    referenceEveningTime.setHours(17, 30, 0, 0);

    const [data, setData] = useState(null)

    useEffect(() => {
        fetchAttendance()
    }, [])


    async function fetchAttendance() {
        const response = await axios.get("/employee/attendance?size=100");

        const attendance = response.data.content;
        const statuses = await fetchStatuses(attendance);

        const withStatus = attendance.map((a, index) => ({
            ...a,
            status: statuses[index]
          }));


        setData(withStatus)
    }

    async function fetchStatuses(data) {
        const promises = data.map(async (row) => {
          const state = await calculateStatus(row['timeIn'], row['timeOut'], row['date']);
          return state;
        });
        return Promise.all(promises);
      }

    async function calculateStatus(arriveTime, leaveTime, date) {
        if (arriveTime == null && leaveTime == null) {
            try {
                const response = await axios.get(`/leave/${date}`);
                if (response.data == null || response.data.status != 'ACCEPTED') {
                    return 'Absence';
                } else {
                    return response.data.type + ' leave';
                }
            } catch (error) {
                return 'Error fetching leave data';
            }
        } else {
            const arrive = parseTimeString(arriveTime);
            const leave = parseTimeString(leaveTime);
            const isArriveLate = arrive > referenceMorningTime;
            const isLeaveEarly = leave < referenceEveningTime;
    
            return isArriveLate && isLeaveEarly
                ? 'Arrive late and leave early'
                : isArriveLate
                ? 'Arrive late'
                : isLeaveEarly
                ? 'Leave early'
                : 'On time';
        }
    }

    return (
        <>
            {data && <AttendanceDataTable data={data}/>}
        </>
    )
}