import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getUserById, updateUser } from "../services/UserService";
import { deleteUser } from "../services/AuthService";

const EditUserComponent = () => {
	const { id } = useParams();
	const navigate = useNavigate();
	const [isCustomer, setIsCustomer] = useState(false);

	const roleMap = {
		1: "ROLE_ADMIN",
		2: "ROLE_STAFF",
		3: "ROLE_BARISTA",
		5: "ROLE_CUSTOMER"
	};

	const reverseRoleMap = {
		ROLE_ADMIN: 1,
		ROLE_STAFF: 2,
		ROLE_BARISTA: 3,
		ROLE_CUSTOMER: 5
	};

	const [user, setUser] = useState({
		name: "",
		username: "",
		email: "",
		password: "",
		roleId: 2,
	});

	const [error, setError] = useState("");
	const [success, setSuccess] = useState("");
	const [deleteMsg, setDeleteMsg] = useState("");

	// Load user
	useEffect(() => {
		getUserById(id)
			.then((res) => {
				const u = res.data;

				const currentRole = u.roles?.[0]?.name || "ROLE_STAFF";
				const roleId = reverseRoleMap[currentRole] || 2;

				// Check if user is CUSTOMER
				if (currentRole === "ROLE_CUSTOMER") {
					setIsCustomer(true);
					setUser({
						name: u.name,
						username: u.username,
						email: u.email,
						password: "",
						roleId: 5,
					});
					return;
				}

				setUser({
					name: u.name,
					username: u.username,
					email: u.email,
					password: "",
					roleId,
				});
			})
			.catch((err) => {
				console.error(err);
				setError("Couldn't load user.");
			});
	}, [id]);

	function handleChange(e) {
		const { name, value } = e.target;
		setUser({ ...user, [name]: value });
	}

	async function saveUser(e) {
		e.preventDefault();

		const payload = {
			name: user.name,
			username: user.username,
			email: user.email,
			password: user.password === "" ? null : user.password,
			roles: [
				{
					id: isCustomer ? 5 : Number(user.roleId),
					name: isCustomer ? "ROLE_CUSTOMER" : roleMap[Number(user.roleId)],
				},
			],
		};


		try {
			await updateUser(id, payload);
			setSuccess("User updated!");

			setTimeout(() => {
				if (isCustomer) {
					navigate("/customers");
				} else {
					navigate("/staff");
				}
			}, 1200);
		} catch (err) {
			console.error(err);
			setError("Failed to update user. Duplicate username/email?");
			setTimeout(() => setError(""), 2000);
		}
	}

	function deleteUserHandler() {
		const confirmDelete = window.confirm("Delete this user?");
		if (!confirmDelete) return;

		deleteUser(id)
			.then(() => {
				setDeleteMsg("User deleted!");
				setTimeout(() => navigate("/staff"), 1200);
			})
			.catch((err) => {
				console.error(err);
				setDeleteMsg("User may already be deleted.");
				setTimeout(() => navigate("/staff"), 1500);
			});
	}

	return (
		<div className="d-flex justify-content-center align-items-start" style={{ paddingTop: "80px" }}>
			<div className="card shadow-lg p-5" style={{ width: "40rem", borderRadius: "1rem" }}>

				{/* Back Button */}
				<button
					type="button"
					className="btn btn-dark"
					onClick={() => navigate("/staff")}
					style={{ position: "absolute", top: "20px", right: "20px" }}
				>
					← Back
				</button>

				<h2 className="text-center mb-4 fw-bold">Edit User</h2>

				{error && <div className="alert alert-danger text-center">{error}</div>}
				{success && <div className="alert alert-success text-center">{success}</div>}
				{deleteMsg && <div className="alert alert-info text-center">{deleteMsg}</div>}

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
						<label className="form-label">Password (leave blank to keep same)</label>
						<input
							type="password"
							name="password"
							className="form-control"
							value={user.password}
							onChange={handleChange}
						/>
					</div>

					{/* Role Selector */}
					{!isCustomer && (
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
					)}

					{isCustomer && (
						<div className="mb-3">
							<label className="form-label">Role</label>
							<input
								disabled
								className="form-control"
								value="Customer"
							/>
						</div>
					)}

					<button type="submit" className="btn btn-success w-100 fw-bold mt-3">
						Update User
					</button>

					<button
						type="button"
						className="btn btn-danger w-100 fw-bold mt-2"
						onClick={deleteUserHandler}
					>
						Delete User
					</button>
				</form>
			</div>
		</div>
	);
};

export default EditUserComponent;
