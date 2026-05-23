import axios from "axios";
const instance = axios.create({
    baseURL: 'http://localhost:8080/',
    headers: {
        "Content-Type": "application/json"
    }
});

// Add a request interceptor
// Interceptor REQUEST: chạy trước khi gửi request
instance.interceptors.request.use(
    function (config) {
        // config chứa thông tin request (url, method, headers, data...)
        // Có thể chỉnh sửa config trước khi gửi (ví dụ: thêm token)



        return config; // bắt buộc return để request tiếp tục
    },
    function (error) {
        // Lỗi xảy ra trước khi request được gửi
        return Promise.reject(error);
    }
);

// Interceptor RESPONSE: chạy sau khi nhận response
instance.interceptors.response.use(
    function (response) {

        // Trả về response.data để khi gọi API không cần .data nữa
        return response && response.data ? response.data : response;
    },
    function (error) {
        // Chạy khi response lỗi (4xx, 5xx)
        // console.log("cekcd >>>> ", error.response.data)
        // return error && error.response && error.response.data
        //     ? error.response.data : Promise.reject(error);

        return Promise.reject(
            error && error.response && error.response.data
                ? error.response.data
                : error
        );
    }
);


export default instance;