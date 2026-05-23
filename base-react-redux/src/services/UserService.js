import axiosClient from "../utils/axiosCustomize"
const postCreateUser = (data) => {
    return axiosClient.post('users', data)
}
const getAllUsers = () => {
    return axiosClient.get('users')
}
export { postCreateUser, getAllUsers }