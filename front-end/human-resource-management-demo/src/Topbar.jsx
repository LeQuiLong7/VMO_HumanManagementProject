import { useEffect } from "react"
import { useLocation } from "react-router-dom"
import image from "./image.png"

export default function Topbar() {
    const location = useLocation()
    useEffect(() =>{
        console.log(location);
    }, [])
    return <div className="top-bar">
        <input type="text" placeholder="Search..." class="search-input"/>
        <div className="current-user">
            <img src={location.state.avatarUrl == null ? image : location.state.avatarUrl}></img>
            <h5>{location.state.firstName + " " + location.state.lastName}</h5>
        </div>
    </div>
}