import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { addUser } from "../services/AuthService";

const UserComponent = () => {
	const navigate = useNavigate();

	const [user, setUser] = useState({
		name: "",
		username: "",
		email: "",
		password: "",
		roleId: 2 // default STAFF
	});

	const [error, setError] = useState("");
	const [success, setSuccess] = useState("");

	function handleChange(e) {
		const { name, value } = e.target;
		setUser({ ...user, [name]: value });
	}

	function saveUser(e) {
		e.preventDefault();

		const roleMap = {
			1: "ROLE_ADMIN",
			2: "ROLE_STAFF",
			3: "ROLE_BARISTA"
		};

		const payload = {
			name: user.name,
			username: user.username,
			email: user.email,
			password: user.password,
			roles: [
				{
					id: Number(user.roleId),
					name: roleMap[Number(user.roleId)] || null
				}
			]
		};

		addUser(payload)
			.then(() => {
				setSuccess("User created!");
				setTimeout(() => navigate("/staff"), 1200);
			})
			.catch((err) => {
				console.error(err);
				setError("Failed to create user. Check fields or duplicate username.");
				setTimeout(() => setError(""), 2000);
			});
	}

	return (
		<div className="d-flex justify-content-center align-items-center vh-100"
			style={{ paddingTop: "120px", minHeight: "100vh" }}>
			<div className="card shadow-lg p-5" style={{ width: "40rem", borderRadius: "1rem" }}>
				<h2 className="text-center mb-4 fw-bold">Create New User</h2>

				<form onSubmit={saveUser}>
					<div className="mb-3">
						<label className="form-label">Name</label>
						<input
							type="text"
							name="name"
							className="form-control"
							value={user.name}
							onChange={handleChange}
							required
						/>
					</div>

					<div className="mb-3">
						<label className="form-label">Username</label>
						<input
							type="text"
							name="username"
							className="form-control"
							value={user.username}
							onChange={handleChange}
							required
						/>
					</div>

					<div className="mb-3">
						<label className="form-label">Email</label>
						<input
							type="email"
							name="email"
							className="form-control"
							value={user.email}
							onChange={handleChange}
							required
						/>
					</div>

					<div className="mb-3">
						<label className="form-label">Password</label>
						<input
							type="password"
							name="password"
							className="form-control"
							value={user.password}
							onChange={handleChange}
							required
						/>
					</div>

					<div className="mb-3">
						<label className="form-label">Role</label>
						<select
							name="roleId"
							className="form-select"
							value={user.roleId}
							onChange={handleChange}
						>
							<option value={1}>Admin</option>
							<option value={2}>Staff</option>
							<option value={3}>Barista</option>
						</select>
					</div>

					<button type="submit" className="btn btn-primary w-100 mt-3">
						Create User
					</button>

					<button
						type="button"
						className="btn btn-secondary w-100 mt-2"
						onClick={() => navigate("/staff")}
					>
						Cancel
					</button>

					{error && (
						<div className="alert alert-danger mt-3 text-center">{error}</div>
					)}
					{success && (
						<div className="alert alert-success mt-3 text-center">{success}</div>
					)}
				</form>
			</div>
		</div>
	);
};

export default UserComponent;
