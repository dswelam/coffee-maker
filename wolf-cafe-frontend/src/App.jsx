import './App.css'
import {BrowserRouter, Routes, Route, Navigate} from 'react-router-dom'
import HeaderComponent from './components/HeaderComponent'
import FooterComponent from './components/FooterComponent'
import ListItemsComponent from './components/ListItemsComponent'
import ItemComponent from './components/ItemComponent'
import RegisterComponent from './components/RegisterComponent'
import LoginComponent from './components/LoginComponent'
import { isUserLoggedIn } from './services/AuthService'

function App() {

  function AuthenticatedRoute({children}) {
    const isAuth = isUserLoggedIn()
	if (isAuth) {
	  return children
	}
	return <Navigate to='/' />
  }

  return (
    <>
      <BrowserRouter>
	  <HeaderComponent />
	  <Routes>
	  	<Route path='/' element={<LoginComponent />}></Route>
		<Route path='/register' element={<RegisterComponent />}></Route>
		<Route path='/login' element={<LoginComponent />}></Route>
		<Route path='/items' element={<AuthenticatedRoute><ListItemsComponent /></AuthenticatedRoute>}></Route>
		<Route path='/add-item' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>
		<Route path='/update-item/:id' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>
	  </Routes>
	  <FooterComponent />
	  </BrowserRouter>
    </>
  )
}

export default App
