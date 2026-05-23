import { useState } from "react";
import ModalCreateUser from "./ModalCreateUser";
import './manageUser.scss'
import { FcPlus } from "react-icons/fc";
import TableUser from "./TableUser";
import { useEffect } from "react";
import { getAllUsers } from "../../../services/UserService";
import ModalUpdateUser from "./ModalUpdateUser";

const ManagerUser = (props) => {

    const [showModalCreateUser, setShowModalCreateUser] = useState(false);
    const [showModalUpdateUser, setShowModalUpdateUser] = useState(false)
    const [listUsers, setListUsers] = useState([])

    // hàm useEffect và fetchListUsers để qua thằng cha là manager user để thực hiện chức năng cập nhật lại người dùng ra giao diên ngay lặp tức không thì để bên file table user rồi
    useEffect(() => {
        fetchListUsers();
    }, []);
    const fetchListUsers = async () => {
        let res = await getAllUsers();
        if (res.error == null) {
            setListUsers(res.data.result)
        }
    }

    const handleClickBtnUpdate = () => {
        // nhấn vào thì mở modal ra
        setShowModalUpdateUser(true);

    }
    return (
        <div className="manage-user-container">
            <div className="title">
                Manage User
            </div>
            <div className="users-content">
                <div className="btn-add-new">
                    <button className="btn btn-primary" onClick={() => setShowModalCreateUser(true)}> <FcPlus></FcPlus> Add new users</button>
                </div>
                <div className="table-users-container">
                    <TableUser
                        listUsers={listUsers}
                        handleClickBtnUpdate={handleClickBtnUpdate}
                    />

                </div>
                <ModalCreateUser show={showModalCreateUser}
                    setShow={setShowModalCreateUser}
                    fetchListUsers={fetchListUsers}
                />
                <ModalUpdateUser
                    show={showModalUpdateUser}
                    setShow={setShowModalUpdateUser}
                />
            </div>
        </div>
    )
}

export default ManagerUser;