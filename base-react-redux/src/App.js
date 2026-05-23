import logo from './logo.svg';
import './App.scss';
import { useDispatch, useSelector } from 'react-redux';
import { increaseCounter, decreaseCounter } from './redux/action/counterAction';
import Header from './components/header/Header';
import { Outlet } from 'react-router-dom';
const App = () => {

  return (
    <div className='app-container'>
      <div className='header-container'>
        <Header />
      </div>

      <div className='main-container'>
        <div className='sidenav-container'>

        </div>
        <div className='app-content'>

          <Outlet></Outlet>
        </div>
      </div>
    </div>


  );
}

export default App;
