import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { registerAPICall } from '../services/AuthService'

const RegisterComponent = () => {

	const [name, setName] = useState('')
	const [username, setUsername] = useState('')
	const [email, setEmail] = useState('')
	const [password, setPassword] = useState('')

	const [errorMessage, setErrorMessage] = useState('')
	const [successMessage, setSuccessMessage] = useState('')

<<<<<<< HEAD
	const navigate = useNavigate()
=======
>>>>>>> b116f371db3e6195d8eb8851c6ab9496f905ba12

	function handleRegistrationForm(e) {
	    e.preventDefault()

	    const register = {
	        name,
	        username,
	        email,
	        password
	    }

	    registerAPICall(register)
	        .then((response) => {
	            console.log(response.data)

	            setSuccessMessage("Account created successfully!")

	            // hide after 3 seconds and redirect
	            setTimeout(() => {
	                setSuccessMessage('')
	                navigate('/login')
	            }, 3000)
	        })
	        .catch((error) => {
	            console.error(error)
	            setErrorMessage('Registration failed. Please check your inputs.')
	        })
	}

	return (
		<div className='d-flex justify-content-center align-items-center vh-100' style={{ paddingTop: '20px' }}>
			<div
				className='card shadow-lg p-5'
				style={{ width: '45rem', transform: 'scale(0.9)', backgroundColor: '#fff', borderRadius: '1rem' }}
			>
				<div className='card-header text-center border-0 mb-3 bg-white'>
					<h2 className='fw-bold mb-0 text-dark'>Create Account</h2>
				</div>

				<div className='card-body'>
					<form>
						<div className='mb-4'>
							<label className='form-label fs-5 fw-semibold text-dark'>Name</label>
							<input
								type='text'
								className='form-control form-control-lg'
								placeholder='Enter name'
								value={name}
								onChange={(e) => setName(e.target.value)}
							/>
						</div>

						<div className='mb-4'>
							<label className='form-label fs-5 fw-semibold text-dark'>Username</label>
							<input
								type='text'
								className='form-control form-control-lg'
								placeholder='Enter username'
								value={username}
								onChange={(e) => setUsername(e.target.value)}
							/>
						</div>

						<div className='mb-4'>
							<label className='form-label fs-5 fw-semibold text-dark'>Email</label>
							<input
								type='email'
								className='form-control form-control-lg'
								placeholder='Enter email'
								value={email}
								onChange={(e) => setEmail(e.target.value)}
							/>
						</div>

						<div className='mb-4'>
							<label className='form-label fs-5 fw-semibold text-dark'>Password</label>
							<input
								type='password'
								className='form-control form-control-lg'
								placeholder='Enter password'
								value={password}
								onChange={(e) => setPassword(e.target.value)}
							/>
						</div>

						{errorMessage && (
							<div className='alert alert-danger mt-4 text-center fs-5 py-3'>
								{errorMessage}
							</div>
						)}
						
						<div className='text-center'>
							<button
								className='btn btn-danger btn-lg w-100 fw-bold'
								onClick={handleRegistrationForm}
								style={{ backgroundColor: '#CC0000' }}
							>
								Register
							</button>
						</div>
<<<<<<< HEAD

						{errorMessage && (
							<div className='alert alert-danger mt-4 text-center fs-5 py-3'>
								{errorMessage}
							</div>
						)}

						{successMessage && (
							<div className='alert alert-success mt-4 text-center fs-5 py-3'>
								{successMessage}
							</div>
						)}
=======
>>>>>>> b116f371db3e6195d8eb8851c6ab9496f905ba12
					</form>
				</div>
			</div>
		</div>
	)
}

export default RegisterComponent
