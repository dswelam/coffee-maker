import React from 'react'
import { NavLink } from 'react-router-dom'
import { useNavigate } from 'react-router-dom'
import { isAdminUser, isStaffUser, isCustomerUser, isUserLoggedIn, logout } from '../services/AuthService'

const HeaderComponent = () => {

	const isAuth = isUserLoggedIn()
	const isAdmin = isAdminUser()
	const isStaff = isStaffUser()
	const isCustomer = isCustomerUser()

	function handleLogout() {
		logout()
		navigator('/login')
	}

	const navigator = useNavigate()

	return (
		<div>
			<header style={{ height: "80px" }}>
				<nav className='navbar navbar-expand-md navbar-dark bg-dark w-100 fixed-top'>
					<div>
						<a href='http://localhost:3000' className='navbar-brand'
							style={{ fontWeight: '800', fontSize: '2rem', display: 'flex', alignItems: 'center' }}
						>

							<img
								src='/wolf-head.png'
								alt='Wolf logo'
								style={{ width: '60px', height: '60px', marginRight: '15px' }}
							/>
							WolfCafe
						</a>
					</div>
					<div className='collapse navbar-collapse'>
						<ul className='navbar-nav'>
							{/*STAFF*/}
							{
								isAuth && (isAdmin || isStaff) &&
								<li className='nav-item'>
									<NavLink to='/items' className='nav-link'
										style={{ fontSize: '1.5rem', fontWeight: '600', marginLeft: '3rem' }}
									>Items</NavLink>
								</li>
							}


							{/* ORDER (customers only) */}
							{
							    isCustomer &&
							    <li className='nav-item'>
							        <NavLink to='/order' className='nav-link'
							            style={{ fontSize: '1.5rem', fontWeight: '600', marginLeft: '3rem' }}
							        >Order</NavLink>
							    </li>
							}
										  
							{/*STAFF OR ADMIN*/}
							{
								isAuth && isAdmin &&
								<li className='nav-item'>
									<NavLink to='/staff' className='nav-link'
										style={{ fontSize: '1.5rem', fontWeight: '600', marginLeft: '3rem' }}
									>Staff Directory</NavLink>
								</li>
							}
							{
								isAuth && isAdmin &&
								<li className='nav-item'>
									<NavLink to='/customers' className='nav-link'
										style={{ fontSize: '1.5rem', fontWeight: '600', marginLeft: '3rem' }}
									>Customer Directory</NavLink>
								</li>
							}
							{
								isAuth && (isAdmin || isStaff) &&
								<li className='nav-item'>
									<NavLink to='/ingredients' className='nav-link'
										style={{ fontSize: '1.5rem', fontWeight: '600', marginLeft: '3rem' }}
									>Inventory</NavLink>
								</li>
							}
							{/* ADMIN */}
							{
								isAuth && isAdmin &&
								<li className='nav-item'>
									<NavLink to='/tax-rate' className='nav-link'
										style={{ fontSize: '1.5rem', fontWeight: '600', marginLeft: '3rem' }}
									>Tax Rate</NavLink>
								</li>
							}
						</ul>
					</div>
					<ul className='navbar-nav'>


						{/*FRONT PAGE */}
						{
							!isAuth &&
							<li className='nav-item'>
								<NavLink to='/register' className='nav-link' style={{ fontSize: '1.5rem', fontWeight: '600', marginRight: '3rem' }}
								>Register</NavLink>
							</li>
						}
						{
							!isAuth &&
							<li className='nav-item'>
								<NavLink to='/login' className='nav-link'
									style={{ fontSize: '1.5rem', fontWeight: '600', marginRight: '3rem' }}
								>Login</NavLink>
							</li>
						}


						{/*LOGOUT BUTTON AFTER LOGGED IN*/}
						{
							isAuth &&
							<li className='nav-item'>
								<NavLink to='/login' className='nav-link' onClick={handleLogout}
									style={{ fontSize: '1.5rem', fontWeight: '600', marginRight: '3rem' }}
								>Logout</NavLink>
							</li>
						}

					</ul>
				</nav>
			</header>
		</div>
	)
}

export default HeaderComponent