import axios from "axios";
 const useAxios = () => {
    const axiosInstance = axios.create({
        baseURL: 'http://localhost:8080',
        headers: {
              Authorization: `${localStorage.getItem("access-token")}`,
        }
    });

    axiosInstance.interceptors.response.use(
        response => response,
        error => {
            if (error.response.status == 403) 
                if(window.location.pathname != '/' && window.location.pathname != '/login' )
                    window.location.assign("/login")
            
            return Promise.reject(error);
        }
    );

    return axiosInstance;
};

export default useAxios; 