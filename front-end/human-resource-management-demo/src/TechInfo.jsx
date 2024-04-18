export default function TechInfo({techId, techName, yearOfExperience}) {
    return (
        <tr className="bold strong tech-info">
            <td>{techId}</td>
            <td>{techName}</td>
            <td>{yearOfExperience}</td>
        </tr>
    )

}