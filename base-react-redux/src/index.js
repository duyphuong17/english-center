import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { Provider } from 'react-redux';
import store from './redux/store';
import 'bootstrap/dist/css/bootstrap.min.css';
import { BrowserRouter, Route, Routes } from "react-router-dom";
import User from './components/user/User';

import HomePage from './components/homepage/Homepage';
import ManagerUser from './components/admin/content/ManagerUser';
import DashBoard from './components/admin/content/DashBoard';
import Admin from './components/admin/Admin';


const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <Provider store={store}>

    <BrowserRouter>
      <Routes>
        <Route path="/" element={<App />}>
          <Route index element={<HomePage />} />
          <Route path="users" element={<User />} />
        </Route>



        <Route path="admins" element={<Admin />} >
          <Route index element={<DashBoard />} />
          <Route path="manage-users" element={<ManagerUser />} />
        </Route>

      </Routes>
    </BrowserRouter>

  </Provider>
);
reportWebVitals();
