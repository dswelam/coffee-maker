import React, { useState, useEffect } from 'react'
import { loginAPICall, saveLoggedInUser, storeToken } from '../services/AuthService'
import { useNavigate } from 'react-router-dom'

const LoginComponent = () => {

	const [usernameOrEmail, setUsernameOrEmail] = useState('')
	const [password, setPassword] = useState('')
	const [errorMessage, setErrorMessage] = useState('')
	const navigator = useNavigate()

	useEffect(() => {
		// prevent scrolling
		document.body.style.overflow = 'hidden'

		return () => {
			document.body.style.overflow = 'auto'
		}
	}, [])


	async function handleLoginForm(e) {
		e.preventDefault()

		const loginObj = { usernameOrEmail, password }

		console.log(loginObj)

		await loginAPICall(usernameOrEmail, password).then((response) => {
			console.log(response.data)

			// const token = 'Basic ' + window.btoa(usernameOrEmail + ':' + password);
			const token = 'Bearer ' + response.data.accessToken

			const role = response.data.role

			storeToken(token)
			saveLoggedInUser(usernameOrEmail, role)

			if (role === "ROLE_ADMIN") {
				navigator("/items"); // admin
			} else if (role === "ROLE_STAFF" || role == "ROLE_BARISTA") {
				navigator("/order-queue"); // staff
			} else {
				navigator("/order"); // customers
			}

			window.location.reload(false)
		}).catch(error => {
			console.error('Login error:' + error)

			if (usernameOrEmail.trim() === '') {
				setErrorMessage('Username or email is required');
			} else if (password.trim() === '') {
				setErrorMessage('Password is required');
			} else {
				setErrorMessage('Incorrect username or password');
			}

		})
	}

	return (
		<div
			className='d-flex justify-content-center align-items-center vh-50'
		>
			<div className='card shadow-lg p-5' style={{ width: '45rem', transform: 'scale(0.9)', backgroundColor: '#fff', borderRadius: '1rem' }}>

				<div className='card-header text-center border-0 mb-3 bg-white'>
				<p className='text-secondary mt-2'>New Customer ? Use the Register button above</p>
				<p className='text-secondary mt-2'>Want to Order without making an account ? Use the Order button above</p>

				<div style={{ height: '2rem' }} /> 

				<h2 className='fw-bold mb-0 text-dark'>Welcome</h2>
				<p className='text-secondary mt-2'>Sign in to continue</p>
				</div>

				<div className='card-body'>
					<form>
						<div className='mb-4'>
							<label className='form-label fs-5 fw-semibold text-dark'>Username</label>
							<input
								type='text'
								name='usernameOrEmail'
								className='form-control form-control-lg'
								placeholder='Enter username or email'
								value={usernameOrEmail}
								onChange={(e) => setUsernameOrEmail(e.target.value)}
							/>
						</div>

						<div className='mb-4'>
							<label className='form-label fs-5 fw-semibold text-dark'>Password</label>
							<input
								type='password'
								name='password'
								className='form-control form-control-lg'
								placeholder='Enter password'
								value={password}
								onChange={(e) => setPassword(e.target.value)}
							/>
						</div>

						<div className='text-center'>
							<button
								className='btn btn-danger btn-lg w-100 fw-bold'
								onClick={(e) => handleLoginForm(e)}
								style={{ backgroundColor: '#CC0000' }}
							>
								Login
							</button>
						</div>
						{errorMessage && (
							<div className='alert alert-danger mt-4 text-center fs-5 py-3' role='alert'>
								{errorMessage}
							</div>
						)}

					</form>
				</div>
			</div>

		</div>
	)
}

export default LoginComponent