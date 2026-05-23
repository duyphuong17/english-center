import { useState } from 'react';
import Button from 'react-bootstrap/Button';
import Modal from 'react-bootstrap/Modal';
import axios from 'axios';
import { FcPlus } from "react-icons/fc";
import { toast } from 'react-toastify';
import { postCreateUser } from '../../../services/UserService';
function ModalCreateUser(props) {
    const { show, setShow } = props;

    const handleClose = () => {
        setShow(false)
        //khi nhấn nút đóng thì có thông tin trong ô input điền nó sẽ mất đi
        setEmail("");
        setPassword("");
        setUsername("");
        setRole("USER");
        setImage("");
        setPreviewImage("")
    };
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [username, setUsername] = useState("");
    const [gender, setGender] = useState("MALE");
    const [role, setRole] = useState("USER");
    const [image, setImage] = useState("");
    const [previewImage, setPreviewImage] = useState("");

    const handleUploadImage = (event) => {
        // event.target.files là kiểm tra xem cái tải lên phải file không
        if (event.target && event.target.files && event.target.files[0]) {
            // URL.createObjectURL(event.target.files[0]) cái này là dùng để hiển thị ảnh đầu tiên
            setPreviewImage(URL.createObjectURL(event.target.files[0]));
            setImage(event.target.files[0])
        } else {
            // setPreviewImage("");
        }
    }

    const validateEmail = (email) => {
        return String(email)
            .toLowerCase()
            .match(
                /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|.(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            );
    };
    const handSubmitCreateUser = async () => {
        const isValidEmail = validateEmail(email);

        if (!isValidEmail) {
            toast.error("Invalid email");
            return;
        }

        if (!password) {
            toast.error("Invalid password");
            return;
        }

        //email đầu là thuộc tính bên backend ,email sau là chổ khai báo usestate của frontend
        const data = {
            email: email,
            password: password,
            name: username,
            gender: gender
        };

        // dùng try catch vì : ở Api postCreateUser viết bên backend nếu nhập trùng email thì sẽ lỗi nếu lỗi mà không dùng try catch thi chương trình đứng lại luôn không chạy nữa nên phải có try cacth thì khi có lỗi nó sẽ chạy xuống catch
        try {
            const res = await postCreateUser(data);

            console.log(">>> check res: ", res);

            if (res && res.error == null) {
                toast.success(res.message);
                handleClose();
                //gọi lại hàm này để cậm nhật lại giao diện ngay lập tức khi vừa thêm mới user
                await props.fetchListUsers();
            }

        }
        // err là trả về toàn bộ thông tin object lỗi 
        catch (err) {
            console.log(">>> check error: ", err);

            if (err && err.error) {
                toast.error(err.error);
            }
        }
    };
    return (
        <>
            {/* <Button variant="primary" onClick={handleShow}>
                Launch demo modal
            </Button> */}

            <Modal
                show={show}
                onHide={handleClose}
                size='xl'
                backdrop="static"
                //className='modal-add-user' này quan trọng nếu không có đặt class thì css modal sẽ không ăn vì modal không nằm trong div id="root"
                className='modal-add-user'
            >
                <Modal.Header closeButton>
                    <Modal.Title>Add new user</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <form className="row g-3">
                        <div className="col-md-6">
                            <label className="form-label">Email</label>
                            <input type="email"
                                className="form-control"
                                value={email}
                                onChange={(event) => setEmail(event.target.value)}
                            />
                        </div>

                        <div className="col-md-6">
                            <label className="form-label">Password</label>
                            <input type="password"
                                className="form-control"
                                value={password}
                                onChange={(event) => setPassword(event.target.value)}
                            />
                        </div>

                        <div className="col-md-6">
                            <label className="form-label">Username</label>
                            <input type="text"
                                className="form-control"
                                value={username}
                                onChange={(event) => setUsername(event.target.value)}
                            />
                        </div>
                        <div className="col-md-4">
                            <label className="form-label">Gender</label>
                            <select className="form-select"
                                //dùng onchange thì khi gõ vào ô input nó mới thay đổi được 
                                onChange={(event) => setGender(event.target.value)}
                                value={gender}
                            >
                                <option value="MALE">MALE</option>
                                <option value="FEMALE">FEMALE</option>
                                <option value="OTHER">OTHER</option>
                            </select>
                        </div>
                        <div className="col-md-4">
                            <label className="form-label">Role</label>
                            <select className="form-select"
                                //dùng onchange thì khi gõ vào ô input nó mới thay đổi được 
                                onChange={(event) => setRole(event.target.value)}
                                value={role}
                            >
                                <option value="USER">USER</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                        </div>
                        <div className='col-md-12'>
                            <label className="form-label label-upload" htmlFor='labelUpload'>
                                <FcPlus /> Upload File Image</label>
                            <input type="file"
                                id='labelUpload' hidden
                                onChange={(event) => handleUploadImage(event)}
                            />
                        </div>

                        <div className='col-md-12 img-preview'>
                            {previewImage ?
                                <img src={previewImage} />
                                :
                                <span>Preview Image</span>
                            }


                        </div>
                    </form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={() => handSubmitCreateUser()}>
                        Save
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default ModalCreateUser;